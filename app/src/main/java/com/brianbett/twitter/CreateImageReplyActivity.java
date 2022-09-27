package com.brianbett.twitter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brianbett.twitter.databinding.ActivityCreateImageReplyBinding;
import com.brianbett.twitter.databinding.ActivityCreateImageTweetBinding;
import com.brianbett.twitter.retrofit.CommentInterface;
import com.brianbett.twitter.retrofit.ProfileTweetOrComment;
import com.brianbett.twitter.retrofit.RetrofitHandler;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class CreateImageReplyActivity extends AppCompatActivity {
    ActivityCreateImageReplyBinding binding;
    MaterialButton replyBtn;
    ImageView selectedImage,profilePic;
    EditText tweetTitle;
    TextView replyingTo;
    EditText commentInput;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding=ActivityCreateImageReplyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        replyBtn=binding.createReply;
        selectedImage=binding.selectedImage;
        tweetTitle=binding.commentInput;
        profilePic=binding.profilePic;
        replyingTo=binding.replyUsername;
        commentInput=binding.commentInput;


        binding.closeActivity.setOnClickListener(view -> super.onBackPressed());

        Intent intent=getIntent();
        String replyUsername=intent.getStringExtra("replyUsername");
        String image=intent.getStringExtra("image");
        String tweetId=intent.getStringExtra("tweetId");

        replyingTo.setText("@"+replyUsername);
        Glide.with(CreateImageReplyActivity.this).load(image).into(selectedImage);


        replyBtn.setOnClickListener((view)-> {
            hideKeyboard(CreateImageReplyActivity.this);

            replyBtn.setText("Sending");
            HashMap<String, String> commentDetails = new HashMap<>();
            String commentTitle=commentInput.getText().toString();
            if(!commentTitle.isEmpty()){
                commentDetails.put("title", commentTitle);
            }
            String fileName=String.valueOf(System.currentTimeMillis());
            commentDetails.put("images", fileName);
            StorageReference storageReference= FirebaseStorage.getInstance().getReference("images/"+fileName);
            storageReference.putFile(Uri.parse(image)).addOnCompleteListener(task -> RetrofitHandler.createComment(getApplicationContext(), tweetId, commentDetails, new CommentInterface() {
                @Override
                public void success(ProfileTweetOrComment comment) {
                    Toast toast = new Toast(getApplicationContext());
                    View customToast = getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.customToast), false);
                    toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 40);

                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(customToast);
                    toast.show();
                    replyBtn.setText("Reply");

                    replyBtn.setEnabled(true);
                    commentInput.setText("");
                    selectedImage.setImageURI(null);


                }

                @Override
                public void failure(Throwable throwable) {
                    replyBtn.setText("Reply");

                    replyBtn.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    Log.d("Exception", throwable.getMessage());
                }

                @Override
                public void errorExists(String errorMessage) {
                    replyBtn.setEnabled(true);
                    replyBtn.setText("Reply");
                    Toast.makeText(getApplicationContext(), "Oops! Something went wrong!", Toast.LENGTH_SHORT).show();

                    Log.d("Error", errorMessage);
                }
            })).addOnFailureListener(e -> Log.d("Error",e.getMessage()));


        });
    }





    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}