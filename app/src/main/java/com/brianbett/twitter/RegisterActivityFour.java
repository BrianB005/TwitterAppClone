package com.brianbett.twitter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.brianbett.twitter.databinding.ActivityRegisterFourBinding;
import com.brianbett.twitter.retrofit.User;
import com.brianbett.twitter.retrofit.UserDetails;
import com.brianbett.twitter.retrofit.UserInterface;
import com.brianbett.twitter.retrofit.RetrofitHandler;
import com.brianbett.twitter.retrofit.UserDetailsInterface;
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

public class RegisterActivityFour extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRegisterFourBinding binding=ActivityRegisterFourBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        MaterialButton nextActivityBtn=binding.nextActivity;
        TextInputLayout usernameLayout=binding.textInputUserName;
        TextInputEditText usernameInput=binding.inputtedUsername;
        View progressView=binding.registrationProgress;

        Intent intent=getIntent();
        HashMap<String,String> detailsHashMap= (HashMap<String, String>) intent.getSerializableExtra("userDetails");
        nextActivityBtn.setEnabled(false);
        usernameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                HashMap<String,String> details=new HashMap<>();
                details.put("username",editable.toString());
                RetrofitHandler.checkUserDetails(details, new UserDetailsInterface() {
                    @Override
                    public void success() {
                        nextActivityBtn.setEnabled(true);
                        usernameLayout.setError(null);

                    }
                    @Override
                    public void errorExists(String errorMessage) {
                        usernameLayout.setError(errorMessage);
                    }
                    @Override
                    public void failure(Throwable throwable) {
                        Log.d("Exception", throwable.getMessage());
                    }
                });
            }
        });

        nextActivityBtn.setOnClickListener(view->{
            String username= Objects.requireNonNull(usernameInput.getText()).toString();
            detailsHashMap.put("username",username);
            progressView.setVisibility(View.VISIBLE);
            RetrofitHandler.registerUser(detailsHashMap,  new UserInterface() {
                @Override
                public void success(User user) {
                    progressView.setVisibility(View.GONE);
                    Gson gson = new Gson();
                    String userString = gson.toJson(user.getUserDetails());

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


                    startActivity(new Intent(RegisterActivityFour.this,RegisterActivityFive.class));
                    finish();

                }
                @Override
                public void failure(Throwable throwable) {
                    progressView.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void errorExists(String errorMessage) {

                    progressView.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
                }
            });

        });
    }
}