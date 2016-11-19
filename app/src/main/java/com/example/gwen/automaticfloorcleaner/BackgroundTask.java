package com.example.gwen.automaticfloorcleaner;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gwen on 11/13/2016.
 */
public class BackgroundTask {

    Context context;
    ArrayList<Room> arrayList = new ArrayList<>();
    String json_array_url = "http://192.168.43.218/AutomaticFloorCleaner/get_user_rooms.php";
    //String json_array_url = "http://gwensimbs.netne.net/AutomaticFloorCleaner/get_user_rooms.php";

    public BackgroundTask(Context context){
        this.context = context;                 //to initialise the context variable
    }


    public ArrayList<Room> getList(final String cellphoneNumber){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, json_array_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //---------------------------------------------------------------

                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //---------------------------------------------------------------
                        int count =0;
                        while(count < jsonArray.length())
                        try {

                            JSONObject jsonObject = jsonArray.getJSONObject(count);
                            Room room = new Room(jsonObject.getString("room_name"));
                            arrayList.add(room);
                            count++;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //display error message
                Toast.makeText(context,"Error... No response from server", Toast.LENGTH_SHORT).show();
                error.printStackTrace();

            }
        }){//Here we overide getparams


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("cellphoneNumber",cellphoneNumber);
                return params;
            }
        };

        MySingleton.getInstance(context).addToRequestQue(stringRequest);
        return arrayList;


}


}