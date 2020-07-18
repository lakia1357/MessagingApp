package com.example.messagingapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private AlertDialog dialog;
    private EditText idEdit, pwEdit;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        idEdit = findViewById(R.id.idEdit);
        pwEdit = findViewById(R.id.pwEdit);

        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        SharedPreferences pref = getSharedPreferences("IdPass", MODE_PRIVATE);

        String prefid = pref.getString("Name", "");
        String prefpw = pref.getString("Password", "");
        Boolean chk = pref.getBoolean("check", false);
        checkBox.setChecked(chk);

        if (checkBox.isChecked()) {
            if (!prefid.equals("")) {
                idEdit.setText(prefid);
            }
            if (!prefpw.equals("")) {
                pwEdit.setText(prefpw);
            }
        } else if (!checkBox.isChecked()) {
            idEdit.setText(null);
            pwEdit.setText(null);
        }
    }


    public void onClickRegister(View v) {
        Intent intentRegister = new Intent(this, RegisterActivity.class);
        startActivity(intentRegister);
    }

    public void onClickLogin(View v) {
        final String userID = idEdit.getText().toString();
        final String userPassword = pwEdit.getText().toString();
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean isSuccess = jsonResponse.getBoolean("success");
                    if (isSuccess) {
                        SharedPreferences pref = getSharedPreferences("IdPass", MODE_PRIVATE);
                        SharedPreferences.Editor edit = pref.edit();
                        edit.putString("Name", userID);
                        edit.putString("Password", userPassword);
                        edit.putBoolean("check", checkBox.isChecked());
                        edit.commit();

                        String userID = jsonResponse.getString("userID");
                        String userPassword = jsonResponse.getString("userPassword");
                        String userName = jsonResponse.getString("userName");
                        String userDepartment = jsonResponse.getString("userDepartment");
                        Bitmap userImage = StringToBitmap(jsonResponse.getString("userImage"));
                        user = new User(userID, userPassword, userName, userDepartment, userImage);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("userIN", user);
                        startActivity(intent);
                        Toast.makeText(LoginActivity.this, userID + "님 환영합니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        dialog = builder.setMessage("로그인 실패").setNegativeButton("Retry", null).create();
                        dialog.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(loginRequest);
    }
    public static Bitmap StringToBitmap (String encodedString){
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}