package chattingServer;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
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
	private JTextArea textArea = new JTextArea();
	private ServerSocket s_sock;
	private boolean socketflag = true;
	
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
	
	public void server_set() throws InterruptedException {
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				int port = Integer.parseInt(textField_port.getText());
				// --- ���� ������
				try {
					// 1) �������� ���� �� Ŭ���̾�Ʈ ���� ��û Listening
					s_sock = new ServerSocket(port);
					textArea.append(port + " ���� Listening....\n");

					// 2) �����尣 �����ϰ� �� HashMap ��ü ����
					HashMap hm = new HashMap();
					
					while (socketflag) {
						// Ŭ���̾�Ʈ�� ������ ��ٸ�
						Socket c_sock = s_sock.accept();

						// 3) Ŭ���̾�Ʈ�� ���� ��û�� ���� ��� �ش� ��û�� ���� ó���� ����
						// �ڽ� ������ ���� ? ChatThread ����
						// �̶�, Ŭ���̾�Ʈ���� ����� ���� ���ϰ�ü�� HashMap ��ü��
						// �����忡 ����
						ChatThread chat_thread = new ChatThread(c_sock, hm);

						// 4) �ڽ� ������ ��ü start
						chat_thread.start();
					}
				} catch (Exception e) {
					textArea.append(port + "���� ����....\n");
					socketflag = false;
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
					socketflag = true;
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
				try {
					s_sock.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnExit.setBounds(333, 10, 97, 23);
		contentPane.add(btnExit);

		textArea.setBounds(12, 43, 410, 208);
		contentPane.add(textArea);
	}
	
	// Ŭ���̾�Ʈ�� ������ ����� ���� ������(�ڽ� ������)
	public class ChatThread extends Thread {
		private Socket sock;
		private String id;
		private BufferedReader br;
		private HashMap hm;
		private boolean initFlag = false;

		// ������(Socket ��ü�� HashMap ��ü�� ���� ����)
		public ChatThread() {
			
		}
		public ChatThread(Socket sock, HashMap hm) {
			this.sock = sock;
			this.hm = hm;
			try {
				// Ŭ���̾�Ʈ�� write�� ���� ��Ʈ����ü ����
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));

				// Ŭ���̾�Ʈ �޽����� read �ϱ� ���� ��Ʈ����ü ����
				br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				
				// Ŭ���̾�Ʈ�� �Է��� id �� read
				id = br.readLine();
				// read �� id ���� �ٸ� ��� Ŭ���̾�Ʈ�� ��ε�ĳ��Ʈ
				broadcast(id + "���� �����߽��ϴ�.");

				// ����ȭ�鿡 ��� ������ Ŭ���̾�Ʈ id ǥ��
				System.out.println("������ ����� ���̵� : " + id);
				textArea.append("������ ����� ���̵� : " + id + "\n");
				
				// HashMap�� id�� key ��, OutputStream ��ü(PrintWriter)�� value�� �Ͽ� ����
				// (1) HashMap�� Ŭ���̾�Ʈ�� PrintWriter ��ü�� �����ϴ� ���� :
				// BufferedReader�� ������ ���� ���ڿ��� HashMap�� ����� ���
				// PrintWriter�� �̿��Ͽ� write �ϱ� ����.
				// (2) synchronized ��Ű�� ���� :
				// ���� �����尡 HashMap�� �����ϹǷ� HashMap�� �ִ� �ڷḦ
				// ���ÿ� �����ϰ� �ϱ� ����.
				synchronized (hm) {
					hm.put(this.id, pw);
				}

				initFlag = true; // �ʱ�ȭ �ϼ�
			} catch (Exception e) {
				System.out.println(e);
			}
		}

		// �ڽ� ������ ���� �Լ� ����
		public void run() {
			try {
				String line = null;

				// Ŭ���̾�Ʈ�� ������ �޽����� �� ���ξ� �о� broadcast �Լ��� ����
				// ��� Ŭ���̾�Ʈ�� ����
				while ((line = br.readLine()) != null && socketflag) {
					// ���� /quit �̸� while �� ��������
					if (line.equals("/quit"))
						break;

					// ���� /to �̸� Ư�� id�� Ŭ���̾�Ʈ���Ը� ���۵�
					if (line.indexOf("/to ") == 0) {
						sendmsg(line);
					} else {
						broadcast(id + " : " + line);
						textArea.append(id + " : " + line+ "\n");
					}
				}
			} catch (Exception e) {
				System.out.println(e);

				// Ŭ���̾�Ʈ�� /quit�� ���� �����Ͽ��ų�, ���� �����Ͽ��ٸ� finally ���� ����
				// HashMap�� ���� �������� id�� �ش��ϴ� ���� ���� �� �������� �޽�����
				// ������ Ŭ���̾�Ʈ�� ������.
				// �׸��� socket �ݴ´�.
			} finally {
				synchronized (hm) {
					hm.remove(id);
				}
				broadcast(id + " ���� ���� �����Ͽ����ϴ�.");
				textArea.append(id + " ���� ���� �����Ͽ����ϴ�.\n");
				try {
					if (sock != null)
						sock.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// Ư�� ���̵𿡰� ���ڿ� ������ ���� �Լ�
		// ���� "/to id ����_�޽���"
		public void sendmsg(String msg) {
			// ���ڿ� �� ó�� ���麸�� 1ū ���� return
			int start = msg.indexOf(" ") + 1;

			// start��° index���� �����Ͽ� ó�� ������ index�� return
			int end = msg.indexOf(" ", start);
			if (end != -1) {
				// start~end �ε����� ���ڿ� ������(id ������)
				String to = msg.substring(start, end);
				String msg2 = msg.substring(end + 1); // ������ �޽��� ������
				Object obj = hm.get(to); // HashMap���� �ش� id�� ��ü �� ������
				if (obj != null) {
					PrintWriter pw = (PrintWriter) obj;
					pw.println(id + " ���� �ӼӸ��� �����˽��ϴ�. : " + msg2);
					textArea.append(id + " ���� "+ obj +"���� �ӼӸ��� �����˽��ϴ�. : " + msg2+"\n");
					pw.flush();
				}
			}
		}

		// ��ε� ĳ��Ʈ �޼ҵ� : ������ ��� Ŭ���̾�Ʈ���� ���ڿ��� �����ϴ� �޼ҵ�
		// ������ ���ڿ��� ���ڷ� ���޹޴´�.
		public void broadcast(String msg) {
			synchronized (hm) {
				// HashMap�� ����� ������ Ŭ���̾�Ʈ�� pw ��ü�� ��ü �迭(Collection)��
				// �����´�. value()�� HashMap�� ���Ե� ������ Collection ��ü�� return.
				Collection collection = hm.values();

				// Collection�� ��ҵ��� �ݺ��Ͽ� ������ ���� ���� Iterator ��ü ������ ��
				// Iterator�� ���Ͽ� ��� pw ��ü�� ������ ��� Ŭ���̾�Ʈ�� �޽��� ����
				Iterator iter = collection.iterator();
				while (iter.hasNext()) {
					PrintWriter pw = (PrintWriter) iter.next();
					pw.println(msg);
					pw.flush();
				}
			}
		}
	}

}

