package com.example.yangtianrui.notebook.activity;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yangtianrui.notebook.R;
import com.example.yangtianrui.notebook.fragment.AllNotesFragment;
import com.example.yangtianrui.notebook.fragment.SearchNoteFragment;
import com.example.yangtianrui.notebook.fragment.SettingFragment;

import cn.bmob.v3.BmobUser;

public class MainActivity extends AppCompatActivity
        implements Toolbar.OnMenuItemClickListener, AdapterView.OnItemClickListener, NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private ActionBarDrawerToggle mToggle;
    private FrameLayout mFlContent;
    private NavigationView mNvMenu;
    private DrawerLayout mDlLayout;
    private TextView mTvUserName;
    private View mHeaderView;

    private Fragment mFragments[] = new Fragment[3];

    private String mUserName;
    private long curTimeMills;

    // 是否启动Service执行计划任务
    public static boolean IS_SYNC = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取配置信息
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        IS_SYNC = pref.getBoolean("auto_sync", false);
        mUserName = getIntent().getStringExtra(SplashActivity.SEND_USER_NAME);
        mFragments[0] = new AllNotesFragment();
        mFragments[1] = new SearchNoteFragment();
        mFragments[2] = new SettingFragment();
        initView();
        showFragment(mFragments[0]);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.id_toolbar);
        mToolbar.setTitle("Yi Note");
        setSupportActionBar(mToolbar);
        mToolbar.setOnMenuItemClickListener(this);
        mDlLayout = (DrawerLayout) findViewById(R.id.id_dl_main_layout);
        mFlContent = (FrameLayout) findViewById(R.id.id_fl_main_content);
        mNvMenu = (NavigationView) findViewById(R.id.id_nav_menu);
        // 获取HeaderView
        mHeaderView = mNvMenu.getHeaderView(0);
        mTvUserName = (TextView) mHeaderView.findViewById(R.id.id_tv_username);
        mNvMenu.setNavigationItemSelectedListener(this);
        mToggle = new ActionBarDrawerToggle(this, mDlLayout, mToolbar, R.string.app_name, R.string.app_name);
        mToggle.syncState();
        mDlLayout.setDrawerListener(mToggle);
        mTvUserName.setText(mUserName);
    }


    /**
     * 显示指定的Fragment
     */
    private void showFragment(Fragment fragment) {
        getFragmentManager().beginTransaction().replace(R.id.id_fl_main_content, fragment).commit();
    }


    /**
     * 添加菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * 设置添加事件
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_menu_add_note:
                Intent intent = new Intent(this, NoteDetailActivity.class);
                startActivity(intent);
                break;
        }
        return false;
    }

    /**
     * 切换到相应的Fragment
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        getFragmentManager().beginTransaction()
                .replace(R.id.id_fl_main_content, mFragments[position]).commit();
        mDlLayout.closeDrawers();
    }


    /**
     * 点击侧滑触发相应的事件
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_all_notes:
                showFragment(mFragments[0]);
                break;
            case R.id.nav_search:
                showFragment(mFragments[1]);
                break;
            case R.id.nav_setting:
                showFragment(mFragments[2]);
                break;
            case R.id.nav_logout:
                logout();
                break;
            default:
                Toast.makeText(this, "功能开发中", Toast.LENGTH_SHORT).show();
        }
        // 关闭菜单
        mDlLayout.closeDrawer(GravityCompat.START);
        // 表示已经处理完毕点击事件
        return true;
    }


    /**
     * 退出当前用户
     */
    private void logout() {
        BmobUser.logOut(this);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 单击后退键时,提示用户是否退出
     */
    @Override
    public void onBackPressed() {
        //curTimeMills = System.currentTimeMillis();
        if (mDlLayout.isDrawerOpen(GravityCompat.START)) {
            mDlLayout.closeDrawer(GravityCompat.START);
        } else {
            // 关闭程序
            exitAPP();
        }
    }

    /**
     * 两秒内单击两下即可关闭APP
     */
    private void exitAPP() {

        if (System.currentTimeMillis() - curTimeMills > 2000) {
            Snackbar.make(mDlLayout, "再单击一下即可退出", Snackbar.LENGTH_SHORT).show();
            curTimeMills = System.currentTimeMillis();
        } else {
            finish();
        }

    }
}
