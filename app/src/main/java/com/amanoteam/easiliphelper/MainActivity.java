package com.amanoteam.easiliphelper;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.amanoteam.easiliphelper.QueryPackagesService;
import com.amanoteam.easiliphelper.InstallPackagesService;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final File directory = new File("/sdcard/EasilipHelper/");
		
		if (!directory.exists()) {
			directory.mkdirs();
		}
		
		final Intent intent = getIntent();
		final String action = intent.getStringExtra("action");
		
		
		if (action == null) {
			Toast.makeText(this, "Missing required argument!", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		
		if (action.equals("query_packages")) {
			final Intent newIntent = new Intent(this, QueryPackagesService.class);
			startService(newIntent);
		} else if (action.equals("install_packages")) {
			final String packagePath = intent.getStringExtra("packagePath");
			
			if (packagePath == null) {
				Toast.makeText(this, "Missing required argument!", Toast.LENGTH_SHORT).show();
				finish();
			}
			
			final Intent newIntent = new Intent(this, InstallPackagesService.class);
			newIntent.putExtra("packagePath", packagePath);
			
			startService(newIntent);
		}
		
		finish();
	}
}