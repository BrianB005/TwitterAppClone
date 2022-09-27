package com.brianbett.twitter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.brianbett.twitter.databinding.ActivityLoginBinding;
import com.brianbett.twitter.retrofit.User;
import com.brianbett.twitter.retrofit.UserDetails;
import com.brianbett.twitter.retrofit.UserInterface;
import com.brianbett.twitter.retrofit.RetrofitHandler;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MaterialButton changeInputTypeBtn, loginBtn;
        TextInputEditText passwordField, phoneNumberOrEmail;
        TextInputLayout passwordLayout,emailLayout;

        changeInputTypeBtn=binding.changeInputType;
        loginBtn=binding.loginBtn;
        passwordField=binding.inputPassword;
        phoneNumberOrEmail=binding.inputPhoneNumber;
        passwordLayout=binding.textInputPassword;
        emailLayout=binding.textInputPhone;


        changeInputTypeBtn.setOnClickListener((view2) -> {
            if (phoneNumberOrEmail.getInputType() == InputType.TYPE_CLASS_PHONE) {
                phoneNumberOrEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                changeInputTypeBtn.setText("Use phone number instead");
            } else {
                phoneNumberOrEmail.setInputType(InputType.TYPE_CLASS_PHONE);
                changeInputTypeBtn.setText("Use email instead");
            }
        });

        passwordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                phoneNumberOrEmail.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.length()<8){
                    passwordLayout.setError("Password must be 8 characters or more");
                }else{
                    passwordLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        View progressView=binding.loginProgress;

        phoneNumberOrEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                phoneNumberOrEmail.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        loginBtn.setOnClickListener(view->{
            if(Objects.requireNonNull(phoneNumberOrEmail.getText()).toString().equals("")){
                emailLayout.setError("You must provide an email or phone number!");
            }else {
                HashMap<String, String> details = new HashMap<>();
                assert phoneNumberOrEmail.getText() != null;
                if (phoneNumberOrEmail.getInputType() == InputType.TYPE_CLASS_PHONE) {
                    details.put("phoneNumber", phoneNumberOrEmail.getText().toString());
                } else {
                    details.put("email", phoneNumberOrEmail.getText().toString());
                }

                String password = Objects.requireNonNull(passwordField.getText()).toString();
                details.put("password", password);

                progressView.setVisibility(View.VISIBLE);
                loginBtn.setEnabled(false);
                RetrofitHandler.loginUser(details,  new UserInterface() {
                    @Override
                    public void success(User user) {
                        progressView.setVisibility(View.VISIBLE);
                        Preferences.saveItemToSP(getApplicationContext(),"token", user.getToken());
                        Preferences.saveItemToSP(getApplicationContext(),"userId", user.getUserDetails().getUserId());


                        Gson gson=new Gson();
                        List<SharedPrefUser> newUsersList=new ArrayList<>();
                        String usersList=Preferences.getItemFromSP(getApplicationContext(),"usersList");
                        Type type = new TypeToken<ArrayList<SharedPrefUser>>() {
                        }.getType();
                        ArrayList<SharedPrefUser> usersArrayList=gson.fromJson(usersList,type);

                        SharedPrefUser userToSave=new SharedPrefUser();
                        userToSave.setActive(true);
                        UserDetails userDetails=new UserDetails();
                        userDetails.setFollowers(user.getUserDetails().getFollowers());
                        userDetails.setFollowing(user.getUserDetails().getFollowing());
                        userDetails.setUserId(user.getUserDetails().getUserId());
                        userDetails.setName(user.getUserDetails().getName());
                        userDetails.setUsername(user.getUserDetails().getUsername());
                        userToSave.setToken(user.getToken());
                        userDetails.setProfilePic(user.getUserDetails().getProfilePic());
                        userDetails.setHeaderPic(user.getUserDetails().getHeaderPic());
                        userDetails.setDescription(user.getUserDetails().getDescription());
                        userDetails.setDateOfBirth(user.getUserDetails().getDateOfBirth());
                        userDetails.setJoinedOn(user.getUserDetails().getJoinedOn());



                        userToSave.setUserDetails(userDetails);

                        if(usersArrayList==null){
                            newUsersList.add(userToSave);
                        }else {
                            for (SharedPrefUser sharedPrefUser : usersArrayList) {
                                sharedPrefUser.setActive(false);
                                if (!sharedPrefUser.equals(user)) {
                                    newUsersList.add(userToSave);
                                }else sharedPrefUser.setActive(sharedPrefUser.equals(user));


                            }



                            newUsersList.addAll(usersArrayList);

                        }



                        String newUsersListString=gson.toJson(newUsersList);
                        Preferences.saveItemToSP(getApplicationContext(),"usersList",newUsersListString);
                        Preferences.saveItemToSP(getApplicationContext(),"token", user.getToken());
                        Preferences.saveItemToSP(getApplicationContext(),"userId", user.getUserDetails().getUserId());
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }
                    @Override
                    public void failure(Throwable throwable) {
                        progressView.setVisibility(View.GONE);
                        loginBtn.setEnabled(true);
                        Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void errorExists(String errorMessage) {

                        loginBtn.setEnabled(true);
                        progressView.setVisibility(View.GONE);
                        if(errorMessage.equals("Wrong password")){
                            passwordLayout.setError("Wrong password!");
                        }else{
                            emailLayout.setError("The provided email or phone number hasn't been registered!");
                        }
                    }
                });
            }
        });
    }
}