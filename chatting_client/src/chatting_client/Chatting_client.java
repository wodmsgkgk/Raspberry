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
	    
	    //ȣ��Ʈ��, ��Ʈ��ȣ �Է� �ޱ� ���� ��ü
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		    
	    //������ ���� �о���� ���� �Է� ��Ʈ�� ��ü
	    BufferedReader readFromServer = null;
		    
	    //������ ������ ��� ��Ʈ�� ��ü
	    PrintWriter pw = null;
	    
	    boolean endflag = false;
		    
	    try{
	        // host, port �Է�
	    	System.out.println("ȣ��Ʈ�� �Է� : ");    	   
	        String host = br.readLine();
	        System.out.println("��Ʈ��ȣ �Է� : ");
	        int port = Integer.parseInt(br.readLine());

	        // ���� ��ü ����(socket open)
	        sock = new Socket(host,port);
	    	System.out.println("���� �Ϸ�.. socket opened....");  
	    	
	    	//��Ʈ�� ��ü ����
	    	pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(),"UTF-8"));
	    	readFromServer = new BufferedReader(new InputStreamReader(sock.getInputStream(),"UTF-8"));
	    	
	    	//����� id �Է�
	    	System.out.println("����� id �Է� : ");
	    	String id = br.readLine();
	    	
	    	//�Էµ� ����� id�� ������ ����
	    	pw.println(id);
	    	pw.flush();
	    	
	    	//�ڽĽ����� ����
	    	//�ڽĽ������ ������ �����ϴ� ���ڿ��� �о�� ȭ��(ǥ�� ���)�� 
//��½�Ű�� ������ �Ѵ�.
	    	ReadFromServerThread thread = new ReadFromServerThread(sock, readFromServer);
	    	thread.start();
	    	
	    	//���ν�����
	    	//���ν������ ������ �޽����� Ű����(ǥ���Է�)�� �Է¹޾� ������ ���
//��Ű�� ����
	    	String line = null;
	    	while((line = br.readLine()) != null){
	    		pw.println(line);
	    		pw.flush();
	    		if(line.equals("/quit")){
	    			endflag = true;
	    			break;
	    		}
	    	}
	    	System.out.println("Ŭ���̾�Ʈ ������ �����մϴ�.");

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

// �ڽ� ������ Ŭ����
// ������ �����ϴ� ���ڿ��� �о�� ȭ��(ǥ�� ���)�� ��½�Ű�� ������ �Ѵ�.
class ReadFromServerThread extends Thread {
	private Socket sock = null;
	private BufferedReader readFromServer = null;

	// ������
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
			System.out.println("���� ���� �Ǿ����ϴ�.");
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