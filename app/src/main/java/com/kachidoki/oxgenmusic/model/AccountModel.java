package com.kachidoki.oxgenmusic.model;

import android.content.Context;
import android.widget.Toast;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by mayiwei on 16/12/10.
 */
public class AccountModel {

    private static final AccountModel instance = new AccountModel();
    public static AccountModel getAccountModel(){
        return instance;
    }


    public interface SucessCallback{
        void login();
    }

    private SucessCallback callback;

    public void setCallBack(SucessCallback callBack){
        this.callback = callBack;
    }

    public boolean isLogin(){
        BmobUser bmobUser = BmobUser.getCurrentUser();
        if (bmobUser!=null){
            return true;
        }else {
            return false;
        }
    }

    public BmobUser getAccount(){
        BmobUser bmobUser = BmobUser.getCurrentUser();
        if(bmobUser != null){
            return bmobUser;
        }else{
            return null;
        }
    }


    public void register(final Context context,String number,String password){
        BmobUser user = new BmobUser();
        user.setMobilePhoneNumber(number);
        user.setUsername(number);
        user.setPassword(password);
        user.signUp(new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                if (e==null){
                    Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT);
                }else {
                    Toast.makeText(context, "注册成功:" , Toast.LENGTH_SHORT);
                }
            }
        });
    }

    public void login(final Context context,String number,String password){
        BmobUser bu2 = new BmobUser();
        bu2.setPassword(password);
        bu2.setUsername(number);
        bu2.login(new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                if (e==null){
                    Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT);
                }else {
                    Toast.makeText(context, "登录成功:" , Toast.LENGTH_SHORT);
                }
            }


        });
    }

    public void logout(){
        BmobUser.logOut();   //清除缓存用户对象
        BmobUser currentUser = BmobUser.getCurrentUser(); // 现在的currentUser是null了
    }

}
