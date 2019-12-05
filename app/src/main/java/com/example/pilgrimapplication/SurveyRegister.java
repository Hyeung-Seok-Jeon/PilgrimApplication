package com.example.pilgrimapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurveyRegister extends Activity {
    static RequestQueue requestQueue;
    FirebaseDatabase database;
    private List<TokenRD> tokenList=new ArrayList<>();
    Button btn_noti_register;
    EditText edit_title,edit_message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_register);
        database= FirebaseDatabase.getInstance();
        btn_noti_register=findViewById(R.id.btn_noti_register);
        edit_title=findViewById(R.id.edit_title);
        edit_message=findViewById(R.id.edit_body_text);
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        database.getReference("token").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tokenList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    TokenRD tokenRD=snapshot.getValue(TokenRD.class);
                    tokenList.add(tokenRD);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btn_noti_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    String message=edit_message.getText().toString();
                    send(message);
            }
        });
    }

    public void send(String input) {
        JSONObject requestData = new JSONObject();

        try {
            requestData.put("priority", "high");

            JSONObject dataObj = new JSONObject();
            dataObj.put("contents", input);

            requestData.put("data", dataObj);
            JSONArray idArray = new JSONArray();
            for(int i=0;i<tokenList.size();i++) {
                idArray.put(i, tokenList.get(i).token);
            }
//            idArray.put(1,regId);
            requestData.put("registration_ids", idArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendData(requestData, new SendResponseListener() {
            @Override
            public void onRequestCompleted() {
            }
            @Override
            public void onRequestStarted() {
            }
            @Override
            public void onRequestWithError(VolleyError error) {

            }
        });
    }

    public interface SendResponseListener {
        public void onRequestStarted();

        public void onRequestCompleted();

        public void onRequestWithError(VolleyError error);
    }
    public void sendData(JSONObject requestData, final SendResponseListener listener) {
        JsonObjectRequest request = new JsonObjectRequest(

                Request.Method.POST, "https://fcm.googleapis.com/fcm/send", requestData,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onRequestCompleted();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onRequestWithError(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "key=AAAA5hmSLuY:APA91bEiJsRvgaq9Z8jowaXbyVvNPWoJOMfqu3VsY98tMo911F8QP-oxF7MorieTn9raJw5Tand9ruKg63apQYd8ERNa8RocTVAWaKUI2oYozRVxCCl3gBzRC3tPp4fBHi_qaTE5UDSw");
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        request.setShouldCache(false);
        listener.onRequestStarted();
        requestQueue.add(request);
    }
}
