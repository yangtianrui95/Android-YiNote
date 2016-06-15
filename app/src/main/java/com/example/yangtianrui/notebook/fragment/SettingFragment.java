package com.example.yangtianrui.notebook.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.yangtianrui.notebook.R;

/**
 * Created by yangtianrui on 16-5-23.
 * <p/>
 * 程序设置界面,提供退出功能
 */
public class SettingFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 显示资源文件
        addPreferencesFromResource(R.xml.preferences);
    }

}
