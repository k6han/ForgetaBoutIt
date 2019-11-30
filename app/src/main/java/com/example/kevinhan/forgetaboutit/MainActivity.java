package com.example.kevinhan.forgetaboutit;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Button toSettings;
    Button toSchedule;

    Schedule schedule;
    BluetoothSocket btSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if(intent.hasExtra("EXTRA_SCHEDULE")){
            schedule = intent.getParcelableExtra("EXTRA_SCHEDULE");
        } else {

            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);

            DayWeek currDay = DayWeek.SUNDAY;

            switch (day) {
                case Calendar.SUNDAY:
                    currDay = DayWeek.SUNDAY;
                    break;
                case Calendar.MONDAY:
                    currDay = DayWeek.MONDAY;
                    break;
                case Calendar.TUESDAY:
                    currDay = DayWeek.TUESDAY;
                    break;
                case Calendar.WEDNESDAY:
                    currDay = DayWeek.WEDNESDAY;
                    break;
                case Calendar.THURSDAY:
                    currDay = DayWeek.THURSDAY;
                    break;
                case Calendar.FRIDAY:
                    currDay = DayWeek.FRIDAY;
                    break;
                case Calendar.SATURDAY:
                    currDay = DayWeek.SATURDAY;
                    break;
            }

            schedule = new Schedule(currDay);
        }

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
        intent.putExtra("EXTRA_SCHEDULE", schedule);

        startActivity(intent);
    }

    public void openSchedule(){
        Intent intent = new Intent(this, ScheduleActivity.class);
        intent.putExtra("EXTRA_SCHEDULE", schedule);

        startActivity(intent);
    }

}
