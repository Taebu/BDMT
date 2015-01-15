package kr.co.cashqc.gcm;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Timer implements Runnable {
	
	TimerListener eventListener;
	
	private long 	nTime;
	private Thread 	thread;
	
	public Timer(TimerListener event, long time) {
		Log.d("!!!Timer.Class!!!", "--------------Timer----------------");
		
		eventListener = event;
		nTime = time;
		start();
	}
	
	public void start() {
		Log.d("!!!Timer.Class!!!", "--------------start----------------");
		
		synchronized(this) {
			thread = new Thread( this );
			thread.start();
		}
	}
	
	public void stop() {
		Log.d("!!!Timer.Class!!!", "--------------stop----------------");
		
		synchronized(this) {
			thread.interrupt();
			thread = null;
		}
	}	
	
	public void run() {
		Log.d("!!!Timer.Class!!!", "--------------run----------------");
		
		try {
			Thread.sleep(nTime);
		}
		catch(Exception e) {
		}
		
		handler.sendEmptyMessage(0);
	}
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.d("!!!Timer.Class!!!", "--------------handleMessage----------------");
			
			if(msg.what==0) {
				if(eventListener!=null) {
					eventListener.onTick();
				}
			}
		}
	};
	
	public interface TimerListener {
		public void onTick();
	}
}
