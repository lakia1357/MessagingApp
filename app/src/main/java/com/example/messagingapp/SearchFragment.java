package com.example.messagingapp;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchFragment extends Fragment {

    ArrayList<SearchUser> searchedUserArrayList;
    ListView searchList;
    SearchUserAdapter searchedUserAdapter;


    Spinner spinner;
    ArrayAdapter adapter;
    String searchDepartment;
    @Override
    public void onResume() {
        super.onResume();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            ((MainActivity) activity).setActionBarTitle("수신자 선택");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);
        Button searchButton = rootView.findViewById(R.id.search);
        Button searchCancelButton = rootView.findViewById(R.id.searchCancelButton);
        spinner = rootView.findViewById(R.id.spinnerDepartment);
        adapter = ArrayAdapter.createFromResource(((MainActivity)getActivity()), R.array.department, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        searchList = (ListView) rootView.findViewById(R.id.searchlist);
        searchedUserArrayList = new ArrayList<SearchUser>();
        searchedUserAdapter = new SearchUserAdapter((MainActivity)getActivity(), searchedUserArrayList);
        searchList.setAdapter(searchedUserAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDepartment = spinner.getSelectedItem().toString();
                new SearchBackgroundTask().execute();
            }
        });
        searchCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).onClickPlusCancel();
            }
        });



        return rootView;
    }

    class SearchBackgroundTask extends AsyncTask<Void, Void, String> {
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
            String userDepartment = searchDepartment;
            try {
                target = "http://lakia1357.dothome.co.kr/MessagingApp/userSearch.php?department=" + URLEncoder.encode(userDepartment, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                searchedUserArrayList.clear();
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                String userID, userName;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject row = jsonArray.getJSONObject(i);
                    userID = row.getString("userID");
                    userName = row.getString("userName");
                    SearchUser searchUser = new SearchUser(userID, userName);
                    searchedUserArrayList.add(searchUser);
                }
                searchedUserAdapter.notifyDataSetChanged();
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