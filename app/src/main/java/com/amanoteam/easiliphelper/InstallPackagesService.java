package com.amanoteam.easiliphelper;

import android.widget.Toast;
import android.app.Service;
import android.content.Intent;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.net.Uri;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.os.Process;

public class InstallPackagesService extends Service {
	
	private Looper serviceLooper;
	private ServiceHandler serviceHandler;
	
	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler {
		public ServiceHandler(final Looper looper) {
			super(looper);
		}
		@Override
		public void handleMessage(final Message msg) {
			final Intent intent = (Intent) msg.obj;
			final String packagePath = intent.getStringExtra("packagePath");
			
			final Uri packageUri = Uri.parse("file://" + packagePath);
			
			final Intent promptInstall = new Intent(Intent.ACTION_VIEW);
			promptInstall.setDataAndType(packageUri, "application/vnd.android.package-archive");
			
			startActivity(promptInstall);
			
			stopSelf(msg.arg1);
		}
	}

	@Override
	public void onCreate() {
		final HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		
		serviceLooper = thread.getLooper();
		serviceHandler = new ServiceHandler(serviceLooper);
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
		
		final Message msg = serviceHandler.obtainMessage();
		msg.arg1 = startId;
		msg.obj = (Object) intent;
		serviceHandler.sendMessage(msg);
		
		return START_STICKY;
	}

	@Override
	public IBinder onBind(final Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
	}
}
