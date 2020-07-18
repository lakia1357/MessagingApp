package com.example.messagingapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends BaseAdapter {
    private Context context;
    private List<Message> messageList;

    public MessageAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
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

        textViewID.setText(messageList.get(position).getUserID());
        textViewTitle.setText(messageList.get(position).getMailTitle());
        textViewDate.setText(messageList.get(position).getDate());
        return v;
    }
}
