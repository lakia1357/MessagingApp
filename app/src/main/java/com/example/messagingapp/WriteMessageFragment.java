package com.example.messagingapp;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class WriteMessageFragment extends Fragment {
    InputMethodManager imm;
    String choosedSendID, replySendID, replySendTitle;
    TextView sendID, sendTitle, sendContents;
    private boolean checkSend = false;
    @Override
    public void onResume() {
        super.onResume();
        FragmentActivity activity = getActivity();
        if(activity != null){
            ((MainActivity)activity).setActionBarTitle("메시지 작성");
        }
    }
    @Override
    public void onStart(){
        super.onStart();


        if(!TextUtils.isEmpty(choosedSendID)){
            sendID.setText(choosedSendID);
        }else if(replySendTitle != null){
            sendID.setText(replySendID);
            sendTitle.setText("[답장]"+replySendTitle);
        }

        if(checkSend == true){
            sendID.setText(null);
            sendTitle.setText(null);
            sendContents.setText(null);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_write, container, false);

        Button cancelButton = (Button) rootView.findViewById(R.id.cancelButton);
        Button plusButton = (Button)rootView.findViewById(R.id.plusButton);

        sendID = (TextView) rootView.findViewById(R.id.receivepeople);
        sendTitle = (TextView) rootView.findViewById(R.id.title);
        sendContents = (TextView) rootView.findViewById(R.id.contents);
        final Button sendButton = (Button) rootView.findViewById(R.id.sendButton);

        if(getArguments() != null){
            choosedSendID = getArguments().getString("choosedID");
            replySendID = getArguments().getString("replySendID");
            replySendTitle = getArguments().getString("replySendTitle");
            checkSend = getArguments().getBoolean("checkSend");
        }


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).sendID = sendID.getText().toString();
                ((MainActivity)getActivity()).sendTitle = sendTitle.getText().toString();
                ((MainActivity)getActivity()).sendContents = sendContents.getText().toString();
                ((MainActivity)getActivity()).onClickSend();
                imm = (InputMethodManager) ((MainActivity)getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(sendID.getWindowToken(), 0);
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).onClickplus();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).onClickCancel();
                sendID.setText(null);
                sendTitle.setText(null);
                sendContents.setText(null);
            }
        });
        return rootView;
    }
}