package com.amanoteam.easiliphelper;

import java.io.File;
import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.amanoteam.easiliphelper.GetPackagesService;

public class MainActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
		}
		
		final Intent intent = getIntent();
		final String action = intent.getStringExtra("action");
		
		Intent newIntent;
		
		if (action.equals("get_packages")) {
			newIntent = new Intent(this, GetPackagesService.class);
		}
		
		startService(newIntent);
		
		finish();
	}
	
	@Override
	public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
		
		if (grantResults.length == 0 || Arrays.asList(grantResults).indexOf(0) != PackageManager.PERMISSION_GRANTED) {
			Toast.makeText(getApplicationContext(), "Storage permission is required for this extension to work properly!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		final File directory = new File("/sdcard/EasilipHelper/");
		
		if (!directory.exists()) {
			directory.mkdirs();
		}
	
	}
}