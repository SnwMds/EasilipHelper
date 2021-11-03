package com.amanoteam.easiliphelper;

import java.io.File;
import java.util.Arrays;

import android.util.Log;

import android.content.Intent;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;

import android.app.Activity;
import androidx.core.content.ContextCompat;

import com.amanoteam.easiliphelper.GetPackagesService;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
		}
		
		final File directory = new File("/sdcard/EasilipHelper/");
		
		if (!directory.exists()) {
			directory.mkdirs();
		}
		
		final Intent intent = getIntent();
		//final String action = intent.getStringExtra("action");
		
		final Intent newIntent = new Intent(this, GetPackagesService.class);
		
		startService(newIntent);
		
		finish();
	}
}