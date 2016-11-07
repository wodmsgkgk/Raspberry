package gpioLED1;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
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

public class GpioLED1 extends JFrame {
	private JPanel contentPane;
	private JTextField textField;
	JButton button = new JButton("1");
	JButton button_1 = new JButton("2");
	JButton button_2 = new JButton("3");

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

	// ���� �޼ҵ�
	public GpioLED1() {
		createGUI();
		switchListener();
	}

	public void switchListener(){
		mySwitch[0].addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				System.out.println("GPIO PIN STATE CHANGE: " + event.getPin()+ " = " + event.getState());
				if (event.getState() == PinState.LOW){
					textField.setText("Switch1 ON");
					try {
						pattern1();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				else
					textField.setText("Switch1 OFF");
			}
		});
		mySwitch[1].addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				System.out.println("GPIO PIN STATE CHANGE: " + event.getPin()+ " = " + event.getState());
				if (event.getState() == PinState.LOW){
					textField.setText("Switch2 ON");
					try {
						pattern2();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				else
					textField.setText("Switch2 OFF");
			}
		});
		mySwitch[2].addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				System.out.println("GPIO PIN STATE CHANGE: " + event.getPin()+ " = " + event.getState());
				if (event.getState() == PinState.LOW){
					textField.setText("Switch3 ON");
					try {
						pattern3();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				else
					textField.setText("Switch3 OFF");
			}
		});
	}
	public void createGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textField.setText("Pattern1 On");
				try {
					pattern1();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
		button.setBounds(37, 167, 97, 23);
		contentPane.add(button);

		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textField.setText("Pattern2 On");
				try {
					pattern2();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

			}
		});
		button_1.setBounds(177, 167, 97, 23);
		contentPane.add(button_1);

		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textField.setText("Pattern3 On");
				try {
					pattern3();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
		button_2.setBounds(308, 167, 97, 23);
		contentPane.add(button_2);

		textField = new JTextField();
		textField.setFont(new Font("Gulim", Font.PLAIN, 12));
		textField.setText("\uC0C1\uD0DC \uBA54\uC2DC\uC9C0");
		textField.setBounds(37, 66, 368, 21);
		contentPane.add(textField);
		textField.setColumns(10);

		this.setVisible(true);
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
			}
		});
		t3.start();
	}

	public static void main(String[] args) throws InterruptedException {
		new GpioLED1();
	}
}