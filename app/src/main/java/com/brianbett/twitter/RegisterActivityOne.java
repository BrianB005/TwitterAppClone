package com.brianbett.twitter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.brianbett.twitter.databinding.ActivityRegisterOneBinding;
import com.brianbett.twitter.retrofit.RetrofitHandler;
import com.brianbett.twitter.retrofit.UserDetailsInterface;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivityOne extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityRegisterOneBinding binding = ActivityRegisterOneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MaterialButton changeInputTypeBtn, nextPageBtn, openLoginBtn;
        TextInputEditText name, phoneNumber, dateOfBirth;
        DatePicker datePicker;

        changeInputTypeBtn = binding.changeInputType;
        nextPageBtn = binding.nextActivity;
        openLoginBtn = binding.openLogin;
        name = binding.inputName;
        phoneNumber = binding.inputPhoneNumber;
        dateOfBirth = binding.inputDateOfBirth;
        datePicker = binding.datePicker;


        openLoginBtn.setOnClickListener(view3 ->{
            startActivity(new Intent(RegisterActivityOne.this, LoginActivity.class));
            finish();
        });

        dateOfBirth.setInputType(InputType.TYPE_NULL);
        dateOfBirth.setKeyListener(null);

        dateOfBirth.setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                hideKeyboard(RegisterActivityOne.this);
                datePicker.setVisibility(View.VISIBLE);
            }
            return false;
        });

        phoneNumber.setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getAction()==MotionEvent.ACTION_UP){

                datePicker.setVisibility(View.GONE);
            }
            return false;
        });
        name.setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getAction()==MotionEvent.ACTION_UP){

                datePicker.setVisibility(View.GONE);
            }
            return false;
        });;



        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        datePicker.setOnDateChangedListener((datePicker1, i, i1, i2) -> {
            int day = datePicker1.getDayOfMonth();
            int month = datePicker1.getMonth() ;
            int year = datePicker1.getYear();

            String datePicked = months[month] + " " + String.valueOf(day) + "," + String.valueOf(year);
            dateOfBirth.setText(datePicked);
        });


        changeInputTypeBtn.setOnClickListener((view2) -> {
            if (phoneNumber.getInputType() == InputType.TYPE_CLASS_PHONE) {
                phoneNumber.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                changeInputTypeBtn.setText("Use phone number instead");
            } else {
                phoneNumber.setInputType(InputType.TYPE_CLASS_PHONE);
                changeInputTypeBtn.setText("Use email instead");
            }
        });

        nextPageBtn.setEnabled(false);

//        checking inputted email or phone number
        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                datePicker.setVisibility(View.GONE);
                binding.textInputEmail.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                binding.textInputEmail.setError(null);

            }

            @Override
            public void afterTextChanged(Editable editable) {

                HashMap<String, String> details = new HashMap<>();
                if (phoneNumber.getInputType() == InputType.TYPE_CLASS_PHONE) {
                    details.put("phoneNumber", editable.toString());
                } else {
                    details.put("email", editable.toString());
                }
                RetrofitHandler.checkUserDetails(details, new UserDetailsInterface() {
                    @Override
                    public void success() {
                        nextPageBtn.setEnabled(true);
                        binding.textInputEmail.setError(null);
                    }
                    @Override
                    public void errorExists(String errorMessage) {
                        binding.textInputEmail.setError(errorMessage);
                    }
                    @Override
                    public void failure(Throwable throwable) {
                        Log.d("Exception", throwable.getMessage());
                    }
                });

            }
        });



        nextPageBtn.setOnClickListener(view1 -> {
            String inputtedName = Objects.requireNonNull(name.getText()).toString();
            String inputtedDateOfBirth = Objects.requireNonNull(dateOfBirth.getText()).toString();

            Intent intent=new Intent(RegisterActivityOne.this,RegisterActivityTwo.class);
            if (inputtedName.equals("")) {
                binding.textInputName.setError("You must provide your name");
            }
            if (inputtedDateOfBirth.equals("")) {
                binding.textInputDOB.setError("You must provide your date of birth");
            } else {
                binding.textInputName.setError(null);
                binding.textInputDOB.setError(null);
                HashMap<String,String> userDetails=new HashMap<>();
                String inputtedValue = Objects.requireNonNull(phoneNumber.getText()).toString();
                if (phoneNumber.getInputType() == InputType.TYPE_CLASS_PHONE){
                    userDetails.put("phoneNumber",inputtedValue);
                }else{
                    userDetails.put("email",inputtedValue);
                }
                userDetails.put("name",inputtedName);
                userDetails.put("dateOfBirth",inputtedDateOfBirth);

               intent.putExtra("userDetails",userDetails);
                startActivity(intent);
                finish();

            }

        });
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}