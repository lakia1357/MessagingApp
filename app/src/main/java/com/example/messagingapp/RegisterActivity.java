package com.example.messagingapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

public class RegisterActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    private Spinner spinner;
    private EditText idEdit, pwEdit, nameEdit;
    private AlertDialog dialog;
    private Button checkButton;
    private String userImage;
    Uri selectedImageURI;
    final static int ACT_ADD_PHOTO = 0;
    private boolean validate = false;
    private boolean imageCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        spinner = findViewById(R.id.spinnerDepartment);
        adapter = ArrayAdapter.createFromResource(this, R.array.department, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        idEdit = findViewById(R.id.idEdit);
        pwEdit = findViewById(R.id.pwEdit);
        nameEdit = findViewById(R.id.nameEdit);
        checkButton = findViewById(R.id.checkButton);
        selectedImageURI = Uri.EMPTY;
    }

    public void addPhotoClick(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, ACT_ADD_PHOTO);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case ACT_ADD_PHOTO:
                if (resultCode == RESULT_OK) {
                    selectedImageURI = imageReturnedIntent.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImageURI);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    ImageButton t_imgbtn = (ImageButton) findViewById(R.id.userImageButton);
                    selectedImage = Bitmap.createScaledBitmap(selectedImage, 200, 200, true);

                    //이미지를 인코딩하여 바이너리화
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.PNG, 100, baos);    //bitmap compress
                    byte[] arr = baos.toByteArray();
                    String image = Base64.encodeToString(arr, Base64.DEFAULT);
                    userImage = "";
                    try {
                        userImage = "&imagedevice=" + URLEncoder.encode(image, "utf-8");
                    } catch (Exception e) {
                        Log.e("exception", e.toString());
                    }
                    t_imgbtn.setImageBitmap(selectedImage);
                    imageCheck = true;
                    //Toast.makeText(RegisterActivity.this, userImage, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void onClickCheck(View v) {
        String userID = idEdit.getText().toString();
        if (userID.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            dialog = builder.setMessage("아이디를 입력해 주세요.").setNegativeButton("Retry", null).create();
            dialog.show();
            return;
        }
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean isSuccess = jsonObject.getBoolean("success");
                    if (isSuccess) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        dialog = builder.setMessage("사용하실 수 있습니다.").setPositiveButton("OK", null).create();
                        dialog.show();
                        idEdit.setEnabled(false);
                        checkButton.setEnabled(false);
                        validate = true;
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        dialog = builder.setMessage("이미 존재하는 ID입니다.").setNegativeButton("Retry", null).create();
                        dialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ValidateRequest validateRequest = new ValidateRequest(userID, responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
        requestQueue.add(validateRequest);
    }

    public void onClickAdd(View v) {
        if (!validate) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            dialog = builder.setMessage("체크버튼을 눌러주세요.").setNegativeButton("Retry", null).create();
            dialog.show();
            return;
        }

        final String userID = idEdit.getText().toString();
        String userPassword = pwEdit.getText().toString();
        String userName = nameEdit.getText().toString();
        String userDepartment = spinner.getSelectedItem().toString();

        if (userID.equals("")) {
            Toast.makeText(RegisterActivity.this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
        } else if (userPassword.equals("")) {
            Toast.makeText(RegisterActivity.this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
        } else if (userName.equals("")) {
            Toast.makeText(RegisterActivity.this, "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
        }

        if(imageCheck == true){
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean isSuccess = jsonResponse.getBoolean("success");
                        if (isSuccess) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                            dialog = builder.setMessage(userID + "님의 회원가입을 축하드립니다.").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            }).create();
                            dialog.show();
                        } else {
                            Log.e("RegisterActivity", "register failed");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            RegisterRequest registerRequest = new RegisterRequest(userID, userPassword, userName, userDepartment, userImage, responseListener);
            RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
            requestQueue.add(registerRequest);
        }else if (imageCheck == false){
            Toast.makeText(RegisterActivity.this, "사진을 선택해주세요", Toast.LENGTH_SHORT).show();
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