package raspberry_client;

import java.awt.Button;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class Raspberry_client extends JFrame implements ActionListener {
	private final boolean ON = true;
	private final boolean OFF = false;
	private JPanel contentPane;
	private JTextField textField_port;
	private JTextField textField_msg;
	private JTextArea textArea = new JTextArea();
	private JTextField textField_ip;
	private JTextField text_ID;
	private boolean send = false;
	private boolean connect = false;
	private JTextField textFieldSw_1;
	private JTextField textFieldSw_2;
	private JTextField textFieldSw_3;
	private JTextField textFieldact_1;
	private JTextField textFieldact_2;
	private JTextField textFieldact_3;
	private JButton btnClient;
	private Button btn_act1;
	private Button btn_act2;
	private Button btn_act3;
	private JButton btnSend;
	private JButton btnClose;
	private Button btn_SW1;
	private Button btn_SW2;
	private Button btn_SW3;
	private boolean[] SW_flag = {OFF,OFF,OFF};

	final GpioController gpio = GpioFactory.getInstance();
	final GpioPinDigitalOutput[] pin = new GpioPinDigitalOutput[6];
	{
		pin[0] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22, "MyLED0",PinState.HIGH);
		pin[1] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "MyLED1",PinState.HIGH);
		pin[2] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "MyLED2",PinState.HIGH);
		pin[3] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "MyLED3",PinState.HIGH);
		pin[4] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, "MyLED4",PinState.HIGH);
		pin[5] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, "MyLED5",PinState.HIGH);
	}
	
	final GpioPinDigitalInput[] mySwitch = new GpioPinDigitalInput[3];
	{
			mySwitch[0] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07, PinPullResistance.PULL_DOWN);
			mySwitch[1] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_DOWN);
			mySwitch[2] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_16, PinPullResistance.PULL_DOWN);
	}


	public void switchListener(){
		mySwitch[0].addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				System.out.println("GPIO PIN STATE CHANGE: " + event.getPin()+ " = " + event.getState());
				if (event.getState() == PinState.LOW){
					textFieldSw_1.setText("ON");
					sendMessage("SW1ON");
				}else{
					textFieldSw_1.setText("OFF");
					sendMessage("SW1OFF");
				}
			}
		});
		mySwitch[1].addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				System.out.println("GPIO PIN STATE CHANGE: " + event.getPin()+ " = " + event.getState());
				if (event.getState() == PinState.LOW){
					textFieldSw_2.setText("ON");
					sendMessage("SW2ON");
				}else{
					textFieldSw_2.setText("OFF");
					sendMessage("SW2OFF");
				}
			}
		});
		mySwitch[2].addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				System.out.println("GPIO PIN STATE CHANGE: " + event.getPin()+ " = " + event.getState());
				if (event.getState() == PinState.LOW){
					textFieldSw_3.setText("ON");
					sendMessage("SW3ON");
				}else{
					textFieldSw_3.setText("OFF");
					sendMessage("SW3OFF");
				}
			}
		});
	}
	public void sendMessage(String str){
		textField_msg.setText(str);
		send = true;
	}
	public void pattern1() throws InterruptedException {
		System.out.println("Pattern1 On");
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				int cnt = 2;
				while (cnt-- > 0) {
					pin[0].low();
					for (int i = 1; i < 6; i++) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						pin[i - 1].high();
						pin[i].low();
					}

					for (int i = 5; i > 0; i--) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						pin[i - 1].low();
						pin[i].high();
					}
				}
				textFieldact_1.setText("");
				sendMessage("Action1OFF");
			}
		});
		t1.start();
	}

	public void pattern2() throws InterruptedException {
		System.out.println("Pattern2 On");
		Thread t2 = new Thread(new Runnable(){
			public void run(){
				int cnt = 6;
				for (int i = 0; i < 3; i++) {
					pin[i].low();
				}
				for (int i = 3; i < 6; i++) {
					pin[i].high();
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				while (cnt-- > 0) {
					for (int i = 0; i < 6; i++) {
						pin[i].toggle();
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				textFieldact_2.setText("");
				sendMessage("Action2OFF");
			}
		});
		t2.start();
	}

	public void pattern3() throws InterruptedException {
		System.out.println("Pattern3 On");
		Thread t3 = new Thread(new Runnable() {
			public void run() {
				int cnt = 6;
				for (int i = 0; i < 6; i++) {
					pin[i].low();
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				while (cnt-- > 0) {
					for (int i = 0; i < 6; i++) {
						pin[i].toggle();
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				textFieldact_3.setText("");
				sendMessage("Action3OFF");
			}
		});
		t3.start();
	}

	/**
	 * Launch the application.
	 */
	public void server_client() throws InterruptedException {
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				connect = true;
				Socket sock = null;

				// 호스트명, 포트번호 입력 받기 위한 객체
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

				// 서버로 부터 읽어오기 위한 입력 스트림 객체
				BufferedReader readFromServer = null;

				// 서버로 전달할 출력 스트림 객체
				PrintWriter pw = null;

				boolean endflag = false;

				try {
					String host = textField_ip.getText();
					int port = Integer.parseInt(textField_port.getText());

					// 소켓 객체 생성(socket open)
					sock = new Socket(host, port);

					// 스트림 객체 생성
					pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
					readFromServer = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));

					// 사용자 id 입력
					String id = text_ID.getText();

					// 입력된 사용자 id를 서버로 전달
					pw.println(id);
					pw.flush();
					textappend("접속 완료.. id : " + id);

					btn_SW1.setEnabled(true);
					btn_SW2.setEnabled(true);
					btn_SW3.setEnabled(true);
					btnClient.setEnabled(false);
					// 자식스레드 시작
					// 자식스레드는 서버가 전달하는 문자열을 읽어와 화면(표준 출력)에
					// 출력시키는 역할을 한다.
					ReadFromServerThread thread = new ReadFromServerThread(sock, readFromServer);
					thread.start();

					// 메인스레드
					// 메인스레드는 전달할 메시지를 키보드(표준입력)로 입력받아 서버로 출력
					// 시키는 역할
					String line = null;
					while (connect) {
						line = textField_msg.getText();
						if (send) {
							if (!line.equals("")) {
								pw.println(line);
								pw.flush();
								textField_msg.setText("");
								send = false;
								if (line.equals("/quit")) {
									endflag = true;
									btn_SW1.setEnabled(false);
									btn_SW2.setEnabled(false);
									btn_SW3.setEnabled(false);
									btnClient.setEnabled(true);
									break;
								}
							}else{
								line = null;
							}
						}
					}
					textappend("클라이언트 접속을 종료합니다.");

				} catch (Exception e) {
					if (!endflag) {
						e.printStackTrace();
						textappend("접속실패...");
					}
				} finally {
					connect = false;
					btnClient.setEnabled(true);
					try {
						if (pw != null)
							pw.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (readFromServer != null)
							readFromServer.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (sock != null)
							sock.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		t1.start();
	}

	public void textappend(String str) {
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Raspberry_client frame = new Raspberry_client();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Raspberry_client() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 687, 379);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// 버튼 객체
		btnClient = new JButton("\uC11C\uBC84 \uC811\uC18D");
		btnClient.addActionListener(this);
		btnClient.setBounds(12, 14, 97, 32);
		contentPane.add(btnClient);

		btnSend = new JButton("\uC804\uC1A1");
		btnSend.addActionListener(this);
		btnSend.setBounds(333, 307, 97, 23);
		contentPane.add(btnSend);

		btnClose = new JButton("\uC811\uC18D \uC885\uB8CC");
		btnClose.addActionListener(this);
		btnClose.setBounds(12, 61, 97, 32);
		contentPane.add(btnClose);

		btn_act1 = new Button("Action1");
		btn_act1.setEnabled(false);
		btn_act1.addActionListener(this);
		btn_act1.setBounds(442, 8, 217, 23);
		contentPane.add(btn_act1);

		btn_act2 = new Button("Action2");
		btn_act2.setEnabled(false);
		btn_act2.addActionListener(this);
		btn_act2.setBounds(442, 70, 217, 23);
		contentPane.add(btn_act2);

		btn_act3 = new Button("Action3");
		btn_act3.setEnabled(false);
		btn_act3.addActionListener(this);
		btn_act3.setBounds(442, 139, 217, 23);
		contentPane.add(btn_act3);

		// 텍스트 필드
		textField_port = new JTextField();
		textField_port.setText("7000");
		textField_port.setBounds(160, 41, 262, 21);
		contentPane.add(textField_port);
		textField_port.setColumns(10);
		textArea.setEditable(false);

		textField_msg = new JTextField();
		textField_msg.setBounds(12, 308, 309, 21);
		contentPane.add(textField_msg);
		textField_msg.setColumns(10);

		textField_ip = new JTextField();
		textField_ip.setText("192.168.162.92");
		textField_ip.setBounds(160, 10, 262, 21);
		contentPane.add(textField_ip);
		textField_ip.setColumns(10);

		text_ID = new JTextField();
		text_ID.setText("rasberry");
		text_ID.setBounds(160, 72, 262, 21);
		contentPane.add(text_ID);
		text_ID.setColumns(10);

		textFieldSw_1 = new JTextField();
		textFieldSw_1.setEditable(false);
		textFieldSw_1.setBounds(434, 226, 49, 21);
		contentPane.add(textFieldSw_1);
		textFieldSw_1.setColumns(10);

		textFieldSw_2 = new JTextField();
		textFieldSw_2.setEditable(false);
		textFieldSw_2.setBounds(503, 226, 49, 21);
		contentPane.add(textFieldSw_2);
		textFieldSw_2.setColumns(10);

		textFieldSw_3 = new JTextField();
		textFieldSw_3.setEditable(false);
		textFieldSw_3.setColumns(10);
		textFieldSw_3.setBounds(575, 226, 49, 21);
		contentPane.add(textFieldSw_3);

		textFieldact_1 = new JTextField();
		textFieldact_1.setEditable(false);
		textFieldact_1.setColumns(10);
		textFieldact_1.setBounds(441, 41, 218, 21);
		contentPane.add(textFieldact_1);

		textFieldact_2 = new JTextField();
		textFieldact_2.setEditable(false);
		textFieldact_2.setColumns(10);
		textFieldact_2.setBounds(441, 105, 218, 21);
		contentPane.add(textFieldact_2);

		textFieldact_3 = new JTextField();
		textFieldact_3.setEditable(false);
		textFieldact_3.setColumns(10);
		textFieldact_3.setBounds(441, 168, 218, 21);
		contentPane.add(textFieldact_3);

		// 스크롤
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(12, 103, 410, 190);
		this.getContentPane().add(scrollPane);

		// 라벨
		JLabel lblNewLabel = new JLabel("I      P");
		lblNewLabel.setBounds(121, 14, 40, 15);
		contentPane.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("PORT");
		lblNewLabel_1.setBounds(121, 42, 40, 15);
		contentPane.add(lblNewLabel_1);

		JLabel lblId = new JLabel("I      D");
		lblId.setBounds(121, 75, 57, 15);
		contentPane.add(lblId);
		
		btn_SW1 = new Button("SW 1");
		btn_SW1.addActionListener(this);
		btn_SW1.setEnabled(false);
		btn_SW1.setBounds(434, 197, 49, 23);
		contentPane.add(btn_SW1);
		
		btn_SW2 = new Button("SW 2");
		btn_SW2.addActionListener(this);
		btn_SW2.setEnabled(false);
		btn_SW2.setBounds(503, 197, 49, 23);
		contentPane.add(btn_SW2);
		
		btn_SW3 = new Button("SW 3");
		btn_SW3.addActionListener(this);
		btn_SW3.setEnabled(false);
		btn_SW3.setBounds(575, 197, 49, 23);
		contentPane.add(btn_SW3);

		// 키 이벤트
		Action ok = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(textField_msg.getText() != ""){
					send = true;
				}
			}
		};
		KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
		textField_msg.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, "ENTER");
		textField_msg.getActionMap().put("ENTER", ok);
		
		switchListener();
	}

	// 이벤트 처리
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource().equals(btnClient)) {
			try {
				server_client();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else if (arg0.getSource().equals(btnSend)) {
		} else if (arg0.getSource().equals(btnClose)) {
			textField_msg.setText("/quit");
		} else if (arg0.getSource().equals(btn_act1)) {
			textField_msg.setText("cmd1");
			textFieldact_1.setText("Action1 On");
		} else if (arg0.getSource().equals(btn_act2)) {
			textField_msg.setText("cmd2");
			textFieldact_2.setText("Action2 On");
		} else if (arg0.getSource().equals(btn_act3)) {
			textField_msg.setText("cmd3");
			textFieldact_3.setText("Action3 On");
		} else if (arg0.getSource().equals(btn_SW1)) {
			if(SW_flag[0] == OFF){
				textField_msg.setText("SW1ON");
				textFieldSw_1.setText("ON");
				SW_flag[0] = ON;
			}else{
				textField_msg.setText("SW1OFF");
				textFieldSw_1.setText("OFF");
				SW_flag[0] = OFF;
			}
		}else if (arg0.getSource().equals(btn_SW2)) {
			if(SW_flag[1] == OFF){
				textField_msg.setText("SW2ON");
				textFieldSw_2.setText("ON");
				SW_flag[1] = ON;
			}else{
				textField_msg.setText("SW2OFF");
				textFieldSw_2.setText("OFF");
				SW_flag[1] = OFF;
			}
		}else if (arg0.getSource().equals(btn_SW3)) {
			if(SW_flag[2] == OFF){
				textField_msg.setText("SW3ON");
				textFieldSw_3.setText("ON");
				SW_flag[2] = ON;
			}else{
				textField_msg.setText("SW3OFF");
				textFieldSw_3.setText("OFF");
				SW_flag[2] = OFF;
			}
		}
		if(connect && textField_msg.getText() != ""){
			send = true;
		}else{
			textField_msg.setText("");
		}
	}

	// 자식 스레드 클래스
	// 서버가 전달하는 문자열을 읽어와 화면(표준 출력)에 출력시키는 역할을 한다.
	class ReadFromServerThread extends Thread {
		private Socket sock = null;
		private BufferedReader readFromServer = null;

		// 생성자
		public ReadFromServerThread(Socket sock, BufferedReader readFromServer) {
			this.sock = sock;
			this.readFromServer = readFromServer;
		}

		public void run() {
			try {
				String line = null;
				while ((line = readFromServer.readLine()) != null) {
					FromServerListener(line);
				}
			} catch (Exception e) {
				textappend("소켓 종료 되었습니다.");
				connect = false;
			} finally {
				try {
					if (readFromServer != null)
						readFromServer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if (sock != null)
						sock.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		private void FromServerListener(String line) {
			if(line.startsWith("cmd1")){
				try {
					textFieldact_1.setText(line.substring(4));
					pattern1();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}else if(line.startsWith("cmd2")){
				try {
					textFieldact_2.setText(line.substring(4));
					pattern2();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}else if(line.startsWith("cmd3")){
				try {
					textFieldact_3.setText(line.substring(4));
					pattern3();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else{
				textappend(line);
			}
		}

		public void textappend(String str) {
			textArea.append(str + "\n");
			textArea.setCaretPosition(textArea.getDocument().getLength());
		}
	}
}
