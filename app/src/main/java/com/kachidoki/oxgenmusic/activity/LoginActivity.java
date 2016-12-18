package com.kachidoki.oxgenmusic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.app.BaseActivity;
import com.kachidoki.oxgenmusic.config.Constants;
import com.kachidoki.oxgenmusic.model.AccountModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mayiwei on 16/12/11.
 */
public class LoginActivity extends BaseActivity {


    @BindView(R.id.login_num)
    EditText loginNum;
    @BindView(R.id.login_pass)
    EditText loginPass;
    @BindView(R.id.login_register)
    TextView loginRegister;
    @BindView(R.id.login)
    Button login;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setToolbar(true);

        AccountModel.getAccountModel().setLogSucessCall(new AccountModel.LogSucessCall() {
            @Override
            public void login() {
                Intent intent = new Intent();
                intent.putExtra("name",loginNum.getText().toString());
                setResult(Constants.LoginSuccess,intent);
                finish();
            }
        });

    }


    @OnClick({R.id.login,R.id.login_register})
    void loginClick(View view){
        switch (view.getId()){
            case R.id.login:
                if (checkInput()){
                    AccountModel.getAccountModel().login(LoginActivity.this,loginNum.getText().toString(),loginPass.getText().toString());
                }
                break;
            case R.id.login_register:
                startActivityForResult(new Intent(LoginActivity.this,RegisterActivity.class),Constants.ResquestRegister);
                break;
        }
    }

    private boolean checkInput(){
        if (loginNum.getText().toString().length()!=11){
            Toast.makeText(LoginActivity.this, "请输入正确手机号",Toast.LENGTH_SHORT).show();
            return false;
        } else if (loginPass.getText().toString().length() < 6 || loginPass.getText().toString().length() > 12) {
            Toast.makeText(LoginActivity.this, "请输入6-12位密码",Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Constants.ResquestRegister&&resultCode==Constants.RegisterSuccess){
            Intent intent = new Intent();
            intent.putExtra("name",data.getStringExtra("name"));
            setResult(Constants.LoginSuccess,intent);
            finish();
        }
    }
}
