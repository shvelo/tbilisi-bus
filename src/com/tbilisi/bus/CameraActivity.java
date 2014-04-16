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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pattern = Pattern.compile("smsto:([0-9]+):([0-9]+)", Pattern.CASE_INSENSITIVE);

        startScanning();
    }

    public void startScanning() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
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
        A.log(resultCode);
        if (scanResult != null) {
            String scanned = scanResult.getContents();
            Toast.makeText(this, scanned, Toast.LENGTH_LONG).show();
            Matcher m = pattern.matcher(scanned);
            if (m.find()) {
                showSchedule(m.group(2));
            } else {
                Toast.makeText(this, "Doesn't match, GabeN help us", Toast.LENGTH_LONG).show();
                startScanning();
            }
        } else {
            Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
