package com.bearapp.baidumapdemo.location;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bearapp.baidumapdemo.util.Utils;

public class LocationService extends Service implements BDLocationListener {

    public LocationClient locationClient = null;

    public static void getLocation(Context context) {
        Intent intent = new Intent(context, LocationService.class);
        context.startService(intent);
    }

    public LocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Henry", "LocationService onCreate()");
        locationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        locationClient.registerLocationListener(this);
        initLocation();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationClient.start();
        return START_NOT_STICKY;
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(true);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        locationClient.setLocOption(option);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Henry", "LocationService onDestroy()");
    }

    @Override
    public void onReceiveLocation(BDLocation loc) {
        if (null != loc) {
            locationClient.stop();
            Intent intent = new Intent(Utils.ACTION_LOCATION);
            intent.putExtra(Utils.KEY_LOCATION, loc);
            LocalBroadcastManager.getInstance(this.getApplicationContext()).sendBroadcast(intent);
            LocationService.this.stopSelf();
        }
    }
}
