package com.example.pilgrimapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.List;

public class SignWelcom extends Activity {
    FirebaseInstanceId firebaseInstanceId;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database_token;
    private List<TokenRD> tokenList=new ArrayList<>();
    Button btn_login_move;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_welcom);
        database_token=FirebaseDatabase.getInstance();
        databaseReference=database_token.getReference("tokens");
        firebaseInstanceId= FirebaseInstanceId.getInstance();
        btn_login_move=findViewById(R.id.btn_login_move);
        firebaseInstanceId.getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w("FIREBASE", "getInstanceId failed", task.getException());
                    return;
                }
                TokenRD tokenRD=new TokenRD();
                tokenRD.token= task.getResult().getToken();
                databaseReference.push().setValue(tokenRD);
            }

        });
        btn_login_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplication(),Login.class);
                startActivity(intent);

            }
        });
    }

    @Override//background 클릭시 activity 종료 방지
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Rect dialogBounds=new Rect();
        getWindow().getDecorView().getHitRect(dialogBounds);
        if(!dialogBounds.contains((int)ev.getX(),(int)ev.getY())){
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }
}
