package com.example.login.logindemo;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgetPassword extends AppCompatActivity {
    @BindView(R.id.forgetpassword_code)
    EditText etCode;
    @BindView(R.id.forgetpassword_password)
    EditText etPassword;
    @BindView(R.id.phone)
    AutoCompleteTextView etPhone;
    @BindView(R.id.edit)
    AppCompatButton btnEdit;


    private UserSetPasswordTask mSetPasswordTaskTask = null;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forget_password);
        getSupportActionBar().setTitle("重置密码");
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(ForgetPassword.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("修改中...");

    }

    @OnClick(R.id.edit)
    public void onClick() {
        if(mSetPasswordTaskTask != null)
        {
            return;
        }

        etPhone.setError(null);
        etPassword.setError(null);
        etCode.setError(null);

        String phone = etPhone.getText().toString();
        String password = etPassword.getText().toString();
        String code = etCode.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(phone))
        {
            etPhone.setError("手机号不能为空");
            focusView = etPhone;
            cancel =true;
        }
        else if(phone.length() != 11)
        {
            etPhone.setError("请填写正确的手机号");
            focusView = etPhone;
            cancel =true;
        }
        else if(TextUtils.isEmpty(password))
        {
            etPassword.setError("密码不能为空");
            focusView = etPassword;
            cancel =true;
        }
        else if(TextUtils.isEmpty(code))
        {
            etCode.setError("验证码不能为空");
            focusView = etCode;
            cancel =true;
        }
        if(cancel)
        {
            focusView.requestFocus();
        }
        else
        {
            progressDialog.show();
            mSetPasswordTaskTask = new UserSetPasswordTask(phone,password,code);
            mSetPasswordTaskTask.execute((Void) null);
        }
    }

    public class UserSetPasswordTask extends AsyncTask<Void,Void,Boolean>
    {

        private String phone,password,code;
        UserSetPasswordTask(String phone,String password,String code)
        {
            this.phone = phone;
            this.password = password;
            this.code = code;
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean successful) {
            super.onPostExecute(successful);
            progressDialog.dismiss();
            mSetPasswordTaskTask = null;
            if(successful)
            {
                final Snackbar snackbar = Snackbar.make(btnEdit, "修改成功！", Snackbar.LENGTH_LONG).setAction("返回登录？", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                snackbar.show();

            }
            else
            {
                final Snackbar snackbar = Snackbar.make(btnEdit, "修改失败！", Snackbar.LENGTH_LONG).setAction("", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //finish();
                    }
                });
                snackbar.show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            mSetPasswordTaskTask = null;
            progressDialog.dismiss();
        }
    }
}
