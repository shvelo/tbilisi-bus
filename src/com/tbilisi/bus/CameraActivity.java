package com.tbilisi.bus;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.Toast;

import com.google.zxing.integration.android.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CameraActivity extends Activity {
    private Pattern pattern;
    private boolean scanning = false;
    private boolean started = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pattern = Pattern.compile("smsto:([0-9]+):([0-9]+)", Pattern.CASE_INSENSITIVE);
        started = true;

        startScanning();
    }

    public void startScanning() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        scanning = true;
        integrator.initiateScan();
    }

    public void onRestart() {
        super.onRestart();
        
        if(started && !scanning) {
            startScanning();
        }
    }

    private void showSchedule(String stopId) {
        A.log("Showing schedule for " + stopId);
        Intent i = new Intent(this, ScheduleActivity.class);
        i.putExtra(ScheduleActivity.STOP_ID_KEY, stopId);
        startActivity(i);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = 
            IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String scanned = scanResult.getContents();

            if(scanned == null) scanned = "";

            Matcher m = pattern.matcher(scanned);
            if (m.find()) {
                scanning = false;
                showSchedule(m.group(2));
                finish();
            } else {
                startScanning();
            }
        } else {
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
