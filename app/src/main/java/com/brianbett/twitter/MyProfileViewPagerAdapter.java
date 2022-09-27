package com.brianbett.twitter;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;

public class MyProfileViewPagerAdapter extends FragmentStateAdapter {
    TabLayout tabLayout;
    public MyProfileViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, TabLayout layoutTab) {
        super(fragmentActivity);
        tabLayout=layoutTab;
    }


    @SuppressLint("NonConstantResourceId")
    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 1:
                return new MyProfileTweetsAndRepliesFragment();

            case 2:
                return new MyProfileMediaFragment();
            case 3:
                return new MyProfileLikesFragment();

            default:
                return new MyProfileTweetsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return tabLayout.getTabCount();
    }
}
