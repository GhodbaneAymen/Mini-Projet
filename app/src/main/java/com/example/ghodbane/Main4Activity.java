package com.example.ghodbane;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class Main4Activity extends AppCompatActivity implements SensorEventListener {
      private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
      Sensor accelerometre;
      SensorManager m;

    boolean connected,Startsend;

    BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    String command;
    ImageButton bluetooth_connect_btn;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    TextView x;

     //string variable that will store value to be transmitted to the bluetooth module


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        Startsend=false;


        bluetooth_connect_btn = (ImageButton) findViewById(R.id.bluetooth_connect_btn);

        m=(SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometre=m.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        m.registerListener(this,accelerometre,SensorManager.SENSOR_DELAY_NORMAL);
        x=(TextView)findViewById(R.id.x);
        try {
            Thread.sleep(160);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        bluetooth_connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(BTinit())
                {
                    BTconnect();
                }

            }
        });

    }


    //Initializes bluetooth module
    public boolean BTinit()
    {
        boolean found = false;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter == null) //Checks if the device supports bluetooth
        {
            Toast.makeText(getApplicationContext(), "Device doesn't support bluetooth", Toast.LENGTH_SHORT).show();
        }

        if(!bluetoothAdapter.isEnabled()) //Checks if bluetooth is enabled. If not, the program will ask permission from the user to enable it
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter,0);

            try
            {
                Thread.sleep(1000);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        if(bondedDevices.isEmpty()) //Checks for paired bluetooth devices
        {
            Toast.makeText(getApplicationContext(), "Please pair the device first", Toast.LENGTH_SHORT).show();
        }
        else
        {
            for(BluetoothDevice iterator : bondedDevices)
            {


                device = iterator;
                found = true;
                break;

            }
        }

        return found;
    }

    public boolean BTconnect()
    {
        connected = true;

        try
        {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID); //Creates a socket to handle the outgoing connection
            socket.connect();

            Toast.makeText(getApplicationContext(),
                    "Connection to bluetooth device successful", Toast.LENGTH_LONG).show();
            Startsend=true;
        }
        catch(IOException e)
        {

            e.printStackTrace();
            connected = false;
        }

        if(connected)
        {
            try
            {
                outputStream = socket.getOutputStream(); //gets the output stream of the socket
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        return connected;
    }



    @Override
    protected void onStart()
    {
        super.onStart();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        x.setText("X: "+(int)event.values[0]+"\nY: "+(int)event.values[1]+"\nZ: "+(int)event.values[2]);

        if(Startsend){
            command="S";
            if((int)event.values[2]>=5){
            command="F";
            }
            else if((int)event.values[2]<=-2){
                command="B";
            }
            else if((int)event.values[1]>=3){
                command="R";
            }
            else if((int)event.values[1]<=-3){
                command="L";
            }else {
                //command = ("a" + (int) event.values[0] + "y" + (int) event.values[1] + "f").toString();
                command="S";
            }
            try {
                outputStream.write(command.getBytes());

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}