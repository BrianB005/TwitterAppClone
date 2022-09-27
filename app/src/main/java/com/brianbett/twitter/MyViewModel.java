package com.brianbett.twitter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;

public class MyViewModel extends ViewModel {
    private MutableLiveData<HashMap<String,String>> authDetails;

    public MutableLiveData<HashMap<String,String>>getAuthDetails(){
        if(authDetails==null){
            authDetails=new MutableLiveData<>();
        }
        return authDetails;
    }
}
