package com.example.pilgrimapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Login extends AppCompatActivity {
    private BackPressCloseHandler backPressCloseHandler;
    EditText login_password, login_ID;
    Button btn_login, btn_IdOrPwdSearch, btn_membership;
    String sId, sPw;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//정방향 세로로 완전히 고정,회전불가

        //로그인 패스워드 암호화
        login_password = (EditText) findViewById(R.id.edit_login_pd);
        login_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        login_password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        btn_login = (Button) findViewById(R.id.btn_login);
        login_ID = (EditText) findViewById(R.id.edit_login_id);
        btn_membership = (Button) findViewById(R.id.btn_membership);
        btn_membership.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplication(), MemberShip.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(getApplication(),MainActivity.class);
                startActivity(intent);
                try
                {
                    sId = login_ID.getText().toString();
                    sPw = login_password.getText().toString();

                } catch (NullPointerException e)
                {
                    Log.e("err", e.getMessage());
                }
                if (sId.equals(""))
                {
                    Toast.makeText(Login.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (sPw.equals(""))
                {
                    Toast.makeText(Login.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (sPw.equals("") && sId.equals(""))
                {
                    Toast.makeText(Login.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loginchk lDB = new loginchk();
                    lDB.execute();
                }


            }
        });
        btn_IdOrPwdSearch = findViewById(R.id.btn_idorPwSh);
        btn_IdOrPwdSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Login.this, IdOrPwdSearch.class);
                startActivity(intent);
            }
        });
        backPressCloseHandler = new BackPressCloseHandler(this);
    }

    @Override
    public void onBackPressed()
    {
        backPressCloseHandler.onBackPressed();//뒤로가기버튼
    }

    class loginchk extends AsyncTask<Void, Integer, Void>
    {

        String data = "";

        @Override
        protected Void doInBackground(Void... voids)
        {
            String param = "u_id=" + sId + "&u_pword=" + sPw + "";

            Log.e("POST", param);
            try
            {
                /* 서버연결 */
                URL url = new URL(
                        "http://192.168.0.68/Myproject/login_chk.php");
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
                while ((line = in.readLine()) != null)
                {
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();

                /* 서버에서 응답 */


                if (data.equals("1"))
                {
                    Log.e("RESULT", "성공적으로 처리되었습니다!");

                }
                else
                {
                    Log.e("RESULT", "에러 발생! ERRCODE = " + data);

                }

            } catch (MalformedURLException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            if (data.equals("1"))
            {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(Login.this, "비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show();
            }
        }


    }



}
