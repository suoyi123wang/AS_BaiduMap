package com.ucas.baidumap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    MapView mMapView;
    BaiduMap mBaiduMap;
    LocationClient mLocClient;
    boolean isFirst = true;

    TextView tv_Lat;  //纬度
    TextView tv_Lon;  //经度
    TextView tv_Add;  //地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mMapView = findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        tv_Lat = findViewById(R.id.tv_Lat);
        tv_Lon = findViewById(R.id.tv_Lon);
        tv_Add = findViewById(R.id.tv_Add);
        mBaiduMap.setMyLocationEnabled(true);

        // 定位初始化
        mLocClient = new LocationClient(getApplicationContext());
        MyLocationListener myListener = new MyLocationListener();
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(5000); // 设置间隔
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            // MapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())// 设置定位数据的精度信息，单位：米
                    .direction(location.getDirection()) // 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();

            // 设置定位数据, 只有先允许定位图层后设置数据才会生效
            if(isFirst){
                isFirst = false;
                mBaiduMap.setMyLocationData(locData);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(latLng).zoom(20.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
            tv_Lat.setText(location.getLatitude()+"");
            tv_Lon.setText(location.getLongitude()+"");
            tv_Add.setText(location.getAddrStr());

        }

    }
}