package com.example.josemi.weatherpro;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class ErrorConnection extends AppCompatActivity{
    public static boolean active = false;
    private NetworkInfo mWifi, mMobile;
    private ConnectivityManager connManager;
    private boolean isGpsEnabled, isInternetEnabled;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_connection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        final LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        final Handler h = new Handler();
        final int delay = 3000; //milliseconds
        h.postDelayed(new Runnable() {
            public void run() {
                isGpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isInternetEnabled = checkInternet();
                if (isInternetEnabled && isGpsEnabled){
                    close();
                }
                h.postDelayed(this, delay);
            }
        }, delay);
    }


    public void close(){
        this.finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        active = false;
    }

    public boolean checkInternet(){
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(mWifi.isConnected() || mMobile.isConnected()){
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClickWifi(View v){
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);
    }
    public void onClickGps(View v){
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

}
