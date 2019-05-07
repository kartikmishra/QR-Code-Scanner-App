package com.example.qrcodescannerapp;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;


        final CodeScannerView scannerView = findViewById(R.id.scanner_view);



        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.CAMERA}, 50);

        }

            t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {

                @Override
                public void onInit(int status) {
                    if(status != TextToSpeech.ERROR) {

                        t1.setLanguage(Locale.UK);
                    }
                }
            });


            mCodeScanner = new CodeScanner(this, scannerView);

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

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        t1.stop();
        t1.shutdown();
        super.onPause();
    }
}
