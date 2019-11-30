package com.example.kevinhan.forgetaboutit;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_SCHED = "com.example.kevinhan.forgetaboutit.EXTRA_SCHED";
    public static final String EXTRA_SOCKET = "com.example.kevinhan.forgetaboutit.EXTRA_SOCKET";

    Button toSettings;
    Button toSchedule;

    Schedule schedule;
    BluetoothSocket btSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    //    schedule = new Schedule();

        toSettings = (Button)findViewById(R.id.toSettings);
        toSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });

        toSchedule = (Button)findViewById(R.id.toSchedule);
        toSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSchedule();
            }
        });
    }

    public void openSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(EXTRA_SCHED, schedule);
        intent.putExtra(EXTRA_SOCKET, String.valueOf(btSocket));

        startActivity(intent);
    }

    public void openSchedule(){
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }

}
