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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

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

    Button getTag;
    TextView tag;
    String[] inputs = new String[5];
    int inputIdx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
    }

    /**
     * Helper method to navigate to home
     */
    public void openHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Helper method to navigate to schedule
     */
    public void openSchedule(){
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
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
                    if(inputIdx < 5){
                        inputs[inputIdx] = string;
                    } else {
                        break;
                    }

                    tag.setText("Tag ID: " + inputs[0]);

                    tmp = "";

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
