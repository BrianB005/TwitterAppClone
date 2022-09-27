package com.brianbett.twitter;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.brianbett.twitter.databinding.ActivityCreateImageTweetBinding;
import com.brianbett.twitter.retrofit.RetrofitHandler;
import com.brianbett.twitter.retrofit.Tweet;
import com.brianbett.twitter.retrofit.TweetInterface;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class CreateImageTweetActivity extends AppCompatActivity {

    Uri uri;
    StorageReference storageReference;
    ActivityCreateImageTweetBinding binding;
    MaterialButton createTweet,selectImage;
    ImageView selectedImage,profilePic;
    EditText tweetTitle;
    ProgressBar progressBar;
    ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCreateImageTweetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

       createTweet=binding.createTweet;
       selectImage=binding.selectImage;
       selectedImage=binding.selectedImage;
       tweetTitle=binding.tweetInput;
       profilePic=binding.profilePic;
       progressBar=binding.progressView;

       binding.closeActivity.setOnClickListener(view -> super.onBackPressed());
       launcher=
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),(ActivityResult result)->{
                if(result.getResultCode()==RESULT_OK){
                    assert result.getData() != null;
                    uri=result.getData().getData();
                    selectedImage.setVisibility(View.VISIBLE);
                    selectedImage.setImageURI(uri);
                    selectImage.setVisibility(View.GONE);
                    createTweet.setEnabled(true);
                    createTweet.setOnClickListener(view -> {
                        hideKeyboard(CreateImageTweetActivity.this);
                        String inputtedTitle=tweetTitle.getText().toString();
                        HashMap<String,String> tweetDetails=new HashMap<>();
                        String fileName=String.valueOf(System.currentTimeMillis());
                        storageReference= FirebaseStorage.getInstance().getReference("images/"+fileName);

                        progressBar.setVisibility(View.VISIBLE);
                        createTweet.setEnabled(false);
                        tweetDetails.put("images",fileName);
                        if(!inputtedTitle.equals("")){
                            tweetDetails.put("title",inputtedTitle);
                        }
                        storageReference.putFile(uri).addOnCompleteListener(task -> {
                            RetrofitHandler.createTweet(getApplicationContext(), tweetDetails, new TweetInterface() {
                                @Override
                                public void success(Tweet tweet) {
                                    selectedImage.setImageURI(null);
                                    tweetTitle.setText("");
                                    progressBar.setVisibility(View.GONE);
                                    selectImage.setVisibility(View.VISIBLE);
                                    createTweet.setEnabled(false);
                                    Toast.makeText(getApplicationContext(),"Tweet created!",Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void failure(Throwable throwable) {

                                    createTweet.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void errorExists() {

                                    createTweet.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }).addOnFailureListener(e -> {
                                Log.e("Error ",e.getMessage());


                        });
                    });

                }else if(result.getResultCode()== ImagePicker.RESULT_ERROR){
                   Log.d("Error","Something went wrong!");
                }
            });

       selectImage.setOnClickListener(view -> selectImage());





    }
    private void selectImage(){
        ImagePicker.Companion.with(CreateImageTweetActivity.this)
                .crop()
                .provider(ImageProvider.BOTH)
                .createIntentFromDialog(new Function1() {
                    public Object invoke(Object var1) {
                        this.invoke((Intent) var1);
                        return Unit.INSTANCE;
                    }

                    public void invoke(@NotNull Intent it) {
                        Intrinsics.checkNotNullParameter(it, "it");
                        launcher.launch(it);
                    }
                });

    };
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }




}