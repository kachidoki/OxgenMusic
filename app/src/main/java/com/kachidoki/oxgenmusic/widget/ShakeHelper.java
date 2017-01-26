package com.kachidoki.oxgenmusic.widget;

import android.hardware.SensorEventListener;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * Created by mayiwei on 17/1/26.
 */
public class ShakeHelper implements SensorEventListener {
    //速度阈值
    private static final int SPEED_SHAKEHOLD=3000;
    //检测时间间隔
    private static final int UPTATE_INTERVAL_TIME = 220;
    //传感器管理器
    private SensorManager sensorManager;
    //传感器
    private Sensor sensor;
    //重力感应监听器
    private OnShakeListener onShakeListener;
    // 上下文
    private Context mContext;
    // 手机上一个位置时重力感应坐标
    private float lastX;
    private float lastY;
    private float lastZ;
    // 上次检测时间
    private long lastUpdateTime;

    public ShakeHelper(Context mContext) {
        super();
        this.mContext = mContext;

        start();
    }


    public void start(){
        sensorManager=(SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager!=null){
            //获得加速度感应器
            sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        if(sensor!=null){
            //注册加速器，并且设置速率
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    // 设置重力感应监听器
    public void setOnShakeListener(OnShakeListener listener) {
        onShakeListener = listener;
    }

    public void stop(){
        sensorManager.unregisterListener(this);
    }


    //重力感应到变化
    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        long currentUpdateTime=System.currentTimeMillis();

        long timeInterval=currentUpdateTime-lastUpdateTime;

        if(timeInterval<UPTATE_INTERVAL_TIME){
            return;
        }

        lastUpdateTime=currentUpdateTime;

        float x=event.values[0];
        float y=event.values[1];
        float z=event.values[2];

        float deltaX=x-lastX;
        float deltaY=y-lastY;
        float deltaZ=z-lastZ;

        lastX=x;
        lastY=y;
        lastZ=z;

        double speed=Math.sqrt(deltaX*deltaX+deltaY*deltaY*deltaZ*deltaZ)/timeInterval*10000;


        if(speed>SPEED_SHAKEHOLD){
            onShakeListener.onShake();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }


    public interface OnShakeListener {
        void onShake();
    }
}
