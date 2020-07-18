package com.example.messagingapp;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class MessageRequest extends StringRequest {
    final static private String URL = "http://lakia1357.dothome.co.kr/MessagingApp/MessageResgister.php";
    private Map<String, String> parameters;

    public MessageRequest(String userID, String receiveID, String title, String contents, String date, Response.Listener<String> listener){
        super(Method.POST, URL, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("RegisterRequest", error.getMessage());
            }
        });

        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("receiveID", receiveID);
        parameters.put("title", title);
        parameters.put("contents", contents);
        parameters.put("date", date);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
