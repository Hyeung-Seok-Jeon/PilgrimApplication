package com.example.pilgrimapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MemberShip extends AppCompatActivity {
    private static String IP_ADDRESS = "www.next-table.com";
    private static String TAG = "phptest";
    String[] items = {"월", "01", "02", "03", "04", "05", "06", "07","08","09","10","11","12"};
    private EditText m_edit_id,m_edit_pword,m_edit_name,m_edit_pwordCheck,m_edit_year,m_edit_day,m_edit_phone;
    private Spinner spinner;
    String spinnerName;
    Button joinbtn,btnDupp;
    RadioButton m_radio_male,m_radio_female;
    String gender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_ship);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//정방향 세로로 완전히 고정,회전불가

        findId();


        //비밀번호 암호화
        m_edit_pword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        m_edit_pword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        m_edit_pwordCheck.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        m_edit_pwordCheck.setTransformationMethod(PasswordTransformationMethod.getInstance());

        m_radio_male.setChecked(true);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                spinnerName=adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnDupp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = m_edit_id.getText().toString();
                IDChk idChk = new IDChk(id,"http://" + IP_ADDRESS + "/pilgrimproject/idChk.php",MemberShip.this);
                idChk.execute();
            }
        });

        joinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = m_edit_name.getText().toString();
                String id = m_edit_id.getText().toString();
                String pword = m_edit_pword.getText().toString();
                String pwordCheck=m_edit_pwordCheck.getText().toString();
                String phone = m_edit_phone.getText().toString();
                String YY = m_edit_year.getText().toString();
                String DD =m_edit_day.getText().toString();


                String Birth = YY+spinnerName+DD;
                if(m_radio_male.isChecked())
                    gender="남자";
                else if(m_radio_female.isChecked())
                    gender="여자";

                if (pword.equals(pwordCheck))
                {
                    InsertData task = new InsertData();
                    task.execute("http://" + IP_ADDRESS + "/pilgrimproject/insert.php", name, id, pword, phone, Birth, gender);
                }
                else{
                    Toast.makeText(getApplication(), "비밀번호가 같지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }




    class InsertData extends AsyncTask<String, String, String>
    {

        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MemberShip.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result.equals("1")) {
                Intent intent = new Intent(getApplication(),SignWelcom.class);
                startActivity(intent);
            }
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String name = params[1];           //문자열 변수를 다시 만들어서 params에다가  회원가입정보를 하나씩 넣어준다.
            String id = params[2];
            String pword = params[3];
            String phone = params[4];
            String Birth = params[5];
            String gender = params[6];


            String serverURL = params[0];       //param의 0번째는 주소를 넣어준다.
            String postParameters = "name=" + name + "&id=" + id + "&pword=" + pword + "&phone=" + phone + "&birth=" + Birth + "&gender=" + gender;
            //Log.d(TAG, ">>"+postParameters);

            try {

                URL url = new URL(serverURL);   // 만든 serverURL로 URL 객체를 생성
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();  //url을 토대로 http와 연결
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");  //
                httpURLConnection.connect();     //연결
                Log.d(TAG, "여기는 된다 오바! http 커넥션구역이다.");
                OutputStream outputStream = httpURLConnection.getOutputStream();  //데이터를 내보낼거기대문에 아웃풋스트림.
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();  //데이터를 밀어준다
                outputStream.close();  //다 보냈으면 끊어준다.


                int responseStatusCode = httpURLConnection.getResponseCode();


                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();

                }
                else{
                    inputStream = httpURLConnection.getErrorStream();

                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                Log.d(TAG,  sb+"가 생성되엇따!");
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                    Log.d(TAG, sb+"에 라인이 추가되었따 오바!");
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return ("Error: " + e.getMessage());
            }

        }
    }

    public void findId() {     //아이디찾는 함수입니다.
        m_edit_id = findViewById(R.id.m_edit_id);
        m_edit_pword = findViewById(R.id.m_edit_pwd);
        m_edit_pwordCheck = findViewById(R.id.m_edit_pwdcheck);
        m_edit_year = findViewById(R.id.m_edit_year);
        m_edit_day = findViewById(R.id.m_edit_day);
        m_edit_name = findViewById(R.id.m_edit_name);
        m_edit_phone = findViewById(R.id.m_edit_phone);
        m_radio_male = findViewById(R.id.m_rb_male);
        m_radio_female = findViewById(R.id.m_rb_female);
        joinbtn = findViewById(R.id.btn_sign);
        btnDupp = findViewById(R.id.btn_duplication);
        spinner = findViewById(R.id.m_spinner_month);

    }

}
