package com.example.messagingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLEncoder;

import static android.app.Activity.RESULT_OK;

public class ModifyFragment extends Fragment {


    @Override
    public void onResume() {
        super.onResume();
        FragmentActivity activity = getActivity();
        if(activity != null){
            ((MainActivity)activity).setActionBarTitle("개인 정보 설정");
        }
    }
    private User user;
    private String userID, userPassword, userName, userDepartment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_modify, container, false);
        final FragmentActivity activity = getActivity();
        ArrayAdapter adapter;
        final int ACT_ADD_PHOTO = 0;
        final TextView modifyID = (TextView) rootView.findViewById(R.id.modify_id);
        final TextView modifyPassword = (TextView)rootView.findViewById(R.id.modify_pw);
        final TextView modifyName = (TextView)rootView.findViewById(R.id.modify_name);

        final Spinner spinner = rootView.findViewById(R.id.spinnerDepartment);
        adapter = ArrayAdapter.createFromResource(((MainActivity)activity), R.array.department, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final ImageButton modifyImage = (ImageButton) rootView.findViewById(R.id.userImageButton);
        final Bitmap userImage;
        final Button modifyButton = rootView.findViewById(R.id.modify_Button);

        if(getArguments() != null){
            user = (User) getArguments().getSerializable("userIN");
        }

        modifyID.setText(user.getUserID());
        modifyPassword.setText(user.getUserPassword());
        modifyName.setText(user.getUserName());
        userImage = user.getUserImage();
        modifyImage.setImageBitmap(userImage);

        modifyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                ((MainActivity)activity).startActivityForResult(intent, ACT_ADD_PHOTO);
            }
        });


        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userID = modifyID.getText().toString();
                userPassword = modifyPassword.getText().toString();
                userName = modifyName.getText().toString();
                userDepartment = spinner.getSelectedItem().toString();

                ((MainActivity)activity).modifyID = userID;
                ((MainActivity)activity).modifyPassword = userPassword;
                ((MainActivity)activity).modifyName = userName;
                ((MainActivity)activity).modifyDepartment = userDepartment;
                ((MainActivity)activity).onClickModify();

            }
        });
        return rootView;
    }
}