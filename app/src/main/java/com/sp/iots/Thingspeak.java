package com.sp.iots;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Thingspeak extends AppCompatActivity {

    private Button upload;
    private RequestQueue mQueue;
    private Button signout;

    public String dataUP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thingspeak);

        upload = (Button) findViewById(R.id.button2);
        mQueue= Volley.newRequestQueue(this);
        signout = (Button) findViewById(R.id.logout);


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                EditText dataup = (EditText) findViewById(R.id.editText);
                dataUP = dataup.getText().toString();
                urlreq();
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Thingspeak.this,Login.class));
                finish();
            }
        });
    }

    public void jsonParse() {
        String urlfeed = "https://api.thingspeak.com/channels/2006951/feeds.json?results=2"; //change this with you http request "READ A CHANNEL FEED"
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlfeed, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("feeds");
                            JSONObject feeds = jsonArray.getJSONObject(0);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request); }





    public void urlreq() {

        RequestQueue queue = Volley.newRequestQueue(this);
        final String base = "https://api.thingspeak.com/update?";
        final String api_key = "api_key";
        final String field_1 = "field1";

        // Build up the query URI
        Uri builtURI = Uri.parse(base).buildUpon()
                .appendQueryParameter(api_key, "2H6F1UQ2MVRAPTOG") //change this with your write api key
                .appendQueryParameter(field_1, dataUP )
                .build();
        String url = builtURI.toString();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display response string.
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });
        queue.add(stringRequest);}
}