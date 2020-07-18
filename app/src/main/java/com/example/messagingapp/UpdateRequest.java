package com.example.messagingapp;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UpdateRequest extends StringRequest {
    final static private String URL = "http://lakia1357.dothome.co.kr/MessagingApp/userUpdate.php";
    private Map<String, String> parameters;

    public UpdateRequest(String modifyID, String modifyPassword, String modifyName, String modifyDepartment, String modifyImage, Response.Listener<String> listener){
        super(Method.POST, URL, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("RegisterRequest", error.getMessage());
            }
        });

        parameters = new HashMap<>();
        parameters.put("userID", modifyID);
        parameters.put("userPassword", modifyPassword);
        parameters.put("userName", modifyName);
        parameters.put("userDepartment", modifyDepartment);
        parameters.put("userImage", modifyImage);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
