package com.example.yangtianrui.notebook;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.yangtianrui.notebook.config.Constants;

import cn.bmob.v3.Bmob;

/**
 * Created by yangtianrui on 16-6-12.
 * <p/>
 * 用于初始化全局变量
 */
public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化Bmob
        Bmob.initialize(this, Constants.BMOB_API_KEY);
    }
}
