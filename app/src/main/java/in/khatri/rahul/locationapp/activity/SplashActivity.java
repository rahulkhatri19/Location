package in.khatri.rahul.locationapp.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import in.khatri.rahul.locationapp.R;
import in.khatri.rahul.locationapp.utils.SharedPreferenceUtils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (new SharedPreferenceUtils(SplashActivity.this).getLoginFlag()){
                    if (new SharedPreferenceUtils(SplashActivity.this).getLogin().equals("admin")){
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, UserActivity.class));
                    }
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
            }
        },1500);
    }
}
