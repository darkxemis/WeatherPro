package com.example.josemi.weatherpro;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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

public class WeatherActivity extends AppCompatActivity {
    private TextView temp,cit,dat,des,hum;
    private ImageView imgView, imgView2;
    JSONWeatherTask request_task;
    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    public double longitud, latitud;
    GPSTracker location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        location = new GPSTracker(this);

        final LocationManager manager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER ))
            location.showSettingsAlert();

        longitud = location.getLongitude();
        latitud = location.getLatitude();

        String font_path = "font/arial.ttf"; // Fonts that I use to the information
        Typeface TF = Typeface.createFromAsset(getAssets(),font_path);

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

        request_task = new JSONWeatherTask();

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.container);
        //mSwipeRefreshLayout.setOnRefreshListener(this);
        System.out.println("caca1 " + longitud + " " + latitud);

        if (mWifi.isConnected() && (longitud != 0 || latitud != 0)){
            request_task.execute(latitud,longitud);
            System.out.println("caca2" + Double.toString(longitud) + " " + Double.toString(latitud));
        }else{
            if (mMobile.isConnected() && (longitud != 0 && latitud != 0)) {
                request_task.execute(latitud,longitud);
            }else{
                Toast.makeText(WeatherActivity.this, "No tiene acceso a internet por favor compruebalo en los ajustes",
                        Toast.LENGTH_LONG).show();
            }
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                System.out.println("piton");
                JSONWeatherTask ejemplo = new JSONWeatherTask();
                ejemplo.execute(latitud,longitud);
            }
        });

    }

    private class JSONWeatherTask extends AsyncTask<Double, Void, Weather> {

        @Override
        protected Weather doInBackground(Double... locale) {
            Weather weather = new Weather();
            JsonRequest request = new JsonRequest();

            String json = request.JRequest(locale[0], locale[1]);
            //System.out.println("yason" + json);

            try {
                weather = JSONWeatherParser.getWeather(json);
                weather.iconData = ((new JsonRequest()).getImage(weather.currentCondition.getIcon()));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return weather;
        }

        @Override
        public void onPostExecute(Weather weather) {

            if (weather.iconData != null && weather.iconData.length > 0) {
                Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
                imgView2.setImageBitmap(img);
                //imgView2.setImageBitmap(Bitmap.createScaledBitmap(img,128,128,false));
            }

            String code = weather.currentCondition.getIcon();

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
            long mil = weather.dt;
            Date d = new Date(mil *1000);
            SimpleDateFormat formatter = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss", Locale.getDefault());
            String date = formatter.format(d);

            Locale loc = new Locale("",weather.location.getCountry());
            dat.setText(date);
            cit.setText(weather.location.getCity() + ", " + loc.getDisplayCountry());
            temp.setText(Math.round((weather.temperature.getTemp() - 273.15)) + "ºC");
            des.setText(weather.currentCondition.getDescr());
            hum.setText("Humidity: " + weather.currentCondition.getHumidity() + "%");
            //mSwipeRefreshLayout.setRefreshing(false);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String CurrentDate(){
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", java.util.Locale.getDefault()).format(Calendar.getInstance().getTime());
    }

}
