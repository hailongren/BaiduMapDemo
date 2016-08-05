package com.bearapp.baidumapdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.bearapp.baidumapdemo.adapter.PlaceListAdapter;
import com.bearapp.baidumapdemo.location.LocationService;
import com.bearapp.baidumapdemo.util.Utils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ImageView ivGetLoc;
    private ListView listView;
    private MapView mapView;
    private ProgressBar progressBar;
    private BaiduMap baiduMap;

    private BDLocation mLocation;

    private GeoCoder mGeoCoder;


    private LatLng mCurrentPoint;


    private ArrayList<PoiInfo> poiInfoArrayList = new ArrayList<>();

    private PlaceListAdapter placeListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }


    private void initViews() {
        mapView = (MapView) findViewById(R.id.mapView);
        listView = (ListView) findViewById(R.id.listView);
        ivGetLoc = (ImageView) findViewById(R.id.ivGetLoc);
        ivGetLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationService.getLocation(getApplicationContext());
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        placeListAdapter = new PlaceListAdapter(this, poiInfoArrayList);

        listView.setAdapter(placeListAdapter);


        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));
        baiduMap.setOnMapStatusChangeListener(onMapStatusChangeListener);

        mGeoCoder = GeoCoder.newInstance();
        mGeoCoder.setOnGetGeoCodeResultListener(geoCoderResultListener);

    }

    private BaiduMap.OnMapStatusChangeListener onMapStatusChangeListener = new BaiduMap.OnMapStatusChangeListener() {
        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChange(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {
            Log.d("Henry", "onMapStatusChangeFinish");
            mCurrentPoint = baiduMap.getMapStatus().target;
            mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(mCurrentPoint));
            poiInfoArrayList.clear();
            placeListAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.VISIBLE);
        }
    };


    BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Utils.ACTION_LOCATION)) {
                mLocation = intent.getParcelableExtra(Utils.KEY_LOCATION);
                String loca = Utils.getLocationStr(mLocation);
                Toast.makeText(getApplicationContext(), loca, Toast.LENGTH_SHORT).show();
                // 构造定位数据
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(mLocation.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(0).latitude(mLocation.getLatitude())
                        .longitude(mLocation.getLongitude()).build();
                // 设置定位数据
                baiduMap.setMyLocationData(locData);
                LatLng ll = new LatLng(mLocation.getLatitude(),
                        mLocation.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 15);
                baiduMap.animateMapStatus(u);

            }
        }
    };


    private OnGetGeoCoderResultListener geoCoderResultListener = new OnGetGeoCoderResultListener() {
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            progressBar.setVisibility(View.GONE);
            if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }

            PoiInfo poiInfo = new PoiInfo();
            poiInfo.name = "[位置]";
            poiInfo.address = reverseGeoCodeResult.getAddress();

            poiInfoArrayList.clear();
            poiInfoArrayList.add(poiInfo);

            if (reverseGeoCodeResult.getPoiList() != null) {
                poiInfoArrayList.addAll(reverseGeoCodeResult.getPoiList());
            }
            placeListAdapter.notifyDataSetChanged();
        }
    };

    private void registerReceiver() {
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(locationReceiver, new IntentFilter(Utils.ACTION_LOCATION));
    }

    private void unregisterReceiver() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(locationReceiver);
    }


    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver();
        LocationService.getLocation(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
