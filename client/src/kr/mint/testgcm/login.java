package kr.mint.testgcm;

import java.net.Socket;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class login extends Activity {
	private EditText e1;
	private EditText e2;
	private TextView s1;
	private Button b1;
	private String ip;
	private Integer port;
	private boolean sw = false;
	private Socket_Thread mThread;
	Handler mHandler = new Handler();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);

		e1 = (EditText) findViewById(R.id.ip);
		e2 = (EditText) findViewById(R.id.port);
		s1 = (TextView) findViewById(R.id.show1);
		b1 = (Button) findViewById(R.id.login);

		mThread = new Socket_Thread(mHandler);
		b1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ip = e1.getText().toString();
				port = Integer.parseInt(e2.getText().toString());
				
				Intent intent = new Intent();
				
				ComponentName componentName = new ComponentName(
						"kr.mint.testgcm", "kr.mint.testgcm.MainActivity");
				intent.putExtra("ip_i",e1.getText().toString());
				intent.putExtra("port_i",Integer.parseInt(e2.getText().toString()));
				//
				intent.setComponent(componentName);
				startActivity(intent);
			}

		});

	}

}
