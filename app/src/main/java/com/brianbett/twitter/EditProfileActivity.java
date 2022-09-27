package com.brianbett.twitter;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brianbett.twitter.databinding.ActivityEditProfileBinding;
import com.brianbett.twitter.retrofit.RetrofitHandler;
import com.brianbett.twitter.retrofit.Tweet;
import com.brianbett.twitter.retrofit.TweetInterface;
import com.brianbett.twitter.retrofit.User;
import com.brianbett.twitter.retrofit.UserDetails;
import com.brianbett.twitter.retrofit.UserDetailsInterface;
import com.brianbett.twitter.retrofit.UserInterface;
import com.bumptech.glide.Glide;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class EditProfileActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> profileLauncher,headerLauncher;
    MaterialButton uploadProfile,uploadHeader;
    TextView cancelProfileUpdate,cancelHeaderUpdate,dateOfBirth;
    EditText changeName,changeUserName,changeDescription,changeLocation;
    ImageView profilePic,headerPic;
    ImageButton changeProfilePic,changeHeaderPic;
    boolean isUsernameAvailable;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityEditProfileBinding binding=ActivityEditProfileBinding.inflate(getLayoutInflater());
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        setContentView(binding.getRoot());



        uploadProfile=binding.uploadProfilePic;
        uploadHeader=binding.uploadHeader;
        cancelHeaderUpdate=binding.cancelHeaderUpload;
        cancelProfileUpdate=binding.cancelProfileUpload;
        dateOfBirth=binding.changeBirthDate;
        changeName=binding.changeName;
        changeUserName= binding.changeUserName;
        changeDescription=binding.changeDescription;
        changeLocation=binding.changeLocation;
        changeHeaderPic=binding.changeHeader;

        changeProfilePic=binding.changeProfile;

        profilePic= binding.profilePic;
        headerPic= binding.headerPic;
        isUsernameAvailable=false;

//        changeUserName.addTextChangedListener(new TextWatcher() {
//              @Override
//              public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                  changeUserName.setError(null);
//              }
//
//              @Override
//              public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                  HashMap<String, String> details = new HashMap<>();
//                  details.put("username", charSequence.toString());
//                  RetrofitHandler.checkUserDetails(details, new UserDetailsInterface() {
//                      @Override
//                      public void success() {
//
//                          isUsernameAvailable=true;
//
//                      }
//
//                      @Override
//                      public void errorExists(String errorMessage) {
//                          changeUserName.setError(errorMessage);
//                          isUsernameAvailable=false;
//                      }
//
//                      @Override
//                      public void failure(Throwable throwable) {
//                          Log.d("Exception", throwable.getMessage());
//                          isUsernameAvailable=false;
//                      }
//                  });
//              }
//
//              @Override
//              public void afterTextChanged(Editable editable) {
//
//              }
//          });


        Intent intent=getIntent();

        MaterialToolbar toolbar=binding.toolBar;

        toolbar.getChildAt(0).setOnClickListener(view -> super.onBackPressed());

        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId()==R.id.save_changes){
                HashMap<String,String> userDetails=new HashMap<>();
               item.setTitle("Saving...");
                String description=changeDescription.getText().toString();
                String name=changeName.getText().toString();
                String userName=changeUserName.getText().toString();
                String location=changeLocation.getText().toString();
                if(userName.isEmpty()){
                    changeUserName.setError("Invalid username! Your current username will be retained");
                }else{
                    HashMap<String,String> usernameMap=new HashMap<>();
                    RetrofitHandler.checkUserDetails(usernameMap, new UserDetailsInterface() {
                      @Override
                      public void success() {
                          userDetails.put("username",userName);
                      }

                      @Override
                      public void errorExists(String errorMessage) {
                          changeUserName.setError(errorMessage);

                      }

                      @Override
                      public void failure(Throwable throwable) {
                          Log.d("Exception", throwable.getMessage());

                      }
                  });

                }
                if(name.isEmpty()){
                    changeName.setError("Invalid name! Your current name will be retained");
                }else{
                    userDetails.put("name",name);
                }
                userDetails.put("location",location);
                userDetails.put("description",description);
                RetrofitHandler.updateUser(getApplicationContext(), userDetails, new UserInterface() {
                    @Override
                    public void success(User user) {

                        Toast.makeText(getApplicationContext(),"Details updated successfully!",Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void failure(Throwable throwable) {
                        Toast.makeText(getApplicationContext() ,"Something went wrong! Try again later.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void errorExists(String errorMessage) {

                        Log.e("Exception","Something went wrong!");
                    }
                });


            }
            return false;
        });

        UserDetails user=(UserDetails) intent.getSerializableExtra("user");

        String headerPic1=Preferences.getItemFromSP(getApplicationContext(),"headerPic");
        Glide.with(getApplicationContext()).load(headerPic1).into(headerPic);

        String profilePic1=Preferences.getItemFromSP(getApplicationContext(),"profilePic");
        Glide.with(getApplicationContext()).load(profilePic1).into(profilePic);

        changeName.setText(user.getName());
        changeUserName.setText(user.getUsername());
        changeLocation.setText(user.getLocation());
        changeDescription.setText(user.getDescription());
        dateOfBirth.setText(ConvertToDate.getFullMonthFormat(user.getDateOfBirth()));
       profileLauncher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),(ActivityResult result)->{
            if(result.getResultCode()==RESULT_OK){
                assert result.getData() != null;
                Uri uri=result.getData().getData();
                profilePic.setImageURI(uri);
                cancelProfileUpdate.setVisibility(View.VISIBLE);
                uploadProfile.setVisibility(View.VISIBLE);
                cancelProfileUpdate.setOnClickListener(view -> {
                    Glide.with(getApplicationContext()).load(profilePic1).into(profilePic);
                    cancelProfileUpdate.setVisibility(View.GONE);
                    uploadProfile.setVisibility(View.GONE);
                });

                uploadProfile.setOnClickListener(view -> {
                    uploadProfile.setEnabled(false);
                    cancelProfileUpdate.setVisibility(View.GONE);
                    uploadProfile.setText("Uploading...");
                    HashMap<String,String> userDetails=new HashMap<>();

                    String filename=String.valueOf(System.currentTimeMillis());
                    userDetails.put("profilePic",filename);
                    StorageReference storageReference=FirebaseStorage.getInstance().getReference("images/"+filename);
                    storageReference.putFile(uri).addOnCompleteListener(task -> {
                        RetrofitHandler.updateUser(getApplicationContext(), userDetails, new UserInterface() {
                            @Override
                            public void success(User user) {
                                uploadProfile.setVisibility(View.GONE);
                                cancelProfileUpdate.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),"Profile photo updated successfully!",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void failure(Throwable throwable) {
                                uploadProfile.setEnabled(true);
                                uploadProfile.setText("Upload");

                                Toast.makeText(getApplicationContext() ,"Something went wrong! Try again later.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void errorExists(String errorMessage) {
                                uploadProfile.setEnabled(true);
                                uploadProfile.setText("Upload");

                                Log.e("Exception","Something went wrong!");
                            }
                        });

                    }).addOnFailureListener(Throwable::printStackTrace);
                });
            }else if(result.getResultCode()== ImagePicker.RESULT_ERROR){
                Log.d("Error","Something went wrong!");
            }
        });
        headerLauncher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),(ActivityResult result)->{
            if(result.getResultCode()==RESULT_OK){
                assert result.getData() != null;
                Uri uri=result.getData().getData();
                headerPic.setImageURI(uri);
                cancelHeaderUpdate.setVisibility(View.VISIBLE);
                uploadHeader.setVisibility(View.VISIBLE);

                uploadHeader.setOnClickListener(view -> {
                    cancelHeaderUpdate.setVisibility(View.GONE);
                    String filename=String.valueOf(System.currentTimeMillis());
                    uploadHeader.setEnabled(false);
                    uploadHeader.setText("Uploading...");
                    HashMap<String,String> userDetails=new HashMap<>();
                    userDetails.put("headerPic",filename);
                    StorageReference storageReference=FirebaseStorage.getInstance().getReference("images/"+filename);
                    storageReference.putFile(uri).addOnCompleteListener(task -> {
                        RetrofitHandler.updateUser(getApplicationContext(), userDetails, new UserInterface() {
                            @Override
                            public void success(User user) {
                                uploadHeader.setVisibility(View.GONE);
                                cancelHeaderUpdate.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),"Header updated successfully!",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void failure(Throwable throwable) {
                                uploadHeader.setEnabled(true);
                                uploadHeader.setText("Upload");

                                Toast.makeText(getApplicationContext() ,"Something went wrong! Try again later.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void errorExists(String errorMessage) {

                                Log.e("Exception","Something went wrong!");
                            }
                        });

                    }).addOnFailureListener(Throwable::printStackTrace);


                });

                cancelHeaderUpdate.setOnClickListener(view -> {
                    Glide.with(getApplicationContext()).load(headerPic1).into(headerPic);
                    cancelHeaderUpdate.setVisibility(View.GONE);
                    uploadHeader.setVisibility(View.GONE);

                });



            }else if(result.getResultCode()== ImagePicker.RESULT_ERROR){
                Log.d("Error","Something went wrong!");
            }
        });

        changeProfilePic.setOnClickListener(view -> selectProfilePic());
        changeHeaderPic.setOnClickListener(view -> selectHeaderPic());




    }

    private  void selectProfilePic(){
        ImagePicker.Companion.with(EditProfileActivity.this)
                .crop()
                .provider(ImageProvider.BOTH)
                .createIntentFromDialog(new Function1() {
                    public Object invoke(Object var1) {
                        this.invoke((Intent) var1);
                        return Unit.INSTANCE;
                    }

                    public void invoke(@NotNull Intent it) {
                        Intrinsics.checkNotNullParameter(it, "it");
                        profileLauncher.launch(it);
                    }
                });
    }

    private  void selectHeaderPic(){
        ImagePicker.Companion.with(EditProfileActivity.this)
                .crop()
                .provider(ImageProvider.BOTH)
                .createIntentFromDialog(new Function1() {
                    public Object invoke(Object var1) {
                        this.invoke((Intent) var1);
                        return Unit.INSTANCE;
                    }

                    public void invoke(@NotNull Intent it) {
                        Intrinsics.checkNotNullParameter(it, "it");
                        headerLauncher.launch(it);
                    }
                });
    }
}