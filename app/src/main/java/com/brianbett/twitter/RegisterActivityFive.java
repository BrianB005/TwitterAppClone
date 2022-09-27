package com.brianbett.twitter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.brianbett.twitter.databinding.ActivityRegisterFiveBinding;
import com.google.android.material.button.MaterialButton;

public class RegisterActivityFive extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRegisterFiveBinding binding=ActivityRegisterFiveBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        MaterialButton syncContactsBtn,notSyncBtn;
        syncContactsBtn=binding.syncContacts;
        notSyncBtn=binding.notNow;
        syncContactsBtn.setOnClickListener(view->{

        });
        syncContactsBtn.setOnClickListener(view3->{
            startActivity(new Intent(RegisterActivityFive.this,MainActivity.class));
            finish();
        });

        notSyncBtn.setOnClickListener(view2->{
            startActivity(new Intent(RegisterActivityFive.this,MainActivity.class));
            finish();
        });
    }
}