package com.example.josemi.weatherpro;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import Model.Weather;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList <Weather> array_weather;

    public MyAdapter(ArrayList <Weather> array_weather) {
        this.array_weather = array_weather;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Weather weather;
        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData
        weather = array_weather.get(position);
        viewHolder.txtViewTitle.setText(String.valueOf(Math.round(weather.temperature.getTemp())) + "ºC" + " " + weather.currentCondition.getDescr());
        viewHolder.imgViewIcon.setImageResource(weather.currentCondition.getImage());
        viewHolder.txtViewDate.setText(weather.date);
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtViewTitle;
        public ImageView imgViewIcon;
        public TextView txtViewDate;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.item_title);
            imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.item_icon);
            txtViewDate = (TextView) itemLayoutView.findViewById(R.id.item_date);
        }
    }


    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return array_weather.size();
    }
}

