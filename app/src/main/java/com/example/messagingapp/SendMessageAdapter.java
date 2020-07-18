package com.example.messagingapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SendMessageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<SendMessage> sendMessageList;

    public SendMessageAdapter(Context context, ArrayList<SendMessage> sendMessageList) {
        this.context = context;
        this.sendMessageList = sendMessageList;
    }

    @Override
    public int getCount() {
        return sendMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return sendMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(context, R.layout.list_message, null);
        TextView textViewID = v.findViewById(R.id.mailID);
        TextView textViewTitle = v.findViewById(R.id.mailTitle);
        TextView textViewDate = v.findViewById(R.id.date);

        textViewID.setText(sendMessageList.get(position).getUserID());
        textViewTitle.setText(sendMessageList.get(position).getMailTitle());
        textViewDate.setText(sendMessageList.get(position).getDate());
        return v;
    }


}
