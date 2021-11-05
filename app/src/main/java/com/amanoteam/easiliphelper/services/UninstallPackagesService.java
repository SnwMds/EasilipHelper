package com.amanoteam.easiliphelper.services;

import java.io.File;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.os.Process;
import android.widget.Toast;

import androidx.core.content.FileProvider;

public class UninstallPackagesService extends Service {
	
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
			final String packageName = intent.getStringExtra("packageName");
			
			final Uri packageUri = Uri.parse("package:" + packageName);
			
			final Intent promptUninstall = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
			promptUninstall.setData(packageUri);
			
			startActivity(promptUninstall);
			
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
		return;
	}
}
