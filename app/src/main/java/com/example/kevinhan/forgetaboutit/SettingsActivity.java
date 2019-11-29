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

        toHome = (Button)findViewById(R.id.toHome);
        toHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHome();
            }
        });

        toSchedule = (Button) findViewById(R.id.toSchedule);
        toSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSchedule();
            }
        });

        btStatus = (TextView) findViewById(R.id.statusBt);

        connBt = (Button) findViewById(R.id.connectBt);
        connBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btStatus.setText("Status: Connecting");

                btAdapter = BluetoothAdapter.getDefaultAdapter();

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

                btDevice = btAdapter.getRemoteDevice(macAddress);

                String pin = "1234";
                btDevice.setPin(pin.getBytes());
                btDevice.createBond();

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

        getTag = (Button) findViewById(R.id.getSetup);
        tag = (TextView) findViewById(R.id.displayTag);

        getTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    os = btSocket.getOutputStream();

                    os.write(1);

                    new ConnectedThread( btSocket );

                } catch(Exception e){
                    Context context = getApplicationContext();
                    CharSequence txt = "Failed to send input";
                    Toast toast = Toast.makeText(context, txt, Toast.LENGTH_LONG);
                    toast.show();
                }

                tag.setText("Tag ID: " + inputs[0]);
            }
        });
    }

    public void openHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openSchedule(){
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }

    private class ConnectedThread extends Thread {
        BluetoothSocket bts;
        InputStream is;
        byte[] buffer;

        public ConnectedThread(BluetoothSocket b) {
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
            buffer = new byte[1024];

            String tmp = "";

            while (true) {
                try {
                    char a = (char) is.read();

                    while (a != '\n') {
                        tmp = tmp + a;
                        a = (char) is.read();
                    }
                    final String string = new String(tmp.getBytes(), "UTF-8");

                    if(inputIdx < 5){
                        inputs[inputIdx] = string;
                    } else {
                        break;
                    }

                    tmp = "";

                } catch (Exception e) {
                    break;
                }
            }
        }

        public void cancel(){
            try{
                is.close();
                btSocket.close();
            } catch(Exception e){}
        }

    }
}
