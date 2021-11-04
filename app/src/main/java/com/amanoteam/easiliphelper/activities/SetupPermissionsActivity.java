package com.amanoteam.easiliphelper.activities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class SetupPermissionsActivity extends Activity {
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
			Toast.makeText(this, "Storage permission already granted, no more actions needed", Toast.LENGTH_SHORT).show();
			return;
		}
		
		Toast.makeText(this, "Please grant storage permission", Toast.LENGTH_SHORT).show();
		
		requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
		
		finish();
	}
	
}

