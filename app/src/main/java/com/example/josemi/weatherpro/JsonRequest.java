package com.example.josemi.weatherpro;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class JsonRequest {
    HttpURLConnection con = null;
    InputStream is = null;
    private static String IMG_URL = "http://openweathermap.org/img/w/";
    private static String API_KEY = "&APPID=155887cf870c42f26bf2577ba1ce785f";
    //private static String OPEN_WEATHER_API = "http://api.openweathermap.org/data/2.5/weather?lat=";

    protected String JRequest(double latitud, double longitud, String OPEN_WEATHER_API) {
        try {
            URL url = new URL(OPEN_WEATHER_API + latitud + "&lon=" + longitud + API_KEY);
            con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            String responseMsg = con.getResponseMessage();
            int response = con.getResponseCode();
            con.connect();

            // Let's read the response
            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null)
                buffer.append(line + "rn");
            is.close();
            con.disconnect();
            return buffer.toString();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Throwable t) {
            }
            try {
                con.disconnect();
            } catch (Throwable t) {
            }
        }
        return null;
    }

    public byte[] getImage(String code) {
        HttpURLConnection con2 = null ;
        InputStream is2 = null;
        try {
            con2 = (HttpURLConnection) ( new URL(IMG_URL + code + ".png")).openConnection();
            con2.connect();
            // Let's read the response
            is2 = con2.getInputStream();
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while (is2.read(buffer) != -1) {
                baos.write(buffer);
            }

            return baos.toByteArray();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            try { is2.close(); } catch(Throwable t) {}
            try { con2.disconnect(); } catch(Throwable t) {}
        }

        return null;

    }
}
