package com.example.administrator.newproject002;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends CommonMethodActivity {
    EditText account_et_lg,password_et_lg;
    Response response;
    static String qustionData = "";
    /* -----------------------------------------------定义接受返回数据的对象   ----------------------------------------------   */
    static int statusCode=1;
    static int status = 1;
    static String usename_data_gson,psw_data_gson;
    static String msg="";
    public static String AccessToken_data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        account_et_lg = findViewById(R.id.account_et_lg);
        password_et_lg = findViewById(R.id.password_et_lg);

        Button login_btn_lg = findViewById(R.id.login_btn_lg);
        login_btn_lg.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                sendAskToLogin(account_et_lg.getText().toString(),password_et_lg.getText().toString());
            }
        });

    }
//////////////////////////////////////////////////////onCreate结束///////////////////////////////////////////////////////////////////

    /*******************************************Handler更新线程*****************************************************/
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Login_lg();
        }
    };
    private void Login_lg(){
        if(status==0){
            changeUI(LoginActivity.this ,MainActivity.class,AccessToken_data);
            Log.d("LoginActivity","登录成功");
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
        }else{
            Log.d("LoginActivity","登录失败");
            Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
        }
    }
    /************************************************向服务器发送登录请求**************************************************************/
    public String sendAskToLogin(final String Account, final String password){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();   //定义一个OKHttpClient实例
                    RequestBody requestBody = new FormBody.Builder()
                            .add("Account",Account)
                            .add("Password",password)
                            .add("IsRememberMe","true")
                            .build();
                    Log.d("Account",Account);
                    Log.d("Password",password);
                    //实例化一个Respon对象，用于发送HTTP请求
                    Request request = new Request.Builder()
                            .url("http://api.nlecloud.com/Users/Login")             //设置目标网址
                            .post(requestBody)
                            .build();
                    response = okHttpClient.newCall(request).execute();  //获取服务器返回的数据
                    if (response.body() != null) {
                        qustionData = response.body().string();//存储服务器返回的数据
                        parseJSONWithGSON(qustionData);
                        Log.d("LoginActivity",qustionData);
                    }
                    Message message = new Message();
                    handler.sendEmptyMessage(0x1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return qustionData;
    }
    private void parseJSONWithGSON(String json) {
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<JsonData_Login>() {}.getType();
        JsonData_Login app = gson.fromJson(json, type);
        status = app.getStatus(); // 获取登录状态
        AccessToken_data = app.getResultObj().getAccessToken(); //获取返回的确定设备标识的字符串
        Log.d("AccessToken_data",AccessToken_data);
        Log.d("status", String.valueOf(status));
        msg = app.getMsg();
    }

}