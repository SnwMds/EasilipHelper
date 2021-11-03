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
			public ServiceHandler(final Looper looper) {
					super(looper);
			}
			@Override
			public void handleMessage(final Message msg) {
					
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
					
					stopSelf(msg.arg1);
			}
	}

	@Override
	public void onCreate() {
		// Set package manager
		packageManager = getPackageManager();
		
		final HandlerThread thread = new HandlerThread("ServiceStartArguments",
						Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		serviceLooper = thread.getLooper();
		serviceHandler = new ServiceHandler(serviceLooper);
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
			final Message message = serviceHandler.obtainMessage();
			
			message.arg1 = startId;
			message.obj = (Object) intent;
			
			serviceHandler.sendMessage(message);
			
			return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(final Intent intent) {
			return null;
	}

	@Override
	public void onDestroy() {
		System.exit(0);
	}


}