package com.example.messagingapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class ReadMessageFragment extends Fragment {

    private String sendID, receiveID, title, contents, date;

    @Override
    public void onResume() {
        super.onResume();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            ((MainActivity) activity).setActionBarTitle("메시지 읽기");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_read, container, false);

        final TextView sendpeopleView = rootView.findViewById(R.id.sendpeopleView);
        final TextView receivepeopleView = rootView.findViewById(R.id.receivepeopleView);
        final TextView titleView = rootView.findViewById(R.id.titleView);
        final TextView containView = rootView.findViewById(R.id.containView);
        final TextView getTimeView = rootView.findViewById(R.id.getTimeView);
        final Button cancel = rootView.findViewById(R.id.cancel);
        final Button delete = rootView.findViewById(R.id.delete);
        final Button reply = rootView.findViewById(R.id.reply);


        if (getArguments() != null) {
            sendID = getArguments().getString("userID");
            receiveID = getArguments().getString("receiveID");
            title = getArguments().getString("title");
            contents = getArguments().getString("contents");
            date = getArguments().getString("date");
        }

        sendpeopleView.setText(sendID);
        receivepeopleView.setText(receiveID);
        titleView.setText(title);
        containView.setText(contents);
        getTimeView.setText(date);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).onClickCancel();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean isSuccess = jsonResponse.getBoolean("success");
                            if (isSuccess) {
                                ((MainActivity) getActivity()).onClickDeleteMessage();
                            } else {
                                Log.e("ReadMessageFragment", "Delete failed");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                MessageDeleteRequest messageDeleteRequest = new MessageDeleteRequest(sendID, receiveID, title, contents, date, responseListener);
                RequestQueue requestQueue = Volley.newRequestQueue((MainActivity)getActivity());
                requestQueue.add(messageDeleteRequest);
            }
        });

        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity) getActivity()).replySendID = sendID;
                ((MainActivity) getActivity()).replySendTitle = title;

                ((MainActivity) getActivity()).onClickReply();
            }
        });


        return rootView;
    }
}