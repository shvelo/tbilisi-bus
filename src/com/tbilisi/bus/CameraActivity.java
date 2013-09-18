package com.tbilisi.bus;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;

import android.widget.FrameLayout;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Size;

import android.widget.Toast;

/* Import ZBar Class files */
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import net.sourceforge.zbar.Config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CameraActivity extends Activity {
    private Camera mCamera;
    private CameraPreview mPreview;
    private int focuseI = 0;
    private Handler autoFocusHandler;
    private FrameLayout preview;

    ImageScanner scanner;

    private boolean barcodeScanned = false;
    private boolean previewing = true;

    static {
        System.loadLibrary("iconv");
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        A.camera = mCamera;

        preview = (FrameLayout) findViewById(R.id.cameraPreview);

        startPreview();

        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);
    }

    private void startPreview() {
        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        preview.removeAllViews();
        preview.addView(mPreview);
    }

    private void continueScanning(){
        if(mCamera == null) mCamera = getCameraInstance();
        barcodeScanned = false;
        mCamera.setPreviewCallback(previewCb);
        mCamera.startPreview();
        previewing = true;
        mCamera.autoFocus(autoFocusCB);
    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    public void onResume() {
        super.onResume();
        if(mCamera != null) {
            continueScanning();
        } else {
            mCamera = getCameraInstance();
            startPreview();
            continueScanning();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    public Camera getCameraInstance(){
        Camera c = null;

        try {
            c = Camera.open();
        } catch (Exception e){
            e.printStackTrace();
        }

        if (c == null ){
            Toast.makeText(CameraActivity.this, "კამერა მიუწვდომელია!", Toast.LENGTH_LONG).show();
            finish();
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing && mCamera != null)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    PreviewCallback previewCb = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();
                Pattern p = Pattern.compile("SMSTO:([0-9]+):([0-9]+)");
                for (Symbol sym : syms) {
                    String qrData = sym.getData();
                    Matcher m = p.matcher(qrData);
                    if (m.find()) {
                        showSchedule(m.group(2));
                        barcodeScanned = true;
                    }
                }
                if (!barcodeScanned){
                    continueScanning();
                }
            }
        }
    };

    private void showSchedule(String stopId) {
        A.log("Showing schedule for " + stopId);
        Intent i = new Intent(this,ScheduleActivity.class);
        i.putExtra(ScheduleActivity.STOP_ID_KEY,stopId);
        startActivity(i);
    }

    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            focuseI--;
            if (focuseI <= 0) return;
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };
}
