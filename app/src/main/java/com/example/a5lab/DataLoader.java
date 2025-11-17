package com.example.a5lab;

import android.os.AsyncTask;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DataLoader extends AsyncTask<Void, Void, InputStream> {

    public interface DataLoaderListener {
        void onDataLoaded(InputStream inputStream);
        void onError(Exception e);
    }

    private DataLoaderListener listener;
    private static final String API_URL = "https://www.floatrates.com/daily/usd.xml";

    public DataLoader(DataLoaderListener listener) {
        this.listener = listener;
        System.out.println("DataLoader: Constructor called");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        System.out.println("DataLoader: Starting background task");
    }

    @Override
    protected InputStream doInBackground(Void... voids) {
        System.out.println("DataLoader: doInBackground started with URL: " + API_URL);

        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            int responseCode = connection.getResponseCode();
            System.out.println("DataLoader: HTTP Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                System.out.println("DataLoader: Successfully got input stream");
                return inputStream;
            } else {
                throw new Exception("HTTP error code: " + responseCode);
            }

        } catch (Exception e) {
            System.out.println("DataLoader: Error in doInBackground - " + e.getMessage());
            if (listener != null) {
                listener.onError(e);
            }
            return null;
        }
    }

    @Override
    protected void onPostExecute(InputStream inputStream) {
        super.onPostExecute(inputStream);
        System.out.println("DataLoader: onPostExecute called");

        if (inputStream != null && listener != null) {
            System.out.println("DataLoader: Notifying listener of successful data load");
            listener.onDataLoaded(inputStream);
        } else {
            System.out.println("DataLoader: No data to process");
        }
    }
}