package kr.mint.testgcm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class Socket_Thread extends Thread {
	private Socket soc = null;
	private PrintWriter pw;
	private BufferedReader in;
	private Context mContext = null;
	Handler mHandler;
	public login mlogin;
	public String ip;
	public int port;
	
	private boolean mStart = true;
	private boolean connect = false;

	Socket_Thread(Handler _h) {
		mHandler = _h;
	}

	public void run() {
		try {
			Log.i("jw", "ip : "+ip+" port : "+port );
			soc = new Socket(ip, port);
			
			connect = soc.isConnected();
			if(!connect) {Log.i("jw", "실패");((MainActivity) MainActivity.mContext).mToggleButton[7].execute(11);}
			else if(connect) {Log.i("jw", "성공");((MainActivity) MainActivity.mContext).mToggleButton[7].execute(10);}
			
			pw = new PrintWriter(soc.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			
			String _msg;
			
			pw.println("SmartPhone");
			
			while (mStart) {
				_msg = in.readLine();
				Log.i("jw", _msg);
				
				if(_msg.contains("SW1ON"))((MainActivity) MainActivity.mContext).mToggleButton[1].execute(1);
				else if(_msg.contains("SW1OFF"))((MainActivity) MainActivity.mContext).mToggleButton[2].execute(2);
				else if(_msg.contains("SW2ON"))((MainActivity) MainActivity.mContext).mToggleButton[3].execute(3);
				else if(_msg.contains("SW2OFF"))((MainActivity) MainActivity.mContext).mToggleButton[4].execute(4);
				else if(_msg.contains("SW3ON"))((MainActivity) MainActivity.mContext).mToggleButton[5].execute(5);
				else if(_msg.contains("SW3OFF"))((MainActivity) MainActivity.mContext).mToggleButton[6].execute(6);
				else if(_msg.contains("Action1OFF"))((MainActivity) MainActivity.mContext).mToggleButton[7].execute(7);
				else if(_msg.contains("Action2OFF"))((MainActivity) MainActivity.mContext).mToggleButton[8].execute(8);
				else if(_msg.contains("Action3OFF"))((MainActivity) MainActivity.mContext).mToggleButton[9].execute(9);
				
				Message msg = new Message();
				
				msg.obj = _msg;
				mHandler.sendMessage(msg);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void sendMessage(String _str) {
		Log.i("jw", "pw : " + pw + " / _str : " + _str);
		pw.println(_str);
	}

	public void closeSocket() {
		mStart = false;
		try {
			soc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
