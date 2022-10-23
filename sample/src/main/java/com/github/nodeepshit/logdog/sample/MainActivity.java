package com.github.nodeepshit.logdog.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import com.github.nodeepshit.LynxView;
import com.github.nodeepshit.model.Logdog;

public class MainActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler.postDelayed(() -> {
            Logdog.tip("this is tip message");
            Logdog.debug("this is debug message");
            Logdog.info("this is info message");
            Logdog.warning("this is warning message");
            Logdog.error("this is error message");
        }, 1000);

        ((LynxView)findViewById(R.id.lv_logdog)).setListener(() -> {
            Logdog.info("logdog view yield focus");
        });
    }
}