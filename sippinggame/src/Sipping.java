import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
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

public class Sipping extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	
	private int cnt = 0;
	private boolean flag = false;
	int time = 500;
	int level = 1;
	
	final int odd = 1;
	final int pair = 0;
	
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

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Sipping frame = new Sipping();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void Sgame() throws InterruptedException {
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				cnt = 0;
				textField.setText(level + " level start");
				Random rand = new Random();
				boolean[] led = new boolean[6];
				for (int i = 0; i < 6; i++) {
					led[i] = rand.nextBoolean();
				}
				do {
					for (int i = 0; i < 6; i++) {
						if (led[i]) {
							pin[i].low();
							++cnt;
						} else
							pin[i].high();
					}
				} while (cnt == 0);
				
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for (int i = 0; i < 6; i++) {
					pin[i].high();
				}
				flag = true;
				textField.setText(level + " level Choice !!");
			}
		});
		t1.start();
	}

	public void switchListener(){
		mySwitch[0].addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if (event.getState() == PinState.LOW){
					click(odd);
				}
			}
		});
		mySwitch[1].addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if (event.getState() == PinState.LOW){
					click(pair);
				}
			}
		});
		mySwitch[2].addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if (event.getState() == PinState.LOW){
					clickstart();
				}
			}
		});
	}
	
	public void clickstart(){
		if(flag){
			level =1;
			time = 500;
		}
		try {
			Sgame();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public void failclear() {
		Thread t3 = new Thread(new Runnable() {
			public void run() {
				textField.setText("NO.....");
				level = 1;
				time = 500;
				for (int i = 0; i < 6; i++) {
					pin[i].low();
				}
				for (int j = 0; j < 9; j++) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for (int i = 0; i < 6; i++) {
						pin[i].toggle();
					}
				}
			}
		});
		t3.start();
	}
	public void allclear(){
		Thread t2 = new Thread(new Runnable() {
			public void run() {
				textField.setText("All Clear!!");
				level = 1;
				time = 500;
				pin[0].low();
				for (int i = 1; i < 6; i++) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					pin[i].low();
					pin[i - 1].high();
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				pin[5].high();
				pin[0].low();
				pin[2].low();
				pin[4].low();
				for (int j = 0; j < 10; j++) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for (int i = 0; i < 6; i++) {
						pin[i].toggle();
					}
				}
			}
		});
		t2.start();
	}
	public void click(int choice){
		if(flag){
			flag = false;
			if(cnt%2 == choice){
				textField.setText("OK!!!!");
				time -=50;
				level++;
				if (level == 12) {
					allclear();
				} else {
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					try {
						Sgame();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}else{
				failclear();
			}
		}
	}
	/**
	 * Create the frame.
	 */
	public Sipping() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 195);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("\uD640\uC9DD \uAC8C\uC784");
		lblNewLabel.setFont(new Font("나눔바른고딕 UltraLight", Font.BOLD, 15));
		lblNewLabel.setBounds(114, 22, 84, 36);
		contentPane.add(lblNewLabel);
		
		JButton btnNewButton_odd = new JButton("\uD640");
		btnNewButton_odd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				click(odd);
			}
		});
		btnNewButton_odd.setBounds(28, 110, 97, 23);
		contentPane.add(btnNewButton_odd);
		
		JButton btnNewButton_pair = new JButton("\uC9DD");
		btnNewButton_pair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				click(pair);
			}
		});
		btnNewButton_pair.setBounds(159, 110, 97, 23);
		contentPane.add(btnNewButton_pair);
		
		textField = new JTextField();
		textField.setBounds(159, 67, 97, 21);
		contentPane.add(textField);
		textField.setColumns(12);
		
		JButton btnNewButton_start = new JButton("START");
		btnNewButton_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clickstart();
			}
		});
		btnNewButton_start.setBounds(28, 66, 97, 23);
		contentPane.add(btnNewButton_start);
		switchListener();
	}
}
