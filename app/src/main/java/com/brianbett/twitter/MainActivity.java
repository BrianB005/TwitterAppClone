package com.brianbett.twitter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.brianbett.twitter.databinding.ActivityMainBinding;
import com.brianbett.twitter.retrofit.UserDetails;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.common.reflect.TypeToken;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomTabs;
    ActionBarDrawerToggle  actionBarDrawerToggle;
    DrawerLayout drawerLayout;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view= binding.getRoot();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        setContentView(view);

        bottomTabs= binding.bottomNavigationView;

        HomeFragment homeFragment=new HomeFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();

        bottomTabs.setOnItemSelectedListener(item -> {
            Fragment fragment;
            switch (item.getItemId()){
                case R.id.search:
                    fragment=new SearchFragment();
                    break;
                case R.id.spaces:
                    fragment=new SpacesFragment();
                    break;
                case R.id.notification:
                    fragment=new NotificationsFragment();
                    break;
                case R.id.messages:
                    fragment=new MessagesFragment();

                default:
                    fragment=new HomeFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
            return false;
        });



        drawerLayout=binding.getRoot();
        actionBarDrawerToggle
                =new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


//        retrieving userDetails from shared Preferences
        Gson gson=new Gson();
        String usersList=Preferences.getItemFromSP(getApplicationContext(),"usersList");
        Type type=new TypeToken<ArrayList<SharedPrefUser>>(){}.getType();
        ArrayList<SharedPrefUser> savedUsers=gson.fromJson(usersList,type);


        NavigationView navigationView=binding.navigationView;
        ImageView toolbarProfilePic=binding.profilePic;
        toolbarProfilePic.setOnClickListener(view1->drawerLayout.open());




        View header=navigationView.getHeaderView(0);
        View followers=header.findViewById(R.id.open_followers);
        View following=header.findViewById(R.id.open_following);
        TextView followingCount=header.findViewById(R.id.following);
        TextView followersCount=header.findViewById(R.id.followers);
        ImageView profilePic=header.findViewById(R.id.profile_pic);

        for(SharedPrefUser sharedPrefUser:savedUsers){
            if(sharedPrefUser.isActive()){
                UserDetails userDetails= sharedPrefUser.getUserDetails();
                navigationView.setNavigationItemSelectedListener(item -> {
                    switch (item.getItemId()){
                        case R.id.open_profile:
                            Intent intent=new Intent(MainActivity.this,MyProfileActivity.class);
                            intent.putExtra("user", userDetails);
                            startActivity(intent);
                            break;
//                    case R.id.open_lists:
//                        startActivity(new Intent(MainActivity.this,TweetActivity.class));
//                        break;
//
//                    case R.id.open_bookmarks:
//                        startActivity(new Intent(MainActivity.this,ProfileActivity.class));
//                        break;
                        case R.id.open_moments:
                            startActivity(new Intent(MainActivity.this,SettingsActivity.class));
                            break;

                    }
                    return false;
                });
                StorageReference storageReference= FirebaseStorage.getInstance().getReference("images/"+userDetails.getProfilePic());
                storageReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                    Glide.with(getApplicationContext()).load(uri1).into(profilePic);
                    Glide.with(getApplicationContext()).load(uri1).into(toolbarProfilePic);


                });

                TextView name=header.findViewById(R.id.name);
                TextView username=header.findViewById(R.id.username);
                name.setText(userDetails.getName());
                username.setText("@"+userDetails.getUsername());
                followingCount.setText(String.valueOf(userDetails.getFollowing().size()));
                followersCount.setText(String.valueOf(userDetails.getFollowers().size()));
            }
        }





        followers.setOnClickListener(view1->startActivity(new Intent(MainActivity.this,FollowersActivity.class)));
        following.setOnClickListener(view1->startActivity(new Intent(MainActivity.this,FollowingActivity.class)));

        header.findViewById(R.id.show_accounts).setOnClickListener(view1->{
            @SuppressLint("InflateParams") View popUpView=getLayoutInflater().inflate(R.layout.switch_accounts_popup,null);
            PopupWindow popupWindow=new PopupWindow(popUpView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
            popupWindow.showAtLocation(view, Gravity.BOTTOM,0,0);
//            binding.getRoot().getForeground().setAlpha(90);
//            popupWindow.setOnDismissListener(() -> binding.getRoot().getForeground().setAlpha(0));
//            showing list of saved users
            SavedUsersRecyclerViewAdapter usersRecyclerViewAdapter=new SavedUsersRecyclerViewAdapter(savedUsers,MainActivity.this);
            RecyclerView recyclerView=popUpView.findViewById(R.id.users_recycler_view);
            recyclerView.setAdapter(usersRecyclerViewAdapter);

            popUpView.findViewById(R.id.create_new_account).setOnClickListener(view2 -> startActivity(new Intent(MainActivity.this,RegisterActivityOne.class)));
            popUpView.findViewById(R.id.open_login).setOnClickListener(view2 -> startActivity(new Intent(MainActivity.this,LoginActivity.class)));

        });



    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        actionBarDrawerToggle.syncState();
    }
}