package chattingServer;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class chattingServer extends JFrame {
	private JPanel contentPane;
	private JTextField textField_port;
	private JTextField textField_msg;
	private JTextArea textArea = new JTextArea();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					chattingServer frame = new chattingServer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}
	protected void createContents() { 
		
	} 
	public void server_set() throws InterruptedException {
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				int port = Integer.parseInt(textField_port.getText());
				// --- 메인 스레드
				try {
					// 1) 서버소켓 생성 후 클라이언트 접속 요청 Listening
					ServerSocket s_sock = new ServerSocket(port);
					textArea.append(port + " 서버 Listening....\n");

					// 2) 스레드간 공유하게 될 HashMap 객체 생성
					HashMap hm = new HashMap();
					
					while (true) {
						// 클라이언트의 접속을 기다림
						Socket c_sock = s_sock.accept();

						// 3) 클라이언트의 접속 요청이 있을 경우 해당 요청에 대한 처리를 위한
						// 자식 스레드 생성 ? ChatThread 실행
						// 이때, 클라이언트와의 통신을 위한 소켓객체와 HashMap 객체를
						// 스레드에 전달
						ChatThread chat_thread = new ChatThread(c_sock, hm);

						// 4) 자식 스레드 객체 start
						chat_thread.start();
					}
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		});
		t1.start();
	}

	/**
	 * Create the frame.
	 */
	public chattingServer() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnServer = new JButton("\uC11C\uBC84");
		btnServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					server_set();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		btnServer.setBounds(12, 10, 97, 23);
		contentPane.add(btnServer);

		textField_port = new JTextField();
		textField_port.setText("7000");
		textField_port.setBounds(121, 11, 200, 21);
		contentPane.add(textField_port);
		textField_port.setColumns(10);

		JButton btnExit = new JButton("\uC885\uB8CC");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnExit.setBounds(333, 10, 97, 23);
		contentPane.add(btnExit);

		textArea.setBounds(12, 43, 410, 177);
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
	}
}

// 클라이언트와 실질적 통신을 위한 스레드(자식 스레드)
class ChatThread extends Thread {
	private Socket sock;
	private String id;
	private BufferedReader br;
	private HashMap hm;
	private boolean initFlag = false;
	
	// 생성자(Socket 객체와 HashMap 객체를 전달 받음)
	public ChatThread(Socket sock, HashMap hm) {
		this.sock = sock;
		this.hm = hm;
		try {
			// 클라이언트에 write를 위한 스트림객체 생성
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));

			// 클라이언트 메시지를 read 하기 위한 스트림객체 생성
			br = new BufferedReader(new InputStreamReader(sock.getInputStream()));

			// 클라이언트가 입력한 id 값 read
			id = br.readLine();
			// read 한 id 값을 다른 모든 클라이언트에 브로드캐스트
			broadcast(id + "님이 접속했습니다.");

			// 서버화면에 방금 접속한 클라이언트 id 표시
			System.out.println("접속한 사용자 아이디 : " + id);

			// HashMap에 id를 key 로, OutputStream 객체(PrintWriter)를 value로 하여 저장
			// (1) HashMap에 클라이언트의 PrintWriter 객체를 저장하는 이유 :
			// BufferedReader로 서버가 읽은 문자열을 HashMap에 저장된 모든
			// PrintWriter를 이용하여 write 하기 위함.
			// (2) synchronized 시키는 이유 :
			// 여러 스레드가 HashMap을 공유하므로 HashMap에 있는 자료를
			// 동시에 접근하게 하기 위함.
			synchronized (hm) {
				hm.put(this.id, pw);
			}

			initFlag = true; // 초기화 완성
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// 자식 스레드 실행 함수 정의
	public void run() {
		try {
			String line = null;

			// 클라이언트가 보내는 메시지를 한 라인씩 읽어 broadcast 함수에 의해
			// 모든 클라이언트에 전송
			while ((line = br.readLine()) != null) {
				// 만약 /quit 이면 while 문 빠져나옴
				if (line.equals("/quit"))
					break;

				// 만약 /to 이면 특정 id의 클라이언트에게만 전송됨
				if (line.indexOf("/to ") == 0) {
					sendmsg(line);
				} else {
					broadcast(id + " : " + line);
				}
			}
		} catch (Exception e) {
			System.out.println(e);

			// 클라이언트가 /quit에 의해 종료하였거나, 강제 종료하였다면 finally 구문 실행
			// HashMap에 현재 스레드의 id에 해당하는 정보 삭제 후 접속종료 메시지를
			// 나머지 클라이언트에 보낸다.
			// 그리고 socket 닫는다.
		} finally {
			synchronized (hm) {
				hm.remove(id);
			}
			broadcast(id + " 님이 접속 종료하였습니다.");
			try {
				if (sock != null)
					sock.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 특정 아이디에게 문자열 전송을 위한 함수
	// 형식 "/to id 전달_메시지"
	public void sendmsg(String msg) {
		// 문자열 중 처음 공백보다 1큰 값을 return
		int start = msg.indexOf(" ") + 1;

		// start번째 index에서 시작하여 처음 공백의 index값 return
		int end = msg.indexOf(" ", start);
		if (end != -1) {
			// start~end 인덱스의 문자열 가져옴(id 가져옴)
			String to = msg.substring(start, end);
			String msg2 = msg.substring(end + 1); // 전달할 메시지 가져옴
			Object obj = hm.get(to); // HashMap에서 해당 id의 객체 값 가져옴
			if (obj != null) {
				PrintWriter pw = (PrintWriter) obj;
				pw.println(id + " 님이 귓속말을 보내셧습니다. : " + msg2);
				pw.flush();
			}
		}
	}

	// 브로드 캐스트 메소드 : 접속한 모든 클라이언트에게 문자열을 전송하는 메소드
	// 전송할 문자열을 인자로 전달받는다.
	public void broadcast(String msg) {
		synchronized (hm) {
			// HashMap에 저장된 각각의 클라이언트의 pw 객체를 객체 배열(Collection)에
			// 가져온다. value()는 HashMap에 포함된 값들을 Collection 객체로 return.
			Collection collection = hm.values();

			// Collection의 요소들을 반복하여 가지고 오기 위해 Iterator 객체 생성한 후
			// Iterator를 통하여 모든 pw 객체를 가져와 모든 클라이언트에 메시지 전송
			Iterator iter = collection.iterator();
			while (iter.hasNext()) {
				PrintWriter pw = (PrintWriter) iter.next();
				pw.println(msg);
				pw.flush();
			}
		}
	}
}
