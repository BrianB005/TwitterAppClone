package com.brianbett.twitter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.brianbett.twitter.retrofit.FollowUserSuccess;
import com.brianbett.twitter.retrofit.RetrofitHandler;
import com.brianbett.twitter.retrofit.UserDetails;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ProfileViewPagerAdapter viewPagerAdapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        setContentView(R.layout.activity_profile);
        MaterialToolbar toolbar=findViewById(R.id.tool_bar);


        toolbar.getChildAt(0).setOnClickListener(view -> super.onBackPressed());
        TextView dateOfBirth ,profileDescription,followers,following,location,name,username,joinedOn;
        ImageView headerPic ,profilePic;
        MaterialButton followButton,followingButton,followBackButton;

        location=findViewById(R.id.location);
        dateOfBirth=findViewById(R.id.date_of_birth);
        followers=findViewById(R.id.followers);
        following=findViewById(R.id.following);
        profileDescription=findViewById(R.id.profile_description);

        headerPic=findViewById(R.id.header_pic);
        profilePic=findViewById(R.id.profile_pic);
        followBackButton=findViewById(R.id.follow_back_btn);
        followButton=findViewById(R.id.follow_btn);
        followingButton=findViewById(R.id.following_btn);
        name=findViewById(R.id.name);
        joinedOn=findViewById(R.id.joined_on);
        username=findViewById(R.id.username);





        tabLayout=findViewById(R.id.tabsLayout);
        viewPager2 = findViewById(R.id.container);


        Intent intent=getIntent();
        UserDetails userDetails=(UserDetails) intent.getSerializableExtra("user");
        StorageReference storageReference= FirebaseStorage.getInstance().getReference("images/"+userDetails.getProfilePic());

        Task<Uri> uriTask=storageReference.getDownloadUrl();

        uriTask.addOnSuccessListener(uri1 -> {
            Glide.with(getApplicationContext()).load(uri1).into(profilePic);

        });
        StorageReference storageReference1= FirebaseStorage.getInstance().getReference("images/"+userDetails.getHeaderPic());

        Task<Uri> uriTask1=storageReference1.getDownloadUrl();

        uriTask1.addOnSuccessListener(uri1 -> {
            Glide.with(getApplicationContext()).load(uri1).into(headerPic);
        });

        following.setText(String.valueOf(userDetails.getFollowing().size()));
        followers.setText(String.valueOf(userDetails.getFollowers().size()));
        profileDescription.setText(userDetails.getDescription());
        if(userDetails.getLocation()!=null){
            location.setText(userDetails.getLocation());
        }else{
            location.setVisibility(View.GONE);
        }
        joinedOn.setText("Joined "+ConvertToDate.getFullMonthFormat(userDetails.getJoinedOn()));
        dateOfBirth.setText("Born "+ConvertToDate.getFullMonthFormat(userDetails.getDateOfBirth()));
        name.setText(userDetails.getName());
        username.setText(userDetails.getUsername());

        View openFollowers,openFollowing;

        openFollowers=findViewById(R.id.open_followers);
        openFollowing=findViewById(R.id.open_following);

        openFollowers.setOnClickListener(view1->{
            Intent intent1=new Intent(ProfileActivity.this,FollowersActivity.class);
            intent1.putExtra("userId",userDetails.getUserId());
            startActivity(intent1);
        });
        openFollowing.setOnClickListener(view1->{
            Intent intent1=new Intent(ProfileActivity.this,FollowingActivity.class);
            intent1.putExtra("userId",userDetails.getUserId());
            startActivity(intent1);
        });
//        passing userId to the fragments
        Bundle bundle=new Bundle();
        bundle.putString("userId",userDetails.getUserId());
        viewPagerAdapter = new ProfileViewPagerAdapter(this, tabLayout,bundle);
        viewPager2.setAdapter(viewPagerAdapter);


        String currentUserId=Preferences.getItemFromSP(getApplicationContext(),"userId");


        if(userDetails.getFollowing().contains(currentUserId)){


            if(userDetails.getFollowers().contains(currentUserId)){
                followButton.setVisibility(View.GONE);
                followBackButton.setVisibility(View.GONE);
                followingButton.setVisibility(View.VISIBLE);
            }else{
                followButton.setVisibility(View.GONE);
                followingButton.setVisibility(View.GONE);
                followBackButton.setVisibility(View.VISIBLE);
            }



        }else{
            if(userDetails.getFollowers().contains(currentUserId)){
                followButton.setVisibility(View.GONE);
                followBackButton.setVisibility(View.GONE);
                followingButton.setVisibility(View.VISIBLE);
            }else{
                followButton.setVisibility(View.VISIBLE);
                followingButton.setVisibility(View.GONE);
                followBackButton.setVisibility(View.GONE);
            }
        }

        followButton.setOnClickListener(view1 -> {
            followButton.setVisibility(View.GONE);
            followingButton.setVisibility(View.VISIBLE);
            followingButton.setEnabled(false);
            RetrofitHandler.followUser(getApplicationContext(), userDetails.getUserId(), new FollowUserSuccess() {
                @Override
                public void success(String message) {
                    followingButton.setEnabled(true);
                }
                @Override
                public void errorExists() {

                    followingButton.setEnabled(true);
                }
                @Override
                public void failure(Throwable throwable) {
                    followingButton.setEnabled(true);
                }
            });
        });
        followBackButton.setOnClickListener(view1 -> {
            followingButton.setEnabled(false);
            followBackButton.setVisibility(View.GONE);
            followingButton.setVisibility(View.VISIBLE);
            RetrofitHandler.followUser(getApplicationContext(), userDetails.getUserId(), new FollowUserSuccess() {
                @Override
                public void success(String message) {
                    followingButton.setEnabled(true);
                }

                @Override
                public void errorExists() {

                    followingButton.setEnabled(true);
                }

                @Override
                public void failure(Throwable throwable) {

                    followingButton.setEnabled(true);
                }
            });
        });


        followingButton.setOnClickListener(view -> {
            followingButton.setVisibility(View.GONE);
            String userId=userDetails.getUserId();
            RetrofitHandler.unFollowUser(getApplicationContext(), userId, new FollowUserSuccess() {
                @Override
                public void success(String message) {


                }

                @Override
                public void errorExists() {


                }

                @Override
                public void failure(Throwable throwable) {


                }
            });

        });


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition(), true);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
//updating the tabs when the page/tab changes as a result of scroll or swipe


        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Objects.requireNonNull(tabLayout.getTabAt(position)).select();
            }
        });
    }
}