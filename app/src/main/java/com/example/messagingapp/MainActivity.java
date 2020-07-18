package com.example.messagingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.messagingapp.RegisterActivity.ACT_ADD_PHOTO;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    User user;
    String getID, choosedID;
    LinearLayout firstContainer, fragmentContainer;
    Fragment writeFragment, sendFragment, modifyFragment, readFragment, readSendFragment, receiveFragment, searchFragment;
    Uri selectedImageURI;
    String modifyImage;
    String modifyID, modifyPassword, modifyName, modifyDepartment;
    String sendID, sendTitle, sendContents;
    String replySendID, replySendTitle;
    private int deletePosition;
    private AlertDialog dialog;
    private boolean imageCheck = false;
    long lastTimeBackPressed;

    ArrayList<Message> messageArrayList;
    ListView messageList;
    MessageAdapter messageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        firstContainer = findViewById(R.id.firstContainer);
        fragmentContainer = findViewById(R.id.fragmentContainer);
        writeFragment = new WriteMessageFragment();
        readFragment = new ReadMessageFragment();
        sendFragment = new SendMessageFragment();
        modifyFragment = new ModifyFragment();
        receiveFragment = new ReceiveMessageFragment();
        searchFragment = new SearchFragment();
        readSendFragment = new ReadSendMessageFragment();

        Intent intent = getIntent();

        user = (User) intent.getSerializableExtra("userIN");
        getID = user.getUserID();
        selectedImageURI = Uri.EMPTY;

        messageList = (ListView) findViewById(R.id.messageList);
        messageArrayList = new ArrayList<Message>();
        messageAdapter = new MessageAdapter(getApplicationContext(), messageArrayList);
        messageList.setAdapter(messageAdapter);
        new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deletePosition = position;
                Bundle readDataBundle = new Bundle();
                readDataBundle.putString("userID", messageArrayList.get(position).getUserID());
                readDataBundle.putString("receiveID", messageArrayList.get(position).getReceiveID());
                readDataBundle.putString("title", messageArrayList.get(position).getMailTitle());
                readDataBundle.putString("contents", messageArrayList.get(position).getMailContents());
                readDataBundle.putString("date", messageArrayList.get(position).getDate());
                readFragment.setArguments(readDataBundle);

                firstContainer.setVisibility(View.GONE);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, readFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }


    class BackgroundTask extends AsyncTask<Void, Void, String> { //받은 메시지 리스트뷰에 연동
        String target;

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            String userID = getID;
            try {
                target = "http://lakia1357.dothome.co.kr/MessagingApp/MessageList.php?userID=" + URLEncoder.encode(userID, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                messageArrayList.clear();
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                String userID, receiveID, title, contents, date;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject row = jsonArray.getJSONObject(i);
                    userID = row.getString("userID");
                    receiveID = row.getString("receiveID");
                    title = row.getString("title");
                    contents = row.getString("contents");
                    date = row.getString("date");
                    Message message = new Message(userID, receiveID, title, contents, date);
                    messageArrayList.add(message);
                }
                messageAdapter.notifyDataSetChanged();
                if (jsonArray.length() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("No match").setNegativeButton("Retry", null).create().show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (fragmentManager.getBackStackEntryCount() > 0) {
            if (firstContainer.getVisibility() == View.GONE)
                firstContainer.setVisibility(View.VISIBLE);
            fragmentManager.popBackStack();
            fragmentTransaction.commit();
        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (System.currentTimeMillis() - lastTimeBackPressed < 1500) {
                finish();
            } else {
                Toast.makeText(this, "'뒤로' 버튼을 한번 더 눌러 종료합니다", Toast.LENGTH_SHORT).show();
                lastTimeBackPressed = System.currentTimeMillis();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_write) { //메시지 작성 화면
            firstContainer.setVisibility(View.GONE);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, writeFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_receive) { //받은 메시지 함
            if (firstContainer.getVisibility() == View.GONE) {
                firstContainer.setVisibility(View.VISIBLE);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, receiveFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        } else if (id == R.id.nav_send) { // 보낸 메시지 함

            Bundle sendBundle = new Bundle();
            sendBundle.putString("getID", getID);
            sendFragment.setArguments(sendBundle);

            firstContainer.setVisibility(View.GONE);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, sendFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();


        } else if (id == R.id.nav_modify) { // 개인정보 변경화면

            User user = (User) getIntent().getSerializableExtra("userIN");
            Bundle modifyBundle = new Bundle();
            modifyBundle.putSerializable("userIN", user);
            modifyFragment.setArguments(modifyBundle);

            modifyBundle.putString("userID", user.getUserID());
            modifyBundle.putString("userPassword", user.getUserPassword());
            modifyBundle.putString("userName", user.getUserName());
            modifyBundle.putString("userDepartment", user.getUserDepartment());


            firstContainer.setVisibility(View.GONE);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, modifyFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_logout) {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onClickModify() { // 개인정보 변경 버튼
        if (imageCheck == true) {
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean isSuccess = jsonResponse.getBoolean("success");
                        if (isSuccess) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            dialog = builder.setMessage("변경완료 ID : " + modifyID + " Password : " + modifyPassword + " Name : " + modifyName).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
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
            UpdateRequest updateRequest = new UpdateRequest(modifyID, modifyPassword, modifyName, modifyDepartment, modifyImage, responseListener);
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.add(updateRequest);

            if (firstContainer.getVisibility() == View.GONE) {
                firstContainer.setVisibility(View.VISIBLE);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, receiveFragment);
                fragmentTransaction.commit();
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            dialog = builder.setMessage("이미지를 변경해주세요.").setNegativeButton("Retry", null).create();
            dialog.show();
        }

    }

    public void onClickSend() { // 메시지 보내기 버튼
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String getTime = simpleDate.format(mDate);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean isSuccess = jsonObject.getBoolean("success");
                    if (isSuccess) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        dialog = builder.setMessage("일치하는 사용자가 없습니다.").setNegativeButton("Retry", null).create();
                        dialog.show();
                    } else if (TextUtils.isEmpty(sendID) || TextUtils.isEmpty(sendTitle) || TextUtils.isEmpty(sendContents)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        dialog = builder.setMessage("내용을 확인해 주세요").setNegativeButton("Retry", null).create();
                        dialog.show();
                    } else {
                        Bundle checkBundle = new Bundle();
                        checkBundle.putBoolean("checkSend", true);
                        writeFragment.setArguments(checkBundle);

                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean isSuccess = jsonResponse.getBoolean("success");
                                    if (isSuccess) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                        dialog = builder.setMessage("메시지 전송 완료!!").setPositiveButton("OK", null).create();
                                        dialog.show();
                                        messageAdapter.notifyDataSetChanged();
                                    } else {
                                        Log.e("RegisterActivity", "register failed");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        MessageRequest messageRequest = new MessageRequest(getID, sendID, sendTitle, sendContents, getTime, responseListener);
                        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                        requestQueue.add(messageRequest);

                        if (firstContainer.getVisibility() == View.GONE) {
                            firstContainer.setVisibility(View.VISIBLE);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragmentContainer, receiveFragment);
                            fragmentTransaction.commit();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ValidateRequest validateRequest = new ValidateRequest(sendID, responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(validateRequest);
    }

    public void onClickplus() { // 메세지 작성 화면에서 + 버튼 누를 시 실행되는 버튼 이벤트
        firstContainer.setVisibility(View.GONE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, searchFragment);
        fragmentTransaction.commit();
    }

    public void onClickOpenReadSendMessage() { // 보낸 메시지함에서 메시지 선택시 프래그먼트 전환
        firstContainer.setVisibility(View.GONE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, readSendFragment);
        fragmentTransaction.commit();
    }

    public void onClickChoosed() { // 보낼 사람 선택시 실행되는 버튼 이벤트
        Bundle chooseBundle = new Bundle();
        chooseBundle.putString("choosedID", choosedID);
        writeFragment.setArguments(chooseBundle);

        firstContainer.setVisibility(View.GONE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, writeFragment);
        fragmentTransaction.commit();
    }

    public void onClickReply() { // 답장 버튼 클릭시 실행되는 버튼 이벤트

        Bundle replyBundle = new Bundle();
        replyBundle.putString("replySendID", replySendID);
        replyBundle.putString("replySendTitle", replySendTitle);
        writeFragment.setArguments(replyBundle);

        firstContainer.setVisibility(View.GONE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, writeFragment);
        fragmentTransaction.commit();
    }


    public void onClickPlusCancel() { // 메세지 작성 화면에서 취소 버튼 누를 시 실행되는 버튼 이벤트
        firstContainer.setVisibility(View.GONE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, writeFragment);
        fragmentTransaction.commit();
    }


    public void onClickCancel() { // 메세지 작성 화면에서 취소 버튼 누를 시 실행되는 버튼 이벤트
        if (firstContainer.getVisibility() == View.GONE) {
            firstContainer.setVisibility(View.VISIBLE);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, receiveFragment);
            fragmentTransaction.commit();
        }
    }

    public void onClickDeleteMessage() { //받은 메시지함에서 삭제 버튼 누를 시 실행

        messageArrayList.remove(deletePosition);
        messageAdapter.notifyDataSetChanged();

        Toast.makeText(this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
        if (firstContainer.getVisibility() == View.GONE) {
            firstContainer.setVisibility(View.VISIBLE);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, receiveFragment);
            fragmentTransaction.commit();
        }
    }

    public void onClickSendDeleteMessage() { //보낸 메시지함에서 삭제 버튼 누를 시 실행

        Toast.makeText(this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
        firstContainer.setVisibility(View.GONE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, sendFragment);
        fragmentTransaction.commit();
    }

    public void onClickReadSendMessageCancel() { // 메세지 읽기 화면에서 취소 버튼 누를 시 실행되는 버튼 이벤트
        firstContainer.setVisibility(View.GONE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, sendFragment);
        fragmentTransaction.commit();

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
                    modifyImage = "";
                    try {
                        modifyImage = "&imagedevice=" + URLEncoder.encode(image, "utf-8");
                    } catch (Exception e) {
                        Log.e("exception", e.toString());
                    }
                    t_imgbtn.setImageBitmap(selectedImage);
                    imageCheck = true;
                }
                break;
        }
    }
}