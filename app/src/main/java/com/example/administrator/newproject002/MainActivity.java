package com.example.administrator.newproject002;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;

public class MainActivity extends CommonMethodActivity {
    //上个活动传递过来的数据
    String accessTokenData_main=" ";
    //按钮
    Button start_btn_main;
    Button stop_btn_main;
    Button shouye_btn_main;
    Button shangye_btn_main;
    Button xiaye_btn_main;
    Button weiye_btn_main;
    // 列表
    ListView id_list_main;
    ListView temp_list_main;
    ListView time_list_main;
    //控制码
    int isGetDataFromclound = 1;
    //现在温度
    Double temp_now_main = 25.3;
    //定义表格数组
    String[] time_data_main = {" "," "," "," "," "," "," "," "," "," "};
    String[] temp_data_main = {" "," "," "," "," "," "," "," "," "," "};
    String[] id_data_main = {" "," "," "," "," "," "," "," "," "," "};
    //表格头部
    int isFristAddheader = 1;
    //查询数据的条数和偏移量
    int dataNumber_main = 10;
    int dataOffset_main = 0;
    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*****************************************初始化视图***************************************************************************************/
        shouye_btn_main  = findViewById(R.id.shouye_btn_main);//翻页
        shangye_btn_main = findViewById(R.id.shangye_btn_main);
        xiaye_btn_main = findViewById(R.id.xiaye_btn_main);
        weiye_btn_main = findViewById(R.id.weiye_btn_main);
        start_btn_main = findViewById(R.id.start_btn_main);//开始、停止获取数据
        stop_btn_main = findViewById(R.id.stop_btn_main);
        id_list_main = findViewById(R.id.id_list_main);//数据列表
        temp_list_main = findViewById(R.id.temp_list_main);
        time_list_main = findViewById(R.id.time_list_main);
        /*****************************************获取上个Activity传递过来的Accesstoken值***************************************************************************************/
        Intent intent = getIntent();
        accessTokenData_main = intent.getStringExtra("Accesstoken");
        Log.d("accessTokenData_main",accessTokenData_main);

        /*****************************************界面跳转***************************************************************************************/
//

        /*****************************************控制、数据更新***************************************************************************************/
        setButton();// 设置按钮事件
        getDataFromDb(dataNumber_main,dataOffset_main);//显示数据
        getTempDataFromClund();//获取手并存储到数据库
        shangye_btn_main.setEnabled(false);//上一页初始为不可点击

    }

/////////////////////////////////////////////////onCreate结束///////////////////////////////////////////////////////////////////


    /*******************************************数据库操作****************************************************************/
    //存储数据
    void saveDataToDb(){
        LitePal.getDatabase();
        Datas datas = new Datas();
        datas.setTimeData(getNewTime());
        datas.setTempData(temp_now_main);
        datas.save();
        Log.d("Main","数据存储");
    }
    //读取、显示数据
    void getDataFromDb(int dataNumber,int dataOffset){
        List<Datas> datasList = LitePal
                .select("id","TempData","TimeData")
                .order("id desc")
                .limit(dataNumber)
                .offset(dataOffset)
                .find(Datas.class);
        int i= 0;
        for(Datas datas:datasList){
            id_data_main[i] = (i+1+dataOffset)+"";
            temp_data_main[i] = ""+datas.getTempData();
            time_data_main[i] = datas.getTimeData();
            Log.d("Main_id",id_data_main[i]);
            Log.d("Main_temp",temp_data_main[i]);
            Log.d("Main_time",time_data_main[i]);
            i++;
        }
        writeDataToListView(id_list_main ,id_data_main ,R.layout.header_id);
        writeDataToListView(temp_list_main ,temp_data_main ,R.layout.header_temp);
        writeDataToListView(time_list_main ,time_data_main ,R.layout.header_time);

    }
    /*****************************************控件开关***************************************************************************************/
    //开启关闭数据获取
    void setButton(){
        start_btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGetDataFromclound = 1;
                getTempDataFromClund();
            }
        });
        stop_btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGetDataFromclound = 0;
            }
        });
        //翻页按钮设置
        shouye_btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataOffset_main = 0;
                getDataFromDb(dataNumber_main,dataOffset_main);// 从数据库获取数据
                shangye_btn_main.setEnabled(false);//上一页不可点击
                xiaye_btn_main.setEnabled(true);
            }
        });
        shangye_btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataOffset_main-=10;
                getDataFromDb(dataNumber_main,dataOffset_main);// 从数据库获取数据
                xiaye_btn_main.setEnabled(true);//下一页可点击
                if(dataOffset_main<=0){
                    shangye_btn_main.setEnabled(false);
                }
            }
        });
        xiaye_btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataOffset_main+=10;
                getDataFromDb(dataNumber_main,dataOffset_main);// 从数据库获取数据
                shangye_btn_main.setEnabled(true);//上一页可点击
                if(dataOffset_main>=20){
                    xiaye_btn_main.setEnabled(false);
                }
            }
        });
        weiye_btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataOffset_main = 20;
                getDataFromDb(dataNumber_main,dataOffset_main);// 从数据库获取数据
                xiaye_btn_main.setEnabled(false);//下一页不可点击
                shangye_btn_main.setEnabled(true);
            }
        });
    }

    /*****************************************其他***************************************************************************************/
    //获取温度数据
    void getTempDataFromClund(){
        if(isGetDataFromclound == 1){
        getDataWithOkHttpGet("devices/"+deviceId+"/Sensors/"+apiTag_temp,accessTokenData_main);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Response response = call.execute();
                        if (response.body() != null) {
                            resultData = response.body().string();
                            Log.d("nl_temperature",resultData);
                            /*******************************************解析温度Json数据**********************************************/
                            Gson gson = new Gson();
                            java.lang.reflect.Type type = new TypeToken<JsonData_DevicesSensors_chuang>() {}.getType();
                            JsonData_DevicesSensors_chuang jsonData_devicesSensors = gson.fromJson(resultData,type);
                            if(jsonData_devicesSensors.getResultObj().getValue() != null) {
                                temp_now_main = (Double) jsonData_devicesSensors.getResultObj().getValue();
                            }
                        }
                        response.close();
                        Thread.sleep(5000);
                        Message message = new Message();
                        message.what = 0x01;
                        handler.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    //将数据填入表格
    void writeDataToListView(ListView listView, String[] dataStr, int headerviewid){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                MainActivity.this, R.layout.array_adapter,dataStr);
        listView.setAdapter(adapter);
        View headerView = View.inflate(this,headerviewid,null);
        if(isFristAddheader<=3){
        listView.addHeaderView(headerView);
        isFristAddheader++;
        }
    }
    /*****************************************handler类，线程更新***************************************************************************************/
    @SuppressLint("HandlerLeak") final Handler handler = new Handler(){
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x01:
                    saveDataToDb();//存储数据到数据库
                    getDataFromDb(dataNumber_main,dataOffset_main);// 从数据库获取数据
                    getTempDataFromClund();// 循环
                    break;
                case 0x02:

                    break;
            }
        }
    };

}