package chatting_client;

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
import javax.swing.DropMode;
import java.awt.Panel;
import java.awt.Color;
import java.awt.Button;
import javax.swing.JTextPane;

public class Chatting_client extends JFrame implements ActionListener {

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

					btn_act1.setEnabled(true);
					btn_act2.setEnabled(true);
					btn_act3.setEnabled(true);
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
									btn_act1.setEnabled(false);
									btn_act2.setEnabled(false);
									btn_act3.setEnabled(false);
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
	public void FromServerListener(String str){
		
	}

	public void textappend(String str) {
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Chatting_client frame = new Chatting_client();
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
	public Chatting_client() {
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
		text_ID.setText("wodms");
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

		JLabel lblNewLabel_2 = new JLabel("SW1");
		lblNewLabel_2.setBounds(442, 201, 57, 15);
		contentPane.add(lblNewLabel_2);

		JLabel lblSw = new JLabel("SW2");
		lblSw.setBounds(511, 201, 57, 15);
		contentPane.add(lblSw);

		JLabel lblSw_1 = new JLabel("SW3");
		lblSw_1.setBounds(583, 201, 57, 15);
		contentPane.add(lblSw_1);

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
			int index;
			if(line.startsWith("SW")){
				index = Integer.parseInt(line.substring(2,3));
				switch(index){
				case 1:
					textFieldSw_1.setText(line.substring(3));
					break;
				case 2:
					textFieldSw_2.setText(line.substring(3));
					break;
				case 3:
					textFieldSw_3.setText(line.substring(3));
					break;
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
