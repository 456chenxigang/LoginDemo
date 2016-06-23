package com.example.login.logindemo;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.READ_CONTACTS;

class Register extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.register_phone)
    AutoCompleteTextView etPhone;
    @BindView(R.id.register_code)
    EditText etCode;
    @BindView(R.id.register_password)
    EditText etPassword;
    @BindView(R.id.register)
    AppCompatButton btnRegister;
    @OnClick(R.id.register)
    void register(AppCompatButton appCompatButton){
        atteptLogin();
    }

    private UserRegisterTask mRegisterTask = null;
    private ProgressDialog progressDialog;
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    //    getSupportActionBar().setTitle("注册");

        populateAutoComplete();


        progressDialog = new ProgressDialog(Register.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("注册中...");
    }
    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;

        }
        getLoaderManager().initLoader(0, null, this);
    }
    private void atteptLogin() {

        if(mRegisterTask != null)
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
            mRegisterTask = new UserRegisterTask(phone,password,code);
            mRegisterTask.execute((Void) null);
        }
    }
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(etPhone, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Phone
                .CONTENT_ITEM_TYPE},
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(Register.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        etPhone.setAdapter(adapter);
    }


    private interface ProfileQuery {

        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.IS_PRIMARY,
        };
        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    public class UserRegisterTask extends AsyncTask<Void,Void,Boolean>{

        private String phone,password,code;

        UserRegisterTask(String phone,String password,String code)
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
            mRegisterTask = null;
            if(successful)
            {
                final Snackbar snackbar = Snackbar.make(btnRegister, "注册成功！", Snackbar.LENGTH_LONG).setAction("返回登录？", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                snackbar.show();

            }
            else
            {
                final Snackbar snackbar = Snackbar.make(btnRegister, "注册失败！", Snackbar.LENGTH_LONG).setAction("", new View.OnClickListener() {
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

            mRegisterTask = null;
            progressDialog.dismiss();
        }
    }
}
