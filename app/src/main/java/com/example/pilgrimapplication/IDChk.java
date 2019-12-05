package com.example.pilgrimapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class IDChk extends AsyncTask<Void, Integer, Void> {

    String data = "";
    private String idChk;
    private String IpAddress;
    private Context context;

    public IDChk(String ID, String IP, Context context) {
        this.idChk = ID;
        this.IpAddress = IP;
        this.context = context;
    }


    @Override
    protected Void doInBackground(Void... voids) {

        String param = "u_id=" + idChk + "";
        Log.e("POST", param);
        try {
            /* 서버연결 */
            URL url = new URL(IpAddress);
            Log.e("POST", IpAddress);
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
            while ((line = in.readLine()) != null) {
                buff.append(line + "\n");
            }
            data = buff.toString().trim();

            /* 서버에서 응답 */


            if (data != null) {
                Log.e("RESULT", "성공적으로 처리되었습니다!");
                //  return null;
            } else {
                Log.e("RESULT", "에러 발생! ERRCODE = " + data);

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
        if (data.equals(idChk)) {
            Toast.makeText(context, "아이디가 중복되었습니다.", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "아이디가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
