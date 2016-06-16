/**
 * This is a tutorial source code 
 * provided "as is" and without warranties.
 *
 * For any question please visit the web site
 * http://www.survivingwithandroid.com
 *
 * or write an email to
 * survivingwithandroid@gmail.com
 *
 */
package com.example.josemi.weatherpro;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import Model.Weather;
import Model.Location;

public class JSONWeatherParser {

	public static Weather getWeather(String data) throws JSONException {
		Weather weather = new Weather();

		// We create out JSONObject from the data
		JSONObject jObj = new JSONObject(data);
		
		// We start extracting the info
		Model.Location loc = new Model.Location();
		
		JSONObject coordObj = getObject("coord", jObj);
		loc.setLatitude(getFloat("lat", coordObj));
		loc.setLongitude(getFloat("lon", coordObj));
		
		JSONObject sysObj = getObject("sys", jObj);
		loc.setCountry(getString("country", sysObj));
		loc.setSunrise(getInt("sunrise", sysObj));
		loc.setSunset(getInt("sunset", sysObj));
		loc.setCity(getString("name", jObj));

		weather.location = loc;
		
		// We get weather info (This is an array)
		JSONArray jArr = jObj.getJSONArray("weather");
		
		// We use only the first value
		JSONObject JSONWeather = jArr.getJSONObject(0);
		weather.currentCondition.setWeatherId(getInt("id", JSONWeather));
		weather.currentCondition.setDescr(getString("description", JSONWeather));
		weather.currentCondition.setCondition(getString("main", JSONWeather));
		weather.currentCondition.setIcon(getString("icon", JSONWeather));
		
		JSONObject mainObj = getObject("main", jObj);
		weather.currentCondition.setHumidity(getInt("humidity", mainObj));
		weather.currentCondition.setPressure(getInt("pressure", mainObj));
		weather.temperature.setMaxTemp(getFloat("temp_max", mainObj));
		weather.temperature.setMinTemp(getFloat("temp_min", mainObj));
		weather.temperature.setTemp(getFloat("temp", mainObj));
		weather.dt = getLong("dt", jObj);
		// Wind
		JSONObject wObj = getObject("wind", jObj);
		weather.wind.setSpeed(getFloat("speed", wObj));
		//weather.wind.setDeg(getFloat("deg", wObj)); Open weather has been deleted this object
		// Clouds
		JSONObject cObj = getObject("clouds", jObj);
		weather.clouds.setPerc(getInt("all", cObj));

		// We download the icon to show
		return weather;
	}

	public static ArrayList<Weather> getWeatherArray(String data) throws JSONException {
		ArrayList<Weather> array_weather = new ArrayList<Weather>();
		JSONObject jObj = new JSONObject(data);

		JSONArray jArr = jObj.getJSONArray("list");

		for(int i=0; i< jArr.length();i++){
			JSONObject JSONWeather = jArr.getJSONObject(i);
			Weather weather = new Weather();
			//Set City
			JSONObject city = getObject("city", jObj);
			Model.Location loc = new Model.Location();
			loc.setCity(getString("name", city));
			weather.location = loc;

			//Set Date
			long mil = getLong("dt",JSONWeather);
			Date d = new Date(mil * 1000);
			SimpleDateFormat formatter = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss", Locale.getDefault());
			String date = formatter.format(d);
			weather.setDate(date);
			//Set temperature
			JSONObject main = getObject("main",JSONWeather);
			float tempe = getFloat("temp", main);
			tempe -= 273.15;
			weather.temperature.setTemp(tempe);
			//Set humidity
			weather.currentCondition.setHumidity(getInt("humidity", main));
			//Set Weather
			JSONArray weath = JSONWeather.getJSONArray("weather");
			JSONObject weat = weath.getJSONObject(0);
			weather.currentCondition.setCondition(getString("main", weat));
			weather.currentCondition.setDescr(getString("description", weat));
			weather.currentCondition.setIcon(getString("icon", weat));
			weather.currentCondition.setImage(getIdImage(getString("icon", weat)));

			array_weather.add(weather);
		}

		return array_weather;
	}
	
	
	private static JSONObject getObject(String tagName, JSONObject jObj)  throws JSONException {
		JSONObject subObj = jObj.getJSONObject(tagName);
		return subObj;
	}
	
	private static String getString(String tagName, JSONObject jObj) throws JSONException {
		return jObj.getString(tagName);
	}

	private static float  getFloat(String tagName, JSONObject jObj) throws JSONException {
		return (float) jObj.getDouble(tagName);
	}

	private static long  getLong(String tagName, JSONObject jObj) throws JSONException {
		return (long) jObj.getDouble(tagName);
	}
	
	private static int  getInt(String tagName, JSONObject jObj) throws JSONException {
		return jObj.getInt(tagName);
	}

	public static int getIdImage(String code) {
		int id = -1;
		switch (code) {
			case "01d":
				id = R.drawable.d1;
			break;
			case "01n":
				id = R.drawable.n1;
			break;
			case "02d":
				id = R.drawable.d2;
			break;
			case "02n":
				id = R.drawable.n2;
			break;
			case "03d":
				id = R.drawable.d3;
			break;
			case "03n":
				id = R.drawable.n3;
			break;
			case "04d":
				id = R.drawable.d4;
			break;
			case "04n":
				id = R.drawable.n4;
			break;
			case "09d":
				id = R.drawable.d9;
			break;
			case "09n":
				id = R.drawable.n9;
			break;
			case "10d":
				id = R.drawable.d10;
			break;
			case "10n":
				id = R.drawable.n10;
			break;
			case "11d":
				id = R.drawable.d11;
			break;
			case "11n":
				id = R.drawable.n11;
			break;
			case "13d":
				id = R.drawable.d13;
			break;
			case "13n":
				id = R.drawable.n13;
			break;
			case "50d":
				id = R.drawable.d50;
			break;
			case "50n":
				id = R.drawable.n50;
			break;
			default:
				break;
		}
		return id;
	}

}
