package com.brianbett.twitter;

import androidx.appcompat.app.AppCompatActivity;

import androidx.viewpager2.widget.ViewPager2;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.brianbett.twitter.retrofit.UserDetails;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class MyProfileActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    MyProfileViewPagerAdapter viewPagerAdapter;
    MaterialButton editProfile;
    TextView dateOfBirth ,profileDescription,followers,following,location,name,username,joinedOn;
    ImageView headerPic ,profilePic;
    View openFollowing,openFollowers;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        setContentView(R.layout.activity_my_profile);
        MaterialToolbar toolbar=findViewById(R.id.tool_bar);

        toolbar.getChildAt(0).setOnClickListener(view -> super.onBackPressed());

        location=findViewById(R.id.location);
        dateOfBirth=findViewById(R.id.date_of_birth);
        followers=findViewById(R.id.followers);
        following=findViewById(R.id.following);
        profileDescription=findViewById(R.id.profile_description);

        headerPic=findViewById(R.id.header_pic);
        profilePic=findViewById(R.id.profile_pic);
        name=findViewById(R.id.name);
        joinedOn=findViewById(R.id.joined_on);
        username=findViewById(R.id.username);

        openFollowers=findViewById(R.id.open_followers);
        openFollowing=findViewById(R.id.open_following);

        openFollowers.setOnClickListener(view1->startActivity(new Intent(MyProfileActivity.this,FollowersActivity.class)));
        openFollowing.setOnClickListener(view1->startActivity(new Intent(MyProfileActivity.this,FollowingActivity.class)));


        Intent intent=getIntent();
        UserDetails userDetails=(UserDetails) intent.getSerializableExtra("user");
        StorageReference storageReference= FirebaseStorage.getInstance().getReference("images/"+userDetails.getProfilePic());

        Task<Uri> uriTask=storageReference.getDownloadUrl();

        uriTask.addOnSuccessListener(uri1 -> {
            Glide.with(getApplicationContext()).load(uri1).into(profilePic);
            Preferences.saveItemToSP(getApplicationContext(),"profilePic",uri1.toString());

        });
        StorageReference storageReference1= FirebaseStorage.getInstance().getReference("images/"+userDetails.getHeaderPic());

        Task<Uri> uriTask1=storageReference1.getDownloadUrl();

        uriTask1.addOnSuccessListener(uri1 -> {
            Glide.with(getApplicationContext()).load(uri1).into(headerPic);
            Preferences.saveItemToSP(getApplicationContext(),"headerPic",uri1.toString());

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
        username.setText("@"+userDetails.getUsername());

        editProfile=findViewById(R.id.edit_profile);

        editProfile.setOnClickListener(view -> {
            Intent intent1=new Intent(MyProfileActivity.this,EditProfileActivity.class);
            intent1.putExtra("user",userDetails);
            startActivity(intent1);
        });

        tabLayout=findViewById(R.id.tabsLayout);
        viewPager2 = findViewById(R.id.container);
        viewPagerAdapter = new MyProfileViewPagerAdapter(this, tabLayout);
        viewPager2.setAdapter(viewPagerAdapter);




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




    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}