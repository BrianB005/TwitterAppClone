package com.brianbett.twitter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.brianbett.twitter.databinding.ActivityRegisterTwoBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;

public class RegisterActivityTwo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ActivityRegisterTwoBinding binding=ActivityRegisterTwoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MaterialButton signUpBtn;


        TextInputEditText name,emailOrPhone,dateOfBirth;
        signUpBtn=binding.nextActivity;
        name=binding.inputtedName;
        emailOrPhone=binding.inputtedPhoneNumber;
        dateOfBirth=binding.inputtedDateOfBirth;




        Intent intent=getIntent();

        HashMap<String,String> detailsHashMap=(HashMap<String, String>) intent.getSerializableExtra("userDetails") ;

        name.setText(detailsHashMap.get("name"));
        dateOfBirth.setText(detailsHashMap.get("dateOfBirth"));
        if(detailsHashMap.get("email")==null){
            emailOrPhone.setText(detailsHashMap.get("phoneNumber"));
        }else{
            emailOrPhone.setText(detailsHashMap.get("email"));
        }


        signUpBtn.setOnClickListener(view->{
            Intent intent1=new Intent(RegisterActivityTwo.this,RegisterActivityThree.class);
            intent1.putExtra("userDetails",detailsHashMap);
            startActivity(intent1);
            finish();
        });


    }
}