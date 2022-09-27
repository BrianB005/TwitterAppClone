package com.brianbett.twitter;

import androidx.annotation.Nullable;

import com.brianbett.twitter.retrofit.User;

import java.util.Objects;

public class SharedPrefUser extends User {
    private boolean isActive=false;

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(this==obj) return true;
        else if(obj instanceof User){
            return Objects.equals(this.getUserDetails().getUserId(), ((User) obj).getUserDetails().getUserId());
        }
        return false;
    }
}
