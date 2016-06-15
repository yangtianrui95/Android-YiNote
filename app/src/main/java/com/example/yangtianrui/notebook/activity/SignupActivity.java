package com.example.yangtianrui.notebook.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yangtianrui.notebook.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

/**
 * A login screen that offers login via email/password.
 */
public class SignupActivity extends AppCompatActivity {


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEtUsername;
    private EditText mEtPwd;
    private EditText mEtVerify;
    private View mProgressView;
    private View mLoginFormView;
    private Button mBtnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initView();
        initEvent();
    }


    /**
     * 初始化组件
     */
    private void initView() {
        mEtPwd = (EditText) findViewById(R.id.id_et_signup_password);
        mEtUsername = (EditText) findViewById(R.id.id_et_signup_username);
        mBtnSignup = (Button) findViewById(R.id.id_btn_signup);
        mProgressView = findViewById(R.id.id_pb_signup_loading);
        mLoginFormView = findViewById(R.id.id_lv_signup_form);
        mEtVerify = (EditText) findViewById(R.id.id_et_signup_verify);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        mEtPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mBtnSignup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    /**
     * 接收登陆的结果,并判断错误
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEtUsername.setError(null);
        mEtPwd.setError(null);
        mEtVerify.setError(null);

        // Store values at the time of the login attempt.
        String userName = mEtUsername.getText().toString();
        String password = mEtPwd.getText().toString();
        String verify = mEtVerify.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // 检查密码
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mEtPwd.setError("密码必须大于5位");
            focusView = mEtPwd;
            cancel = true;
        }

        // 检查两次密码是否一致
        if (TextUtils.isEmpty(verify) || !verifyPwd(password, verify)) {
            mEtVerify.setError("两次输入密码不一致");
            focusView = mEtVerify;
            cancel = true;
        }

        // 检查用户名
        if (TextUtils.isEmpty(userName)) {
            mEtUsername.setError("用户名是必填项");
            focusView = mEtUsername;
            cancel = true;
        } else if (!isUserNameValid(userName)) {
            mEtUsername.setError("用户名必须大于4位");
            focusView = mEtUsername;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
//            mAuthTask = new UserLoginTask(userName, password);
//            mAuthTask.execute((Void) null);
            signupInBmob(userName, password);
        }
    }

    /**
     * 在Bmob 注册数据
     */
    private void signupInBmob(final String userName, String password) {
        // 向Bmob后端注册数据
        BmobUser user = new BmobUser();
        user.setUsername(userName);
        user.setPassword(password);
        user.signUp(SignupActivity.this, new SaveListener() {
            @Override
            public void onSuccess() {
                Snackbar.make(mLoginFormView, "登陆成功", Snackbar.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                intent.putExtra(SplashActivity.SEND_USER_NAME, userName);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                showProgress(false);
                Snackbar.make(mLoginFormView, s, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 用户名必须大于4位
     */
    private boolean isUserNameValid(String userName) {
        return userName.length() >= 4;
    }

    /**
     * 密码必须大于5位
     */
    private boolean isPasswordValid(String password) {
        return password.length() >= 5;
    }

    /**
     * 验证两次输入的密码是否一致
     */
    private boolean verifyPwd(String pwd, String verify) {
        return pwd.equals(verify);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * 执行登陆操作
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mUserName;
        private final String mPassword;

        UserLoginTask(String userName, String password) {
            mUserName = userName;
            mPassword = password;
        }

        @Override
        protected String doInBackground(Void... params) {
            final boolean[] loginOK = {false};
            // 向Bmob后端注册数据
            BmobUser user = new BmobUser();
            user.setUsername(mUserName);
            user.setPassword(mPassword);
            final StringBuilder result = new StringBuilder();
            user.signUp(SignupActivity.this, new SaveListener() {
                @Override
                public void onSuccess() {
                    // loginOK[0] = true;
                    result.append("登陆成功");
                }

                @Override
                public void onFailure(int i, String s) {
                    result.append(s);
                }
            });


            // TODO: register the new account here.
            Toast.makeText(SignupActivity.this, loginOK[0] + "", Toast.LENGTH_SHORT).show();
            return result.toString();
        }

        @Override
        protected void onPostExecute(final String success) {
            mAuthTask = null;
            showProgress(false);

            if (success.equals("登陆成功")) {
                Snackbar.make(mLoginFormView, success, Snackbar.LENGTH_SHORT).show();
                finish();
            } else {
//                mEtPwd.setError("输入密码错误");
//                mEtPwd.requestFocus();
                Snackbar.make(mLoginFormView,
                        success, Snackbar.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

