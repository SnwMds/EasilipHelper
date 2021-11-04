package com.amanoteam.easiliphelper;

import java.io.IOException;
import java.util.List;

import android.widget.Toast;
import android.content.ContentResolver;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ApplicationInfo;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.net.Uri;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.os.Process;
import java.io.OutputStream;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class GetPackagesService extends Service {
	private Looper serviceLooper;
	private ServiceHandler serviceHandler;


	private PackageManager packageManager;
	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}
		@Override
		public void handleMessage(Message msg) {
			// Normally we would do some work here, like download a file.
			// For our sample, we just sleep for 5 seconds.
			
			final List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
			
			String packageLabel;
			String packageName;
			int versionCode;
			String versionName;
			
			PackageInfo packageInfo;
			
			JSONObject jsonObject;
			final JSONArray jsonArray = new JSONArray();
			
			try {
				for (ApplicationInfo applicationInfo : packages) {
					packageLabel = packageManager.getApplicationLabel(applicationInfo).toString();
					packageName = applicationInfo.packageName;
					
					packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
					
					versionCode = packageInfo.versionCode;
					versionName = packageInfo.versionName;
					
					jsonObject = new JSONObject();
					
					jsonObject.put("packageLabel", packageLabel);
					jsonObject.put("packageName", packageName);
					jsonObject.put("versionCode", versionCode);
					jsonObject.put("versionName", versionName);
					
					jsonArray.put(jsonObject);
				}
				
				final ContentResolver contentResolver =  getContentResolver();
				final Uri fileUri = Uri.parse("/sdcard/EasilipHelper/installed_packages.json");
				
				final OutputStream outputStream = contentResolver.openOutputStream(fileUri);
				outputStream.write(jsonArray.toString().getBytes());
				outputStream.close();
			} catch (IOException | JSONException | NameNotFoundException e) {
				Toast.makeText(getApplicationContext(), "Error getting packages list!", Toast.LENGTH_SHORT).show();
			}
			
			// Stop the service using the startId, so that we don't stop
			// the service in the middle of handling another job
			stopSelf(msg.arg1);
		}
	}

	@Override
	public void onCreate() {
	packageManager = getPackageManager();
	// Start up the thread running the service. Note that we create a
	// separate thread because the service normally runs in the process's
	// main thread, which we don't want to block. We also make it
	// background priority so CPU-intensive work doesn't disrupt our UI.
	HandlerThread thread = new HandlerThread("ServiceStartArguments",
			Process.THREAD_PRIORITY_BACKGROUND);
	thread.start();

	// Get the HandlerThread's Looper and use it for our Handler
	serviceLooper = thread.getLooper();
	serviceHandler = new ServiceHandler(serviceLooper);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

		// For each start request, send a message to start a job and deliver the
		// start ID so we know which request we're stopping when we finish the job
		Message msg = serviceHandler.obtainMessage();
		msg.arg1 = startId;
		serviceHandler.sendMessage(msg);

		// If we get killed, after returning from here, restart
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// We don't provide binding, so return null
		return null;
	}

	@Override
	public void onDestroy() {
	Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
	}
}
