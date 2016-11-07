package chatting_client;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

public class Chatting_client extends JFrame {
	
	private JPanel contentPane;
	private JTextField textField_port;
	private JTextField textField_msg;
	private JTextArea textArea = new JTextArea();
	private JTextField textField_ip;
	
	/**
	 * Launch the application.
	 */
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
	    Socket sock = null;
	    
	    //호스트명, 포트번호 입력 받기 위한 객체
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		    
	    //서버로 부터 읽어오기 위한 입력 스트림 객체
	    BufferedReader readFromServer = null;
		    
	    //서버로 전달할 출력 스트림 객체
	    PrintWriter pw = null;
	    
	    boolean endflag = false;
		    
	    try{
	        // host, port 입력
	    	System.out.println("호스트명 입력 : ");    	   
	        String host = br.readLine();
	        System.out.println("포트번호 입력 : ");
	        int port = Integer.parseInt(br.readLine());

	        // 소켓 객체 생성(socket open)
	        sock = new Socket(host,port);
	    	System.out.println("접속 완료.. socket opened....");  
	    	
	    	//스트림 객체 생성
	    	pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(),"UTF-8"));
	    	readFromServer = new BufferedReader(new InputStreamReader(sock.getInputStream(),"UTF-8"));
	    	
	    	//사용자 id 입력
	    	System.out.println("사용할 id 입력 : ");
	    	String id = br.readLine();
	    	
	    	//입력된 사용자 id를 서버로 전달
	    	pw.println(id);
	    	pw.flush();
	    	
	    	//자식스레드 시작
	    	//자식스레드는 서버가 전달하는 문자열을 읽어와 화면(표준 출력)에 
//출력시키는 역할을 한다.
	    	ReadFromServerThread thread = new ReadFromServerThread(sock, readFromServer);
	    	thread.start();
	    	
	    	//메인스레드
	    	//메인스레드는 전달할 메시지를 키보드(표준입력)로 입력받아 서버로 출력
//시키는 역할
	    	String line = null;
	    	while((line = br.readLine()) != null){
	    		pw.println(line);
	    		pw.flush();
	    		if(line.equals("/quit")){
	    			endflag = true;
	    			break;
	    		}
	    	}
	    	System.out.println("클라이언트 접속을 종료합니다.");

	    }catch(Exception e){
	    	if(!endflag) { 		
	    		e.printStackTrace();
	    	}
	    }finally{
	    	try {
		    if(pw != null) pw.close();
		}catch (Exception e) {				
		    e.printStackTrace();
		}
		try{
		    if(readFromServer != null) readFromServer.close(); 
		}catch(Exception e){				
		    e.printStackTrace();
		}
                try{
                    if(sock != null) sock.close();
                }catch(Exception e){          	
                    e.printStackTrace();    	 
                }
	    }
	}

	/**
	 * Create the frame.
	 */
	public Chatting_client() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnClient = new JButton("\uC811\uC18D");
		btnClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnClient.setBounds(12, 10, 97, 52);
		contentPane.add(btnClient);

		textField_port = new JTextField();
		textField_port.setText("7000");
		textField_port.setBounds(160, 41, 161, 21);
		contentPane.add(textField_port);
		textField_port.setColumns(10);

		JButton btnExit = new JButton("\uC885\uB8CC");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnExit.setBounds(333, 10, 97, 52);
		contentPane.add(btnExit);

		textArea.setBounds(12, 72, 410, 148);
		contentPane.add(textArea);

		textField_msg = new JTextField();
		textField_msg.setBounds(12, 230, 309, 21);
		contentPane.add(textField_msg);
		textField_msg.setColumns(10);

		JButton btnSend = new JButton("\uC804\uC1A1");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = textField_msg.getText();
				
			}
		});
		btnSend.setBounds(333, 229, 97, 23);
		contentPane.add(btnSend);
		
		textField_ip = new JTextField();
		textField_ip.setText("192.168.162.105");
		textField_ip.setBounds(160, 10, 161, 21);
		contentPane.add(textField_ip);
		textField_ip.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("IP");
		lblNewLabel.setBounds(121, 14, 29, 15);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("PORT");
		lblNewLabel_1.setBounds(121, 42, 40, 15);
		contentPane.add(lblNewLabel_1);
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
				System.out.println(line);
			}
		} catch (Exception e) {
			System.out.println("소켓 종료 되었습니다.");
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
}