package com.example.messagingapp;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SendMessageFragment extends Fragment {
    ArrayList<SendMessage> sendMessageArrayList;
    ListView sendMessageList;
    SendMessageAdapter sendMessageAdapter;
    private String getID;


    @Override
    public void onResume() {
        super.onResume();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            ((MainActivity) activity).setActionBarTitle("보낸 메시지 함");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_send, container, false);

        if(getArguments() != null){
            getID = getArguments().getString("getID");
        }

        sendMessageList = (ListView) rootView.findViewById(R.id.sendMessageList);
        sendMessageArrayList = new ArrayList<SendMessage>();
        sendMessageAdapter = new SendMessageAdapter(getActivity(), sendMessageArrayList);
        sendMessageList.setAdapter(sendMessageAdapter);
        new SendBackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



        sendMessageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle readSendDataBundle = new Bundle();
                readSendDataBundle.putString("userID", sendMessageArrayList.get(position).getUserID());
                readSendDataBundle.putString("receiveID", sendMessageArrayList.get(position).getReceiveID());
                readSendDataBundle.putString("title", sendMessageArrayList.get(position).getMailTitle());
                readSendDataBundle.putString("contents", sendMessageArrayList.get(position).getMailContents());
                readSendDataBundle.putString("date", sendMessageArrayList.get(position).getDate());
                ((MainActivity)getActivity()).readSendFragment.setArguments(readSendDataBundle);
                ((MainActivity)getActivity()).onClickOpenReadSendMessage();
            }
        });

        return rootView;
    }

    class SendBackgroundTask extends AsyncTask<Void, Void, String> {
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
                target = "http://lakia1357.dothome.co.kr/MessagingApp/SendMessageList.php?userID=" + URLEncoder.encode(userID, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                sendMessageArrayList.clear();
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
                    SendMessage sendMessage = new SendMessage(userID, receiveID, title, contents, date);
                    sendMessageArrayList.add(sendMessage);
                }
                sendMessageAdapter.notifyDataSetChanged();
                if (jsonArray.length() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("No match").setNegativeButton("Retry", null).create().show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}