package com.example.gwen.automaticfloorcleaner;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddRoom extends AppCompatActivity {

    //declare variables
    EditText edtRoomName,edtRoomLength,edtRoomWidth,edtDuration;
    Button btnSaveRoom;
    String room_name,length,width,duration,user_cellphoneNumber;
    AlertDialog.Builder builder;
    String url_add_room = "http://192.168.43.218/AutomaticFloorCleaner/add_room_details.php";
    //String url_add_room = "http://gwensimbs.netne.net/AutomaticFloorCleaner/add_room_details.php";
    TextView txtTestAddRoom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        //Initialise variables
        edtRoomName = (EditText)findViewById(R.id.edtRoomName);
        edtRoomLength = (EditText)findViewById(R.id.edtRoomLength);
        edtRoomWidth = (EditText)findViewById(R.id.edtRoomWidth);
        edtDuration = (EditText)findViewById(R.id.edtDuration);
        btnSaveRoom = (Button)findViewById(R.id.btnSaveRoom);
        txtTestAddRoom =(TextView)findViewById(R.id.txtTestAddRoom);
        builder = new AlertDialog.Builder(AddRoom.this);
       // cellphoneNumber = getIntent().getExtras().getString("cellphoneNumber");
        user_cellphoneNumber = getIntent().getExtras().getString("cellphoneNumber");                //get user_cellphone from previous activity
        txtTestAddRoom.setText(user_cellphoneNumber);

        //Onclick of btnSaveRoom
        btnSaveRoom.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                //Fetch values entered by user and also fetch the cellphone number as entered by the user from the UserDetails activity
                room_name = edtRoomName.getText().toString();
                length = edtRoomLength.getText().toString();
                width = edtRoomWidth.getText().toString();
                duration = edtDuration.getText().toString();
                //user_cellphoneNumber = "108465683";  //Note:user_cellphoneNumber must come with bundles form the prevous activity
                //user_cellphoneNumber = getIntent().getExtras().getString("cellphoneNumber");


                //Test if user has entered all the fields
                if(room_name.equals("")||length.equals("")||width.equals("")||duration.equals("")||user_cellphoneNumber.equals("")){
                    builder.setTitle("Input Error");
                    builder.setMessage("Please fill in all the fields");
                    displayAlertInputError("input_error");
                }else{
                    //If user has entered all the fields
                    //--------Do the StringRequest here-------------------------------------------------
                StringRequest strReq = new StringRequest(Request.Method.POST, url_add_room,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                               // txtTestAddRoom.setText(response.toString());

                               try {
                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    String code = jsonObject.getString("code");         //this will return reg_success or reg_failed
                                    String message = jsonObject.getString("message");
                                    builder.setTitle("Server Response");
                                    builder.setMessage(message);
                                    displayAlert(code);
                                    // txtTest.setText(message);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("AddRoom",error.getMessage());

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        //Send the data entered by user to the php file
                        params.put("room_name", room_name);
                        params.put("length", length);
                        params.put("width", width);
                        params.put("duration", duration);
                        params.put("user_cellphoneNumber", user_cellphoneNumber);       //user_cellphoneNumber from previous intent

                        return params;
                    }
                };

                    MySingleton.getInstance(AddRoom.this).addToRequestQue(strReq);

                    //--------------------End of StringRequest------------------------------------------

                }

            }
        });
    }
    //-------------------------------displayAlertInputError-----------------------------------------
    void displayAlertInputError(final String code){
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(code.equals("input_error")){
                    //clear all the fields
                    edtRoomName.setText("");
                    edtRoomLength.setText("");
                    edtRoomWidth.setText("");
                    edtDuration.setText("");

                }

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    //-----------------------------------display alert function-------------------------------------
    void displayAlert(final String code){

        //setting a positive button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //what should happen when user clicks YES
                if(code.equals("failed"))
                {
                    //remain on the same page. closes the dialog box
                    dialogInterface.cancel();

                }else if(code.equals("success")){

                    //clear all the fields and stays in the same page
                    edtRoomName.setText("");
                    edtRoomLength.setText("");
                    edtRoomWidth.setText("");
                    edtDuration.setText("");

                }

            }
        });

        //setting a negative button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //what should happen when user clicks NO
             if(code.equals("failed"))
                {
                    //redirect to the connecToAFC
                    Intent intent = new Intent(AddRoom.this,HomePage.class);
                    intent.putExtra("cellphoneNumber",user_cellphoneNumber);             //intent.putExtra("KEY","value");
                    startActivity(intent);


                }else if(code.equals("success")){

                    //redirect to the connecToAFC page
                 Intent intent = new Intent(AddRoom.this,HomePage.class);
                 intent.putExtra("cellphoneNumber",user_cellphoneNumber);             //intent.putExtra("KEY","value");
                 startActivity(intent);

                }

            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    //----------------------------------------------------------------------------------------------
}
