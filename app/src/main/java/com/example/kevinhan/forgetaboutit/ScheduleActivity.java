package com.example.kevinhan.forgetaboutit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.ArrayList;

public class ScheduleActivity extends AppCompatActivity {

    Button toHome;
    Button toSettings;

    Button prevDay;
    Button nextDay;

    TextView scheduleHeader;

    TextView event1;
    TextView event2;
    TextView event3;
    TextView items1;
    TextView items2;
    TextView items3;

    Schedule schedule;

    Day currDay;

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

        currDay = schedule.getDay();

        event1 = (TextView)findViewById(R.id.event1);
        event2 = (TextView)findViewById(R.id.event2);
        event3 = (TextView)findViewById(R.id.event3);
        items1 = (TextView)findViewById(R.id.items1);
        items2 = (TextView)findViewById(R.id.items2);
        items3 = (TextView)findViewById(R.id.items3);

        prevDay = (Button)findViewById(R.id.prevDay);
        prevDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currDay = schedule.getPrevDay(currDay.getDayWeek());
                displayDay();
            }
        });

        nextDay = (Button)findViewById(R.id.nextDay);
        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currDay = schedule.getNextDay(currDay.getDayWeek());
                displayDay();
            }
        });


        displayDay();
    }

    public void displayDay() {
        Resources res = getResources();
        scheduleHeader.setText(res.getStringArray(R.array.DaysOfWeek)[currDay.getDayWeek()]);

        hideAllEvents();

        List<Event> eventList = currDay.getEvents();
        if (eventList.size() >= 1) {
            displayEvent(eventList.get(0),1);
        }
        if (eventList.size() >= 2) {
            displayEvent(eventList.get(1),2);
        }
        if (eventList.size() >= 3) {
            displayEvent(eventList.get(2),3);
        }
    }

    public void hideAllEvents() {
        event1.setVisibility(View.INVISIBLE);
        items1.setVisibility(View.INVISIBLE);
        event1.setVisibility(View.INVISIBLE);
        items1.setVisibility(View.INVISIBLE);
        event1.setVisibility(View.INVISIBLE);
        items1.setVisibility(View.INVISIBLE);
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

    public void displayEvent(Event ev, int number) {

        TextView event1 = (TextView)findViewById(R.id.event1);
        TextView event2 = (TextView)findViewById(R.id.event2);
        TextView event3 = (TextView)findViewById(R.id.event3);
        TextView items1 = (TextView)findViewById(R.id.items1);
        TextView items2 = (TextView)findViewById(R.id.items2);
        TextView items3 = (TextView)findViewById(R.id.items3);

        switch (number) {
            case 1:
                event1.setText(ev.getHeader());
                items1.setText(ev.getItemsFormat());
                event1.setVisibility(View.VISIBLE);
                items1.setVisibility(View.VISIBLE);
                break;
            case 2:
                event2.setText(ev.getHeader());
                items2.setText(ev.getItemsFormat());
                event2.setVisibility(View.VISIBLE);
                items2.setVisibility(View.VISIBLE);
                break;
            case 3:
                event3.setText(ev.getHeader());
                items3.setText(ev.getItemsFormat());
                event3.setVisibility(View.VISIBLE);
                items3.setVisibility(View.VISIBLE);
                break;
        }
    }
}
