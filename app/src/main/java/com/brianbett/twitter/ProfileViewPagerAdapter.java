package com.brianbett.twitter;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.brianbett.twitter.ProfileTweetsFragment;
import com.google.android.material.tabs.TabLayout;

public class ProfileViewPagerAdapter extends FragmentStateAdapter {
    TabLayout tabLayout;
    Bundle bundle;
    public ProfileViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, TabLayout layoutTab, Bundle bundle) {
        super(fragmentActivity);
        tabLayout=layoutTab;
        this.bundle=bundle;
    }


    @SuppressLint("NonConstantResourceId")
    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 1:
                ProfileTweetsAndRepliesFragment tweetsAndRepliesFragment=new  ProfileTweetsAndRepliesFragment();
                tweetsAndRepliesFragment.setArguments(bundle);
                return tweetsAndRepliesFragment;

            case 2:
                ProfileMediaFragment mediaFragment=new ProfileMediaFragment();
                mediaFragment.setArguments(bundle);
                return mediaFragment;
            case 3:
                ProfileLikesFragment likesFragment=new ProfileLikesFragment();
                likesFragment.setArguments(bundle);
                return likesFragment;

            default:
                ProfileTweetsFragment tweetsFragment=new ProfileTweetsFragment();
                tweetsFragment.setArguments(bundle);
                return tweetsFragment;
        }
    }

    @Override
    public int getItemCount() {
        return tabLayout.getTabCount();
    }
}
