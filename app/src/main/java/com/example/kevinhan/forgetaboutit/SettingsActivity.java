package com.example.kevinhan.forgetaboutit;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    static final UUID myID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    static final String macAddress = "00:14:03:06:7E:E8";

    Button toHome;
    Button toSchedule;

    Button connBt;
    TextView btStatus;

    BluetoothAdapter btAdapter;
    BluetoothDevice btDevice;
    BluetoothSocket btSocket;

    InputStream is;
    OutputStream os;

    Schedule schedule;

    Button getTag;
    TextView tag;
    String input = "";
    int inputIdx = 0;

    Button confItem;
    EditText itemName;
    Item item;
    Event event;
    EditText eventName;
    EditText eventTime;
    Spinner events;
    Spinner daysOfWeek;
    int dayWeek = 6;
    boolean createEvent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Intent intent = getIntent();
        schedule = intent.getParcelableExtra("EXTRA_SCHEDULE");

        // Button to navigate to home
        toHome = (Button)findViewById(R.id.toHome);
        toHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHome();
            }
        });

        // Button to navigate to schedule
        toSchedule = (Button) findViewById(R.id.toSchedule);
        toSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSchedule();
            }
        });

        // Initialize text and button for Bluetooth
        btStatus = (TextView) findViewById(R.id.statusBt);

        // Bluetooth
        connBt = (Button) findViewById(R.id.connectBt);
        connBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btStatus.setText("Status: Connecting");

                // Set up adapter and enable bluetooth
                btAdapter = BluetoothAdapter.getDefaultAdapter();

                // Error message
                if(btAdapter == null){
                    Context context = getApplicationContext();
                    CharSequence txt = "Device does not support bluetooth";
                    Toast toast = Toast.makeText(context, txt, Toast.LENGTH_LONG);
                    toast.show();
                }

                if(!btAdapter.isEnabled())
                {
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetooth, 0);
                }

                // Set up device and password
                btDevice = btAdapter.getRemoteDevice(macAddress);

                String pin = "1234";
                btDevice.setPin(pin.getBytes());
                btDevice.createBond();

                // Attempt to connect
                try {
                    btSocket = btDevice.createRfcommSocketToServiceRecord(myID);
                } catch (Exception e) {}

                while( !btSocket.isConnected()){

                    try{
                        btSocket.connect();
                    } catch(Exception e){
                        Context context = getApplicationContext();
                        CharSequence txt = "Failed to connect, retrying";
                        Toast toast = Toast.makeText(context, txt, Toast.LENGTH_LONG);
                        toast.show();
                    }
                }

                btStatus.setText("Status: Connected");
            }
        });

        // Set up text and tag receiving button
        getTag = (Button) findViewById(R.id.getSetup);
        tag = (TextView) findViewById(R.id.displayTag);

        // Scan for tag
        getTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    // Prompt arduino for input
                    os = btSocket.getOutputStream();

                    os.write(1);

                    // Attempt to read input in separate thread
                    new ConnectedThread( btSocket );

                } catch(Exception e){
                    Context context = getApplicationContext();
                    CharSequence txt = "Failed to send input";
                    Toast toast = Toast.makeText(context, txt, Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });

        itemName = (EditText) findViewById(R.id.itemName);
        events = (Spinner) findViewById(R.id.chooseEvent);
        confItem = (Button) findViewById(R.id.confirmItem);
        daysOfWeek = (Spinner) findViewById(R.id.daySelect);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.DaysOfWeek, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysOfWeek.setAdapter(adapter);
        daysOfWeek.setOnItemSelectedListener(this);

        ArrayList<CharSequence> displayEvents = new ArrayList<CharSequence>();
        if(schedule.getEvents().size() > 0) {
            for (Event e : schedule.getEvents()) {
                if(e != null)
                displayEvents.add(e.getName());
                System.out.println(displayEvents.get(0));
            }
        }

        displayEvents.add("New");

        ArrayAdapter<CharSequence> adapter2 = new ArrayAdapter<CharSequence>(SettingsActivity.this,
            android.R.layout.simple_spinner_dropdown_item, displayEvents);

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        events.setAdapter(adapter2);

        events.setOnItemSelectedListener(this);

        confItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = itemName.getText().toString();
                item = new Item(input, name);

                if(createEvent){
                    eventName = (EditText) findViewById(R.id.eventName);
                    eventTime = (EditText) findViewById(R.id.eventTime);

                    String n = eventName.getText().toString();
                    int t = Integer.parseInt(eventTime.getText().toString());

                    event = new Event(t, n);
                    createEvent = false;

                    schedule.addEvent(dayWeek, event);
                }

                schedule.addItem(event, item);
            }
        });
    }

    /**
     * Helper method to navigate to home
     */
    public void openHome(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("EXTRA_SCHEDULE", schedule);

        startActivity(intent);
    }

    /**
     * Helper method to navigate to schedule
     */
    public void openSchedule(){
        Intent intent = new Intent(this, ScheduleActivity.class);
        intent.putExtra("EXTRA_SCHEDULE", schedule);

        startActivity(intent);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        if(parent.getId() == R.id.daySelect){

            String text = parent.getItemAtPosition(pos).toString();

            switch(text){
                case "Sunday":
                    dayWeek = 6;
                    break;
                case "Monday":
                    dayWeek = 0;
                    break;
                case "Tuesday":
                    dayWeek = 1;
                    break;
                case "Wednesday":
                    dayWeek = 2;
                    break;
                case "Thursday":
                    dayWeek = 3;
                    break;
                case "Friday":
                    dayWeek = 4;
                    break;
                case "Saturday":
                    dayWeek = 5;
                    break;
            }

        } else if(parent.getId() == R.id.chooseEvent){
            if(parent.getItemAtPosition(pos).toString().equals("New")){
                createEvent = true;
            } else {
                for(Event e : schedule.getEvents()){
                    if( e.getName().equals(parent.getItemAtPosition(pos).toString()) ){
                        event = e;
                    }
                }
            }
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        return;
    }

    /**
     * Thread to read input
     */
    private class ConnectedThread extends Thread {
        BluetoothSocket bts;
        InputStream is;
        byte[] buffer;

        public ConnectedThread(BluetoothSocket b) {

            // Set up input stream
            bts = b;
            InputStream tmp = null;

            try {
                tmp = bts.getInputStream();
            } catch (Exception e) {
                Context context = getApplicationContext();
                CharSequence txt = "Failed to connect istream";
                Toast toast = Toast.makeText(context, txt, Toast.LENGTH_LONG);
                toast.show();
            }

            is = tmp;

            start();
        }

        public void run() {
            String tmp = "";

            while (true) {
                try {
                    // Read character by character
                    char a = (char) is.read();

                    while (a != '\n') {
                        tmp = tmp + a;
                        a = (char) is.read();
                    }

                    // Stop reading on newline, generate string
                    final String string = new String(tmp.getBytes(), "UTF-8");

                    // Store string to array of inputs, until 5 inputs have been read
                    input = string;

                    tag.setText("Tag ID: " + input);

                    tmp = "";
                    if(input.length() > 0){
                        break;
                    }

                } catch (Exception e) {
                    break;
                }
            }

            cancel();
        }

        public void cancel(){
            try{
                is.close();
                btSocket.close();
            } catch(Exception e){}
        }

    }
}
