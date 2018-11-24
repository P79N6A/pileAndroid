package com.swust.androidpile.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.swust.androidpile.R;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_msg);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        TextView titleTV = (TextView) findViewById(R.id.title);
        TextView contentTV = (TextView) findViewById(R.id.content);
        titleTV.setText(title);
        contentTV.setText("     " + content);
    }
}
