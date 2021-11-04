package com.amanoteam.easiliphelper;

import java.io.IOException;
import java.util.List;
import android.util.Log;

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
			
			final List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
			
			String packageLabel;
			String packageName;
			long versionCode;
			String versionName;
			
			PackageInfo packageInfo;
			
			JSONObject jsonObject;
			final JSONArray jsonArray = new JSONArray();
			
			try {
				for (ApplicationInfo applicationInfo : packages) {
					packageName = applicationInfo.packageName;
					
					if (!packageName.startsWith("eu.kanade.tachiyomi.extension")) {
						continue;
					}
					
					packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
					
					packageLabel = packageManager.getApplicationLabel(applicationInfo).toString();
					versionCode = packageInfo.getLongVersionCode();
					versionName = packageInfo.versionName;
					
					jsonObject = new JSONObject();
					
					jsonObject.put("packageLabel", packageLabel);
					jsonObject.put("packageName", packageName);
					jsonObject.put("versionCode", versionCode);
					jsonObject.put("versionName", versionName);
					
					jsonArray.put(jsonObject);
				}
				
				final ContentResolver contentResolver =  getContentResolver();
				final Uri fileUri = Uri.parse("file:///sdcard/EasilipHelper/installed_packages.json");
				
				final OutputStream outputStream = contentResolver.openOutputStream(fileUri);
				outputStream.write(jsonArray.toString().getBytes());
				outputStream.close();
			} catch (IOException | JSONException | NameNotFoundException e) {
				Toast.makeText(getApplicationContext(), "Error getting packages list!", Toast.LENGTH_SHORT).show();
				Log.d("myapp", Log.getStackTraceString(e));
			}
			
			stopSelf(msg.arg1);
		}
	}

	@Override
	public void onCreate() {
		packageManager = getPackageManager();
		
		HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		
		serviceLooper = thread.getLooper();
		serviceHandler = new ServiceHandler(serviceLooper);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
		
		Message msg = serviceHandler.obtainMessage();
		msg.arg1 = startId;
		serviceHandler.sendMessage(msg);
		
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
	}
}
