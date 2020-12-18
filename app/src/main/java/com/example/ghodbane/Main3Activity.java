package com.example.ghodbane;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class Main3Activity extends AppCompatActivity {
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    Boolean connected=false;
    String command;
    RelativeLayout layout_joystick;
    ImageView image_joystick, image_border;
    TextView textView1, textView2, textView3, textView4, textView5;
    ImageButton bluetooth_connect_btn;
    JoyStickClass js;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_3);

        textView1 = (TextView)findViewById(R.id.textView1);
        textView2 = (TextView)findViewById(R.id.textView2);
        bluetooth_connect_btn = (ImageButton) findViewById(R.id.bluetooth_connect_btn);
        bluetooth_connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (BTinit()) {
                    BTconnect();
                }

            }
        });


        layout_joystick = (RelativeLayout)findViewById(R.id.layout_joystick);

        js = new JoyStickClass(getApplicationContext()
                , layout_joystick, R.drawable.image_button);
        js.setStickSize(150, 150);
        js.setLayoutSize(500, 500);
        js.setLayoutAlpha(150);
        js.setStickAlpha(100);
        js.setOffset(90);
        js.setMinimumDistance(50);

        layout_joystick.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js.drawStick(arg1);
                command="S";
                if(arg1.getAction() == MotionEvent.ACTION_DOWN
                        || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                    int x1=js.getX()/100;
                    int x2=js.getX()/10;
                    int y1=js.getY()/100;
                    int y2=js.getY()/10;
                    if(x1>=2){
                        //command="j+"+String.valueOf(js.getX());
                        command="R";
                    }
                    else if(x1<=-2){
                       // command="j"+String.valueOf(js.getX());
                        command="L";

                    }
                    else if(x2>=1&&x2<=9 && y2>=1&&y2<=9){
                       // command="j+0"+String.valueOf(js.getX());
                        command="S";
                    }
                    else if(x2<=-1&&x2>=-9 && y2<=-1&&y2>=-9 ){
                       // command="j-0"+String.valueOf(js.getX()*(-1));
                        command="S";
                    }
//                    else {
//                        if(js.getX()>=0 && js.getY()>=0){
//                            //command="j+00"+String.valueOf(js.getX());
//                            command="S";
//                        }
//                        else{
//                           // command="j-00"+String.valueOf(js.getX()*(-1));
//                            command="S";
//                        }
//                    }
//                    try {
//                        outputStream.write(command.getBytes());
//                        Thread.sleep(50);
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }




                    if(y1>=1){
                        //command=command+"y+"+String.valueOf(js.getY())+"f";
                        command="B";
                    }
                    else if(y1<=-1){
                        //command=command+"y"+String.valueOf(js.getY())+"f";
                        command="F";

                    }
                    else if(y2>=1&&y2<=9 && x2>=1&&x2<=9){
                       // command=command+"y+0"+String.valueOf(js.getY())+"f";
                        command ="S";
                    }
                    else if(y2<=-1&&y2>=-9 && x2<=-1&&x2>=-9){
                        //command=command+"y-0"+String.valueOf(js.getY()*(-1))+"f";
                        command ="S";

                    }
//                    else {
//                        if(js.getY()>=0 && js.getX()>=0){
//                            //command=command+"y+00"+String.valueOf(js.getY())+"f";
//                            command ="S";
//
//                        }
//                        else {
//                           // command=command+"y-00"+String.valueOf(js.getY()*(-1))+"f";
//                            command ="S";
//
//                        }
//                    }


                    textView1.setText("X : " + String.valueOf(js.getX()));
                    textView2.setText("Y : " + String.valueOf(js.getY()));


                        try {
                            outputStream.write(command.getBytes());
                            Thread.sleep(50);

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }




                    int direction = js.get8Direction();

                } else if(arg1.getAction() == MotionEvent.ACTION_UP) {
                    textView1.setText("X :");
                    textView2.setText("Y :");
                    command="S";
                    try {
                        outputStream.write(command.getBytes());
                        Thread.sleep(50);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                return true;
            }
        });
    }
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
}
