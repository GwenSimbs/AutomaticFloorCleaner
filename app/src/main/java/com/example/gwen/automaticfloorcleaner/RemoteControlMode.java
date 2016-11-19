package com.example.gwen.automaticfloorcleaner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

public class RemoteControlMode extends AppCompatActivity {

    private static final String TAG =  "bluetooth1";

    Button btnright, btnleft, btnforward, btnbackward, btnconnect, btndisconnect,btnstop;

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;


    //SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //MAC address of the bluetooth module
    private static String address = "20:16:06:16:04:24";            //Must be able to send the MAC address from connect page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control_mode);

        btnright = (Button)findViewById(R.id.btnRight);
        btnleft = (Button) findViewById(R.id.btnLeft);
        btnforward = (Button)findViewById(R.id.btnForward);
        btnbackward = (Button)findViewById(R.id.btnBackward);

        btnstop = (Button)findViewById(R.id.btnStop);

        btAdapter = BluetoothAdapter.getDefaultAdapter();



        //listen if right button is clicked
        btnright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("R");
                Toast.makeText(getBaseContext(), "Turning right", Toast.LENGTH_SHORT).show();
            }
        });

        //listen if left button is clicked
        btnleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("L");
                Toast.makeText(getBaseContext(), "Turning left", Toast.LENGTH_SHORT).show();

            }
        });

        //listen if forward button is clicked
        btnforward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("F");
                Toast.makeText(getBaseContext(), "Going forward", Toast.LENGTH_SHORT).show();
            }
        });

        //listen of backward nutton is clicked
        btnbackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("B");
                Toast.makeText(getBaseContext(), "Going backward", Toast.LENGTH_SHORT).show();
            }
        });

        //listen if stop button is clicked
        btnstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("S");
                Toast.makeText(getBaseContext(), "Stopped", Toast.LENGTH_SHORT).show();

            }
        });

    }

    //------------------------------CheckBTState----------------------------------------------------
    private void checkBTState(){
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }


    //----------------------------------Create bluetooth socket-------------------------------------
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }
    //-------------------------------Error Exit-----------------------------------------------------
    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }


    //---------------------------------Send data----------------------------------------------------
    private void sendData(String message){
        byte[] msgBuffer = message.getBytes();

        Log.d(TAG, "...Send data: " + message + "...");

        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            if (address.equals("00:00:00:00:00:00"))
                msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 35 in the java code";
            msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";

            errorExit("Fatal Error", msg);
        }
    }
    //----------------------------------------------------------------------------------------------
}

