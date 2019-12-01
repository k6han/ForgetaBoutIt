package com.example.kevinhan.forgetaboutit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    static final UUID myID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    static final String macAddress = "00:14:03:06:7E:E8";

    NotificationManager notificationManager;

    Button toSettings;
    Button toSchedule;

    Schedule schedule;
    BluetoothAdapter btAdapter;
    BluetoothDevice btDevice;
    BluetoothSocket btSocket;

    InputStream is;
    OutputStream os;

    TextView btStatus;

    Button refresh;

    List<Item> itemList;

    TextView item1;
    TextView item2;
    TextView item3;
    TextView item4;

    Button testNotif;

    TextView mitem1;
    TextView mitem2;

    boolean inputDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        Intent intent = getIntent();
        if(intent.hasExtra("EXTRA_SCHEDULE")){
            schedule = intent.getParcelableExtra("EXTRA_SCHEDULE");
        } else {

            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);

            int currDay = 0;

            switch (day) {
                case Calendar.SUNDAY:
                    currDay = 0;
                    break;
                case Calendar.MONDAY:
                    currDay = 1;
                    break;
                case Calendar.TUESDAY:
                    currDay = 2;
                    break;
                case Calendar.WEDNESDAY:
                    currDay = 3;
                    break;
                case Calendar.THURSDAY:
                    currDay = 4;
                    break;
                case Calendar.FRIDAY:
                    currDay = 5;
                    break;
                case Calendar.SATURDAY:
                    currDay = 6;
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

        btStatus = (TextView) findViewById(R.id.btStatus);

        btStatus.setText("Connecting");

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if(btAdapter != null){
            btConnection();
        }

        refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    os = btSocket.getOutputStream();

                    os.write(1);

                    itemList = new ArrayList<Item>();

                    new ConnectedThread( btSocket );

                } catch (Exception e){
                    Context context = getApplicationContext();
                    CharSequence txt = "Failed to send input";
                    Toast toast = Toast.makeText(context, txt, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        testNotif = (Button) findViewById(R.id.sendNotif);
        testNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    os = btSocket.getOutputStream();

                    os.write(1);

                    itemList = new ArrayList<Item>();

                    new ConnectedThread( btSocket );

                } catch (Exception e){
                    Context context = getApplicationContext();
                    CharSequence txt = "Failed to send input";
                    Toast toast = Toast.makeText(context, txt, Toast.LENGTH_LONG);
                    toast.show();
                }

                while(true){
                    if(inputDone) break;
                }

                List<Item> masterList = schedule.getItems();
                String content = "Remember to bring: ";

                for(int i = 0; i < masterList.size(); i++){
                    for(Item j : itemList){
                        if(j.equals(masterList.get(i))){
                            masterList.remove(i);
                            i--;
                        }
                    }
                }

                if(masterList.size() == 0){
                    return;
                }

                if(masterList.size() > 0){
                    mitem1.setText(masterList.get(0).getName());
                }

                if(masterList.size() > 1){
                    mitem2.setText(masterList.get(1).getName());
                }

                for(Item i : masterList){
                    content = content + i.getName() + " ";
                }

                sendNotif(content);

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

    public void sendNotif(String content){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "!FAI")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Missing Items!")
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pi);

        notificationManager.notify(5, builder.build());
    }

    public void btConnection(){
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
        btStatus.setText("Connected");
    }

    private class ConnectedThread extends Thread {
        BluetoothSocket bts;
        InputStream is;
        List<Item> masterList;

        public ConnectedThread(BluetoothSocket b) {

            btStatus.setText("Scanning bag");

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

            masterList = schedule.getItems();

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

                    for(Item i : masterList){
                        if(i.getId().equals(string)){
                            itemList.add(i);
                        }
                    }
                    // Store string to array of inputs, until 5 inputs have been read

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

            btStatus.setText("Connected");
            if(itemList.size() > 0){
                item1.setText(itemList.get(0).getName());
            }

            if(itemList.size() > 1){
                item2.setText(itemList.get(1).getName());
            }

            if(itemList.size() > 2){
                item3.setText(itemList.get(2).getName());
            }

            if(itemList.size() > 3){
                item4.setText(itemList.get(3).getName());
            }

            inputDone = true;
        }

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "!ForgetaBoutIt";
            String description = "Notification channel for !ForgetaBoutIt";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("!FAI", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
