package com.example.josemi.weatherpro;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Model.GPSTracker;
import Model.Weather;

public class WeatherActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView temp, cit, dat, des, hum;
    private ImageView imgView, imgView2;
    public JSONWeatherTask request_task;
    public double longitud, latitud;
    GPSTracker location;
    public NetworkInfo mWifi, mMobile;
    ConnectivityManager connManager;
    public boolean isGpsEnabled;
    private static String OPEN_WEATHER_API = "http://api.openweathermap.org/data/2.5/weather?lat=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //location = new GPSTracker(this);
        final LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        isGpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        //longitud = location.getLongitude();
        //latitud = location.getLatitude();

        refreshLocation();
        loadTextView();

        request_task = new JSONWeatherTask(latitud,longitud,OPEN_WEATHER_API);

        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (mWifi.isConnected() && (longitud != 0 || latitud != 0)) {
            request_task.execute();
        } else {
            if (mMobile.isConnected() && (longitud != 0 && latitud != 0)) {
                request_task.execute();
            } else {
                //Change activity
                ChangeActivityError();
            }
        }

        refreshWeather();
    }


    void loadTextView(){
        String font_path = "font/arial.ttf"; // Fonts that I use to the information
        Typeface TF = Typeface.createFromAsset(getAssets(), font_path);

        dat = (TextView) findViewById(R.id.date);
        temp = (TextView) findViewById(R.id.temperature);
        cit = (TextView) findViewById(R.id.city);
        des = (TextView) findViewById(R.id.description);
        hum = (TextView) findViewById(R.id.humidity);

        imgView = (ImageView) findViewById(R.id.condIcon);
        imgView2 = (ImageView) findViewById(R.id.condIconTitule);
        // Change font items
        cit.setTypeface(TF);
        temp.setTypeface(TF);
        cit.setTypeface(TF);
        des.setTypeface(TF);
        hum.setTypeface(TF);
    }

    void refreshWeather(){
        final Handler h = new Handler();
        final int delay = 8000; //milliseconds

        h.postDelayed(new Runnable() {
            public void run() {
                boolean checkNet = checkInternet();
                /*
                GPSTracker loc;
                loc = new GPSTracker(getAppContext());
                latitud = loc.getLatitude();
                longitud = loc.getLongitude();
                */
                refreshLocation();
                if(!ErrorConnection.active){
                    if(checkNet){
                        if(longitud != 0 && latitud != 0){
                            if(temp.getText() == ""){
                                JSONWeatherTask petition = new JSONWeatherTask(latitud,longitud,OPEN_WEATHER_API);
                                petition.execute();
                            }
                        }else {
                            ChangeActivityError();
                        }
                    }else{
                        ChangeActivityError();
                    }
                }
                h.postDelayed(this, delay);
            }
        }, delay);
    }

    public void refreshLocation(){
        location = new GPSTracker(this);
        longitud = location.getLongitude();
        latitud = location.getLatitude();
    }

    public String CurrentDate(){
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", java.util.Locale.getDefault()).format(Calendar.getInstance().getTime());
    }

    public void ChangeActivityError(){
        Intent intent = new Intent(this, ErrorConnection.class);
        startActivity(intent);
    }

    public boolean checkInternet(){
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(mWifi.isConnected() || mMobile.isConnected()){
            return true;
        }
        return false;
    }

    public static Context getAppContext() {
        return WeatherActivity.getAppContext();
    }

    private class JSONWeatherTask extends AsyncTask<Void, Void, Weather> {
        private ProgressDialog progress = null;
        double latitude;
        double longitud;
        String url;

        public JSONWeatherTask(double latitude, double longitud, String url){
            this.latitude = latitude;
            this.longitud = longitud;
            this.url = url;
        }


        @Override
        protected Weather doInBackground(Void... locale) {
            Weather weather = new Weather();
            JsonRequest request = new JsonRequest();

            String json = request.JRequest(this.latitude,this.longitud,this.url);

            try {
                weather = JSONWeatherParser.getWeather(json);
                weather.iconData = ((new JsonRequest()).getImage(weather.currentCondition.getIcon()));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return weather;
        }

        @Override
        protected void onPreExecute() {
            //start the progress dialog

            progress = ProgressDialog.show(WeatherActivity.this, null, "Getting concurrent weather");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            super.onPreExecute();

        }

        @Override
        public void onPostExecute(Weather weather) {

            if (weather.iconData != null && weather.iconData.length > 0) {
                Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
                //imgView2.setImageBitmap(img);
                //imgView2.setImageBitmap(Bitmap.createScaledBitmap(img,128,128,false));
            }

            String code = weather.currentCondition.getIcon();
            if (code != null) {
                switch (code) {
                    case "01d":
                        imgView.setImageResource(R.drawable.d1);
                        break;
                    case "01n":
                        imgView.setImageResource(R.drawable.n1);
                        break;
                    case "02d":
                        imgView.setImageResource(R.drawable.d2);
                        break;
                    case "02n":
                        imgView.setImageResource(R.drawable.n2);
                        break;
                    case "03d":
                        imgView.setImageResource(R.drawable.d3);
                        break;
                    case "03n":
                        imgView.setImageResource(R.drawable.n3);
                        break;
                    case "04d":
                        imgView.setImageResource(R.drawable.d4);
                        break;
                    case "04n":
                        imgView.setImageResource(R.drawable.n4);
                        break;
                    case "09d":
                        imgView.setImageResource(R.drawable.d9);
                        break;
                    case "09n":
                        imgView.setImageResource(R.drawable.n9);
                        break;
                    case "10d":
                        imgView.setImageResource(R.drawable.d10);
                        break;
                    case "10n":
                        imgView.setImageResource(R.drawable.n10);
                        break;
                    case "11d":
                        imgView.setImageResource(R.drawable.d11);
                        break;
                    case "11n":
                        imgView.setImageResource(R.drawable.n11);
                        break;
                    case "13d":
                        imgView.setImageResource(R.drawable.d13);
                        break;
                    case "13n":
                        imgView.setImageResource(R.drawable.n13);
                        break;
                    case "50d":
                        imgView.setImageResource(R.drawable.d50);
                        break;
                    case "50n":
                        imgView.setImageResource(R.drawable.n50);
                        break;
                    default:
                        break;
                }
            }

            long mil = weather.dt;
            Date d = new Date(mil * 1000);
            SimpleDateFormat formatter = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss", Locale.getDefault());
            String date = formatter.format(d);


            Locale loc = new Locale("", weather.location.getCountry());
            dat.setText(date);
            cit.setText(weather.location.getCity() + ", " + loc.getDisplayCountry());
            temp.setText(Math.round((weather.temperature.getTemp() - 273.15)) + "ºC");
            des.setText(weather.currentCondition.getDescr());
            hum.setText("Humidity: " + weather.currentCondition.getHumidity() + "%");

            progress.dismiss();

        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_weather_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                break;
            case R.id.nav_five:
                Intent intent = new Intent(this, WeatherActivityFiveDays.class);
                intent.putExtra("longitud", longitud);
                intent.putExtra("latitud",latitud);
                startActivity(intent);
                break;
            case R.id.nav_about:
                showInfo();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                break;
            case R.id.nav_five:
                Intent intent = new Intent(this, WeatherActivityFiveDays.class);
                intent.putExtra("longitud", longitud);
                intent.putExtra("latitud",latitud);
                startActivity(intent);
                break;
            case R.id.nav_about:
                showInfo();
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void showInfo(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        // Setting Dialog Title
        alertDialog.setTitle("WeatherPro");

        // Setting Dialog Message
        alertDialog.setMessage("WeatherPro has been created by Josemi for the project school. All rights reserved ");

        // On pressing Settings button
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }
    @Override
    public void onDestroy()
    {
        this.finish();
        System.exit(0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
