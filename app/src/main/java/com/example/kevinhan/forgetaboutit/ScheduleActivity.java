package com.example.kevinhan.forgetaboutit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;

public class ScheduleActivity extends AppCompatActivity {

    Button toHome;
    Button toSettings;

    TextView scheduleHeader;

    Schedule schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        Intent intent = getIntent();
        schedule = intent.getParcelableExtra("EXTRA_SCHEDULE");

        toHome = (Button)findViewById(R.id.toHome);
        toHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHome();
            }
        });

        toSettings = (Button)findViewById(R.id.toSettings);
        toSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });

        scheduleHeader = (TextView)findViewById(R.id.scheduleHeader);

        Day currDay = schedule.getDay();
        Resources res = getResources();
        scheduleHeader.setText(res.getStringArray(R.array.DaysOfWeek)[currDay.getDayWeek()]);
    }

    public void openHome(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("EXTRA_SCHEDULE", schedule);

        startActivity(intent);
    }

    public void openSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("EXTRA_SCHEDULE", schedule);

        startActivity(intent);
    }
}
