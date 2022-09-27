package com.brianbett.twitter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.brianbett.twitter.databinding.ActivityRegisterThreeBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivityThree extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRegisterThreeBinding binding=ActivityRegisterThreeBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        Intent intent1=getIntent();
        HashMap<String,String> detailsHashMap=(HashMap<String, String>) intent1.getSerializableExtra("userDetails") ;

        TextInputEditText passwordField=binding.inputtedPassword;
        TextInputLayout passwordLayout=binding.textInputPassword;
        MaterialButton nextActivityBtn=binding.nextActivity;

        passwordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String password=editable.toString();
                if(password.length()<8){
                    passwordLayout.setError("Password must be 8 characters or more");
                    nextActivityBtn.setEnabled(false);
                }else{
                    passwordLayout.setError(null);
                    nextActivityBtn.setEnabled(true);
                }
            }
        });

        nextActivityBtn.setOnClickListener(view->{

            Intent intent=new Intent(RegisterActivityThree.this,RegisterActivityFour.class);
            detailsHashMap.put("password", Objects.requireNonNull(passwordField.getText()).toString());

            intent.putExtra("userDetails",detailsHashMap);
            startActivity(intent);
            finish();

        });
    }
}