package com.example.messagingapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

public class SearchUserAdapter extends BaseAdapter {

    private Context context;
    private List<SearchUser> userList;
    public String searchID;

    public SearchUserAdapter(Context context, List<SearchUser> userList) {
        this.context = context;
        this.userList = userList;
    }


    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = View.inflate(context, R.layout.list_search, null);
        TextView textViewID = v.findViewById(R.id.searchID);
        String IDplusName = userList.get(position).getSearchedID()+"(" + userList.get(position).getSearchedName()+")";
        textViewID.setText(IDplusName);

        Button chooseButton = v.findViewById(R.id.chooseButton);
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchID = userList.get(position).getSearchedID();
                ((MainActivity)context).choosedID = searchID;
                ((MainActivity)context).onClickChoosed();
            }
        });

        return v;
    }
}
