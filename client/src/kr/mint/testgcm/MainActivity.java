package kr.mint.testgcm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.text.TextUtils;

public class MainActivity extends Activity {

	public static Context mContext;
	
	private Intent i;
	//SpeechRecognizer mRecognizer;

	private Context context;
	
	//CustomDialog mDialog = null;
	
	private TextView textAct01;
	private TextView textAct02;
	private TextView textAct03;
	private TextView textSW01;
	private TextView textSW02;
	private TextView textSW03;

	private Socket_Thread mThread;

	private ToggleButton tgb03;
	private ToggleButton tgb02;
	private ToggleButton tgb01;

	Handler mHandler = new Handler();

	public ToggleButtonTask[] mToggleButton = new ToggleButtonTask[10];

	public class ToggleButtonTask extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			int i;
			for(i=1;i<12;i++){
				if (params[0] == i)
					return i;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			switch (result){
			case 1 : textSW01.setText("SW1 ON");break;
			case 2 : textSW01.setText("SW1 OFF");break;
			case 3 : textSW02.setText("SW2 ON");break;
			case 4 : textSW02.setText("SW2 OFF");break;
			case 5 : textSW03.setText("SW3 ON");break;
			case 6 : textSW03.setText("SW3 OFF");break;
			case 7 : tgb01.setChecked(false);break;
			case 8 : tgb02.setChecked(false);break;
			case 9 : tgb03.setChecked(false);break;
			case 10: Toast.makeText(MainActivity.this, mThread.ip + " / " + mThread.port + "접속성공",Toast.LENGTH_SHORT).show();break;
			case 11: Toast.makeText(MainActivity.this, mThread.ip + " / " + mThread.port + "접속실패",Toast.LENGTH_SHORT).show();break;
			}
			for(int i = 0 ;i<10;i++)
				mToggleButton[i] = new ToggleButtonTask();
			
			super.onPostExecute(result);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Intent intent = getIntent();
		
		mThread = new Socket_Thread(mHandler);
		mThread.ip = intent.getStringExtra("ip_i");
		mThread.port = intent.getExtras().getInt("port_i");
		mThread.start();
		
		tgb01 = (ToggleButton) findViewById(R.id.toggleButton01);
		tgb02 = (ToggleButton) findViewById(R.id.toggleButton02);
		tgb03 = (ToggleButton) findViewById(R.id.toggleButton03);

		for(int i = 0 ;i<10;i++)
			mToggleButton[i] = new ToggleButtonTask();
		
		mContext = this;

		textAct01 = (TextView) findViewById(R.id.textView01);
		textAct02 = (TextView) findViewById(R.id.textView02);
		textAct03 = (TextView) findViewById(R.id.textView03);

		textSW01 = (TextView) findViewById(R.id.textSW01);
		textSW02 = (TextView) findViewById(R.id.textSW02);
		textSW03 = (TextView) findViewById(R.id.textSW03);
	    
		tgb01.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					tgb01.setBackgroundDrawable(getResources().getDrawable(R.drawable.led_red));
					mThread.sendMessage("cmd1");
					textAct01.setText("Action1   ON");
				} else {
					tgb01.setBackgroundDrawable(getResources().getDrawable(R.drawable.led_black));
					textAct01.setText("Action1 OFF");
				}
			}
		});

		tgb02.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					tgb02.setBackgroundDrawable(getResources().getDrawable(R.drawable.led_red));
					mThread.sendMessage("cmd2");
					textAct02.setText("Action2   ON");
				} else {
					tgb02.setBackgroundDrawable(getResources().getDrawable(R.drawable.led_black));
					textAct02.setText("Action2 OFF");
				}
			}
		});

		tgb03.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					tgb03.setBackgroundDrawable(getResources().getDrawable(R.drawable.led_red));
					mThread.sendMessage("cmd3");
					textAct03.setText("Action3   ON");
				} else {
					tgb03.setBackgroundDrawable(getResources().getDrawable(R.drawable.led_black));
					textAct03.setText("Action3 OFF");
				}
			}
		});
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		// display received msg
		String msg = intent.getStringExtra("msg");
		Log.i("MainActivity.java | onNewIntent", "|" + msg + "|");
		if (!TextUtils.isEmpty(msg));
	}

	/*@Override
	protected void onDestroy() {
		super.onDestroy();
	}*/
}
