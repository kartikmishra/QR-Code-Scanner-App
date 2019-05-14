package com.example.qrcodescannerapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import java.util.Locale;

import androidx.annotation.NonNull;

public class MainActivity extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    Activity activity ;
    TextToSpeech t1;
    private final int PERMISSION_REQUEST_CAMERA = 101;
    private  CodeScannerView scannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {

                    t1.setLanguage(Locale.UK);
                }
            }
        });

        scannerView = findViewById(R.id.scanner_view);
        activity = this;


        if(askForCameraPermission()){

            mCodeScanner = new CodeScanner(this, scannerView);
            readQRCode();


        }




    }


    public void readQRCode(){


        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(activity, result.getText(), Toast.LENGTH_LONG).show();
                        t1.speak(result.getText(), TextToSpeech.QUEUE_FLUSH, null);
                    }
                });

                scannerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mCodeScanner.startPreview();
                    }
                });
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();

            mCodeScanner.startPreview();


    }

    public boolean askForCameraPermission(){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Camera access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("please confirm Camera access");//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.CAMERA}
                                    , PERMISSION_REQUEST_CAMERA);
                        }
                    });
                    builder.show();
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CAMERA);


                }
            }else{
                return true;

            }
        }
        else{

            readQRCode();
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readQRCode();
                    // permission was granted, yay! Do the

                } else {
                    Toast.makeText(this, "No Permissions ", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        t1.stop();
        t1.shutdown();
        super.onPause();
    }
}
