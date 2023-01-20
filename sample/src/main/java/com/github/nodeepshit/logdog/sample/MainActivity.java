package com.github.nodeepshit.logdog.sample;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.github.nodeepshit.model.Logcat;
import com.github.nodeepshit.model.Logdog;
import com.github.nodeepshit.model.TraceLevel;

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
            Logdog.result("this is result message");
            Logdog.response("this is response message");

            for (int i = 0; i < 10; i++) {
                Logdog.info("this is info message");
            }
        }, 1000);

        findViewById(R.id.b_add_new_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logdog.info("this is new message");
            }
        });
    }

    protected void logdog(final TraceLevel level, final String text, long delay) {
        if (TextUtils.isEmpty(text)) return;
        mHandler.postDelayed(() -> Logdog.log(level, text), delay);
    }
}