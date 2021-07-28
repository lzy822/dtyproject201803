package com.esri.arcgisruntime.demos.displaymap;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.esri.arcgisruntime.demos.displaymap.MainActivity.UPDATE_TEXT;

/**
 * 路径记录的后台服务类
 * 用于以后台服务的形式记录路径
 *
 * @author  李正洋
 *
 * @since   1.3
 */
public class RecordTrail extends Service {
    private static final String TAG = "RecordTrail";

    //当前记录的坐标点个数
    private int isLocate = 0;

    //记录当前轨迹
    private String m_cTrail = "";

    Location location;

    private LocationManager locationManager;

    //是否结束绘制
    private boolean isLocateEnd = true;

    //上一个记录下来的坐标点坐标
    private float last_x, last_y;

    //坐标信息
    double m_lat,m_long;

    String m_ic = "";

    /*private RecordTrailBinder mBinder = new RecordTrailBinder();

    class RecordTrailBinder extends Binder {

    }*/
    public RecordTrail() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w(TAG, "onCreate: " );
        getLocation();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w(TAG, "onStartCommand: " );
        m_ic = intent.getStringExtra("ic");
        Log.w(TAG, m_ic );
        m_cTrail = "";
        isLocateEnd = false;
        isLocate = 0;
        initTrail();
        return super.onStartCommand(intent, flags, startId);

    }

    //开始记录轨迹
    private void initTrail(){
        if (isGPSEnabled()){
            Toast.makeText(MyApplication.getContext(), "开始绘制轨迹", Toast.LENGTH_SHORT).show();
        }else Toast.makeText(MyApplication.getContext(), "GPS没有开启, 请打开GPS功能", Toast.LENGTH_SHORT).show();
    }

    //判断GPS功能是否处于开启状态
    private boolean isGPSEnabled(){
        //textView = (TextView) findViewById(R.id.txt);
        //得到系统的位置服务，判断GPS是否激活
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean ok = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(ok){
            //textView.setText("GPS已经开启");
            //Toast.makeText(this, "GPS已经开启", Toast.LENGTH_LONG).show();
            return true;
        }else{
            Toast.makeText(MyApplication.getContext(), "GPS没有开启, 请打开GPS功能", Toast.LENGTH_SHORT).show();
            //textView.setText("GPS没有开启");
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.w(TAG, "2021/1/15 onDestroy: " + m_cTrail);
        isLocateEnd = true;
        recordTrail(last_x, last_y);
        if (m_cTrail.trim().length()>0) {
            m_cTrail=m_cTrail.trim();
            Log.w(TAG, "当前有: 坐标串" + m_cTrail);
            Trail trail = new Trail();
            List<Trail> trails = LitePal.findAll(Trail.class);
            trail.setIc(m_ic);
            trail.setName("路径" + Integer.toString(trails.size() + 1));
            trail.setPath(m_cTrail);
            float[] spatialIndex = DataUtil.getSpatialIndex(m_cTrail);
            trail.setMaxlat(spatialIndex[0]);
            trail.setMinlat(spatialIndex[1]);
            trail.setMaxlng(spatialIndex[2]);
            trail.setMinlng(spatialIndex[3]);
            trail.save();

            Message message = new Message();
            message.what = UPDATE_TEXT;
            MainActivity.instance.handler.sendMessage(message);
        }
        else{
            Toast.makeText(RecordTrail.this, "当前轨迹为空，可能是没有GPS信号", Toast.LENGTH_LONG).show();
        }
    }

    public static int appearNumber(String srcText, String findText) {
        int count = 0;
        Pattern p = Pattern.compile(findText);
        Matcher m = p.matcher(srcText);
        while (m.find()) {
            count++;
        }
        return count;
    }

    //记录轨迹
    private void recordTrail(float x, float y){
        if (x != 0 && y != 0) {
            if (isLocateEnd || isLocate == 1) {
                if (!m_cTrail.isEmpty()) {
                    if (isLocate > 2) {
                        int num = appearNumber(m_cTrail, " ");
                        String str = m_cTrail;
                        for (int i = 0; i <= num - 2; i++) {
                            str = str.substring(str.indexOf(" ") + 1);
                        }
                        m_cTrail = m_cTrail.substring(0, m_cTrail.length() - str.length());
                    } else
                        m_cTrail = m_cTrail + " " + Float.toString(x) + " " + Float.toString(y);
                } else m_cTrail = m_cTrail + Float.toString(x) + " " + Float.toString(y);
            } else
                m_cTrail = m_cTrail + " " + Float.toString(x) + " " + Float.toString(y) + " " + Float.toString(x) + " " + Float.toString(y);
        }
        //setHereLocation();
        //locError(Integer.toString(m_lat) + "," + Double.toString(m_long) + "Come here");

        //} else {

        //}
    }

    private void getLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {
            Toast.makeText(this, "请打开网络或GPS定位功能!", Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            //startActivityForResult(intent, 0);
            return;
        }

        try {

            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location == null){
                Log.d(TAG, "onCreate.location = null");
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            Log.d(TAG, "onCreate.location = " + location);
            updateView(location);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 2, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 2, locationListener);
        }catch (SecurityException  e){
            e.printStackTrace();
        }
    }

    //坐标监听器
    protected final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //Log.d(TAG, "Location changed to: " + getLocationInfo(location));
            updateView(location);
            if (!isLocateEnd) {
                recordTrail((float)location.getLatitude(), (float)location.getLongitude());
                //locError(m_cTrail);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged() called with " + "provider = [" + provider + "], status = [" + status + "], extras = [" + extras + "]");
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.i(TAG, "AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i(TAG, "OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i(TAG, "TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled() called with " + "provider = [" + provider + "]");
            try {
                Location location = locationManager.getLastKnownLocation(provider);
                Log.d(TAG, "onProviderDisabled.location = " + location);
                updateView(location);
            }catch (SecurityException e){

            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled() called with " + "provider = [" + provider + "]");
        }
    };

    //更新坐标信息
    private void updateView(Location location) {
        //Geocoder gc = new Geocoder(this);
        //List<Address> addresses = null;
        //String msg = "";
        //Log.d(TAG, "updateView.location = " + location);
        if (location != null) {
            m_lat = location.getLatitude();
            m_long = location.getLongitude();
        }
        else{
            Toast.makeText(RecordTrail.this, "请打开GPS组件或者移动到有GPS信号的地方", Toast.LENGTH_LONG).show();
        }
        //setHereLocation();
        //locError(Double.toString(m_lat) + "&&" + Double.toString(m_long) + "Come here");
    }

    private void setIC(String ic){
        m_ic = ic;
    }
}

