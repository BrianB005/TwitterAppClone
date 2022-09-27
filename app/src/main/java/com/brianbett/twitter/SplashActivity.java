package com.brianbett.twitter;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.brianbett.twitter.retrofit.RetrofitHandler;
import com.brianbett.twitter.retrofit.UserDetailsInterface;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        String token=Preferences.getItemFromSP(getApplicationContext(),"token");

        new Handler().postDelayed(() -> {
            if(token.isEmpty()){
                startActivity(new Intent(SplashActivity.this,RegisterActivityOne.class));
            }else{
                RetrofitHandler.getCurrentUser(getApplicationContext(), new UserDetailsInterface() {
                    @Override
                    public void success() {
                        Log.d("Success","Success");
                    }

                    @Override
                    public void errorExists(String errorMessage) {

                        Log.e("Error",errorMessage);
                    }

                    @Override
                    public void failure(Throwable throwable) {

                        Log.d("Exception",throwable.getMessage());
                    }
                });
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
            }
            finish();
        },2000);
    }
}