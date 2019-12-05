package com.example.pilgrimapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class Intro extends AppCompatActivity {

    Handler handler;

    Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            Intent intent = new Intent(Intro.this,Login.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        /**********************************************단말 id 체크 (FCM)*************************************************/
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult result) {
                Log.d("FCM_TEST",result.getToken().toString());
            }
        });
        /**********************************************단말 id 체크 (FCM)*************************************************/

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//정방향 세로로 완전히 고정,회전불가
        handler = new Handler();
        handler.postDelayed(runnable, 1700);
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        handler.removeCallbacks(runnable);
    }


}
