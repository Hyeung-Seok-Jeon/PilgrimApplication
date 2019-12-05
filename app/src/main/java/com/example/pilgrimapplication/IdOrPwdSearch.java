package com.example.pilgrimapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class IdOrPwdSearch extends AppCompatActivity {
    public EditText IdShPhone,PwdShId,PwdShPhone,edit_code_enter,edit_code_enter2;
    public Button btn_idCode,btn_pwdCode,btn_check1,btn_check2;
    String sPhone,sId, saveData,S_certificationCode;
    String param;
    String IpAddress;
    int distinction=0;
    TextView showCode1,showCode2;
    int certificationCode;
    static final int SMS_RECEIVE_PERMISSON=1;
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) !=
                        PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.id_or_pwd_search);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//정방향 세로로 완전히 고정,회전불가
        String[] PERMISSIONS ={
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
        };
        if(!hasPermissions(this, PERMISSIONS)){
            //권한요청
            ActivityCompat.requestPermissions(this, PERMISSIONS,SMS_RECEIVE_PERMISSON);
        }





        IdShPhone=(EditText)findViewById(R.id.edit_id_SearchPhone);
        PwdShId=(EditText)findViewById(R.id.edit_id_searchpwd);
        PwdShPhone=(EditText)findViewById(R.id.edit_phone_searchpwd);
        btn_idCode=findViewById(R.id.btn_idcode);
        btn_pwdCode=findViewById(R.id.btn_pwdcode);
        showCode1=findViewById(R.id.txt_code1);
        edit_code_enter=findViewById(R.id.edit_codeEnter);
        edit_code_enter2=findViewById(R.id.edit_codeEnter2);
        btn_check1=findViewById(R.id.btn_check1);
        showCode2=findViewById(R.id.txt_code2);
        btn_check2=findViewById(R.id.btn_check2);

        btn_idCode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                try
                {
                    sPhone=IdShPhone.getText().toString();
                    param = "u_phone=" + sPhone +"";
                    IpAddress="http://www.next-table.com/pilgrimproject/find_id.php";
                }catch(NullPointerException e){

                }

                distinction=1;
                IdorPwdSearch idorPwdSearch=new IdorPwdSearch();
                idorPwdSearch.execute();

            }
        });
        btn_pwdCode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    sId=PwdShId.getText().toString();
                    sPhone=PwdShPhone.getText().toString();
                    param = "u_id=" + sId +"&u_phone="+sPhone+"";
                    IpAddress="http://www.next-table.com/pilgrimproject/find_pwd.php";
                }catch(NullPointerException e){

                }
                distinction=0;
                IdorPwdSearch idorPwdSearch=new IdorPwdSearch();
                idorPwdSearch.execute();

            }
        });
        btn_check1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(edit_code_enter.getText().toString().equals(S_certificationCode))
                    showCode1.setText("    아이디는 "+ saveData +"입니다.");
            }
        });
        btn_check2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(edit_code_enter2.getText().toString().equals(S_certificationCode))
                    showCode2.setText("    비밀번호는 "+ saveData +"입니다.");
            }
        });
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        Intent intent = new Intent(this, Intro.class);
        startActivity(intent);
    }

    class IdorPwdSearch extends AsyncTask<Void,Integer,Void> {

        String data="";


        @Override
        protected Void doInBackground(Void... voids) {


            Log.e("POST",param);
            try {
                /* 서버연결 */
                URL url = new URL(IpAddress);
                Log.e("POST",IpAddress);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;


                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ( ( line = in.readLine() ) != null )
                {
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();

                /* 서버에서 응답 */


                if(data!=null)
                {
                    Log.e("RESULT","성공적으로 처리되었습니다!");
                    //  return null;
                }
                else
                {
                    Log.e("RESULT","에러 발생! ERRCODE = " + data);

                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(data.equals("false")){
                if(distinction==1)
                    Toast.makeText(getApplication(), "없는 번호입니다", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplication(), "아이디 또는 번호가 틀립니다.", Toast.LENGTH_SHORT).show();


            }else{
                certificationCode=(int)(Math.random()*999999)+100000;
                S_certificationCode=String.valueOf(certificationCode);
                saveData=data;
                String P_certificationCode=String.valueOf(certificationCode);
                SmsManager mSmsManager=SmsManager.getDefault();
                if(distinction==1)
                    mSmsManager.sendTextMessage(sPhone,"","인증번호는 "+certificationCode+"입니다.",null,null);
                else
                    mSmsManager.sendTextMessage(sPhone,"","인증번호는 "+certificationCode+"입니다.",null,null);
            }
        }


    }

}
