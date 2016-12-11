package com.kachidoki.oxgenmusic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;
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
public class RegisterActivity extends BaseActivity {


    @BindView(R.id.register_num)
    EditText registerNum;
    @BindView(R.id.register_pass)
    EditText registerPass;
    @BindView(R.id.register_pass_re)
    EditText registerPassRe;
    @BindView(R.id.register)
    Button register;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        setToolbar(true);

        AccountModel.getAccountModel().setRegSucessCall(new AccountModel.RegSucessCall() {
            @Override
            public void register() {
                Intent intent = new Intent();
                intent.putExtra("name",registerNum.getText().toString());
                setResult(Constants.RegisterSuccess,intent);
                finish();
            }
        });
    }


    public boolean checkInput() {
        if (registerNum.getText().toString().length() != 11) {
            Toast.makeText(RegisterActivity.this, "请输入正确手机号",Toast.LENGTH_SHORT).show();
            return false;
        }else if (registerPass.getText().toString().length() < 6 || registerPass.getText().toString().length() > 12) {
            Toast.makeText(RegisterActivity.this, "请输入6-12位密码",Toast.LENGTH_SHORT).show();
            return false;
        }else if (!registerPassRe.getText().toString().equals(registerPassRe.getText().toString())){
            Toast.makeText(RegisterActivity.this, "两次输入密码不一致",Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }

    @OnClick(R.id.register)
    void register(){
        if (checkInput()){
            AccountModel.getAccountModel().register(RegisterActivity.this,registerNum.getText().toString(),registerPass.getText().toString());
        }
    }
}
