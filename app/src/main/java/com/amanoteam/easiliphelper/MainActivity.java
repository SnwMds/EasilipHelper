package com.amanoteam.easiliphelper;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.amanoteam.easiliphelper.EasilipService;

public class MainActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
		}
		
		final Intent intent = getIntent();
		final String action = intent.getStringExtra("action");
		
		final Intent newIntent = new Intent(this, EasilipService.class);
		
		newIntent.putExtra("action", action);
		
		startService(newIntent);
		
		finish();
	}
	
	@Override
	public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
		
		if (grantResults.length == 0 || grantResults.get(0) != PackageManager.PERMISSION_GRANTED) {
			Toast.makeText(getApplicationContext(), "Storage permission is required for this extension to work properly!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		final File directory = new File(Environment.getExternalStorageDirectory() + "/EasilipHelper/");
		
		if (!directory.exists()) {
			directory.mkdirs();
		}
	
	}
}