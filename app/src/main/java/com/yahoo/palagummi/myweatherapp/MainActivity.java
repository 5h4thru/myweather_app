package com.yahoo.palagummi.myweatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView resultTextView;


    public void findWeather(View view) {
        // Log.i("city name ", cityName.getText().toString());
        DownloadTask task = new DownloadTask();
        String city = cityName.getText().toString();
        String weatherURL = "http://api.openweathermap.org/data/2.5/weather?q="+city+"&APPID=9dd67e4dba6524581f8a490cf8313324";
        task.execute(weatherURL);

        // code to make the keyboard go away once the button is pressed
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); // gets the service that the system cuurently uses (in this case it is a keyboard which is used to type in the cityName)
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(),0);
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject jsonObject;
            JSONArray arr;
            String message = "";
            try {
                jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");
                arr = new JSONArray(weatherInfo);
                for(int i=0; i<arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");
                    if(main != null && description != null)
                        message += main + ": " + description + "\r\n";
                    // resultTextView.setText(main + "\n" + description);
                    resultTextView.setText(message);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the cityName editText, resultTextView
        cityName = (EditText) findViewById(R.id.cityName);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
    }
}
