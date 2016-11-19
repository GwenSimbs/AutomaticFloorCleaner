package com.example.gwen.automaticfloorcleaner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

public class HomePage extends AppCompatActivity {

    //declare variables
    Button btnSelectRoom,btnRCMode,btnProfile,btnNotifications;
    String user_cellphoneNumber;
    TextView txtAFCStatus,txtHint;
    ToggleButton toggleAutoMode,toggleConnect;
    private static final String TAG =  "bluetooth1";

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    //SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //MAC address of the bluetooth module
    private static String address = "20:16:06:16:04:24";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //Initialise variables
        btnSelectRoom = (Button)findViewById(R.id.btnSelectRoom);
        btnRCMode = (Button)findViewById(R.id.btnRCMode);
        btnProfile = (Button)findViewById(R.id.btnProfile);
        btnNotifications = (Button)findViewById(R.id.btnNotification);
        user_cellphoneNumber = getIntent().getExtras().getString("cellphoneNumber");        //get cellphone number from user_details OR Add_rooms screens
        BluetoothDevice btDevice = getIntent().getExtras().getParcelable("btDevice");
        txtAFCStatus = (TextView)findViewById(R.id.txtAFCStatus);
        txtHint = (TextView)findViewById(R.id.txtHint);
        toggleAutoMode = (ToggleButton)findViewById(R.id.toggleStartStop);
        toggleConnect = (ToggleButton)findViewById(R.id.toggleConnectToAFC);

        //Upon landing on this page, everythign should be unclickable until you connect to BT device
        btnRCMode.setClickable(false);
        toggleAutoMode.setClickable(false);
        //txtHint.setVisibility(View.VISIBLE);

        //initialise textview
        if(toggleAutoMode.isChecked()){

            //Automatic mode is ON
            txtAFCStatus.setText("Automatic mode is running");


        }else{

            //Automatic mode is OFF
            txtAFCStatus.setText("Automatic mode is off");


        }

        //On click of toggle connect button
        /*toggleConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toggleConnect.isChecked()){
                    //connect to bluetooth
                    onConnect();

                }else{
                    //disconnect ot bluetooth
                    Disconnect();
                }
            }
        });*/



        //onclick of toggle button
        toggleAutoMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toggleAutoMode.isChecked()){
                    //Send instructions via bluetooth to the robot to start running
                    txtAFCStatus.setText("Automatic mode is running");
                    sendData("A");
                    Toast.makeText(getBaseContext(), "Automatic mode is running", Toast.LENGTH_SHORT).show();

                }
                else{

                    //Send instructions to the robot to stop running
                    txtAFCStatus.setText("Automatic mode is off");
                    sendData("E");
                    Toast.makeText(getBaseContext(), "Automatic mode is off", Toast.LENGTH_SHORT).show();

                }
            }
        });


        //onclick of select room button
        OnClickSelectRoom();

        //onclick of switch to RC mode button
        OnClickRCmode();



    }


    //-----------------------Select room button OnClick---------------------------------------------
    void OnClickSelectRoom(){

        btnSelectRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Redirect to select room screen
                Intent intent = new Intent(HomePage.this,SelectRoom.class);
                intent.putExtra("cellphoneNumber",user_cellphoneNumber);             //intent.putExtra("KEY","value");
                startActivity(intent);
            }
        });

    }
    //-----------------------------Select RC mode button---------------------------------------------
    void OnClickRCmode(){
        btnRCMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //redirect to the RC mode screen
                Intent intent = new Intent(HomePage.this,RemoteControlMode.class);
                startActivity(intent);

            }
        });

    }

    //----------------------------------OnBack press---------------------------------------------


    @Override
    public void onBackPressed() {
        //super.onBackPressed();              //will exit the app on back press
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        HomePage.super.onBackPressed();
                        finish();
                    }
                }).create().show();
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
    //-------------------------------Error Exit-----------------------------------------------------
    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
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
    //---------------------------------On Connect---------------------------------------------------
    public void onConnect() {
        // super.onResume();

        Log.d(TAG, "...onResume - try connect...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e1) {
            errorExit("Fatal Error", "In onConnect() and socket create failed: " + e1.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting...");
        Toast.makeText(getApplicationContext(),"Connecting...",Toast.LENGTH_LONG).show();
        try {
            btSocket.connect();
            Log.d(TAG, "...Connection ok...");
            Toast.makeText(getApplicationContext(),"Successfully connected to bluetooth device",Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onConnect() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Create Socket...");

        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            errorExit("Fatal Error", "In onConnect() and output stream creation failed:" + e.getMessage() + ".");
        }
    }
    //----------------------------------On Disconnect-----------------------------------------------
    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            {
                errorExit("Fatal Error", "In onConnect() and unable to close socket during connection failure" + e.getMessage() + ".");
            }
        }
        finish(); //return to the first layout
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
