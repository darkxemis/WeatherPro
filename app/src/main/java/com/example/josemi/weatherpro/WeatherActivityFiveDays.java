package com.example.josemi.weatherpro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Model.Weather;

public class WeatherActivityFiveDays extends AppCompatActivity {
    private final String OPEN_WEATHER_API = "http://api.openweathermap.org/data/2.5/forecast?lat=";
    public RecyclerView recyclerView;
    public TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_activity_five_days);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // 2. set layoutManger
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        text = (TextView) findViewById(R.id.title);

        Bundle bundle = getIntent().getExtras();
        Double longitud = bundle.getDouble("longitud");
        Double latitud = bundle.getDouble("latitud");

        JSONWeatherTask request_task = new JSONWeatherTask(latitud,longitud,OPEN_WEATHER_API);
        request_task.execute();


    }

    class JSONWeatherTask extends AsyncTask<Void, Void, ArrayList<Weather>> {
        private ProgressDialog progress = null;
        double latitude;
        double longitud;
        String url;

        public JSONWeatherTask(double latitude, double longitud, String url) {
            this.latitude = latitude;
            this.longitud = longitud;
            this.url = url;
        }


        @Override
        protected ArrayList<Weather> doInBackground(Void... locale) {

            ArrayList<Weather> weather= new ArrayList<Weather>();
            JsonRequest request = new JsonRequest();

            String json = request.JRequest(this.latitude, this.longitud, this.url);

            try {
                weather = JSONWeatherParser.getWeatherArray(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return weather;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        public void onPostExecute(ArrayList weather) {
            Weather weath;
            weath = (Weather) weather.get(0);
            text.setText(weath.location.getCity());

            // 3. create an adapter
            MyAdapter mAdapter = new MyAdapter(weather);
            // 4. set adapter
            recyclerView.setAdapter(mAdapter);
            // 5. set item animator to DefaultAnimator
            recyclerView.setItemAnimator(new DefaultItemAnimator());

        }
    }

    // Very important if you want show same setting menu as WeatherActivity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_weather_drawer, menu);
        return true;
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
}
