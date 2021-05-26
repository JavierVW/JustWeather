package com.jvanwyk.justweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = "001";


    //API key
    static {
        System.loadLibrary("keys");
    }
    public native String getNativeKey1();
    String key1 = new String(Base64.decode(getNativeKey1(),Base64.DEFAULT));



    TextView resultTextView;
    TextView cityTextView;
    TextView windTextView, tempTextView, feelsTextView, maxTextView, minTextView;

    LocationManager locationManager;
    LocationListener locationListener;


    //    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 3;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 30;
    private static final long MIN_TIME_BW_UPDATES_QUICK = 1000;



    String latVal = "";
    String longVal = "";
    String main = "";

    //detail strings
    String nameInfo = "";
    String tempCurrent = "";
    String tempMax = "";
    String tempMin = "";
    String tempFeel = "";
    String windSpeed = "";

    String windDirection;

    //strings for concat
    String tempHolder = "";
    String feelsHolder;
    String maxHolder = "";
    String minHolder = "";
    String windHolder = "";


    //temp strings
    String btnRef = "button refreshed";

    public void refresh() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES_QUICK, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, 0, locationListener);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                //ask perm
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, 0, locationListener);
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if(loc!=null){
                    latVal = String.valueOf(loc.getLatitude());
                longVal = String.valueOf(loc.getLongitude());
                }

                Log.i("changed val: ", latVal);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDelegate().setLocalNightMode(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);


        setContentView(R.layout.activity_main);




        resultTextView = findViewById(R.id.resultTextView);
        cityTextView = findViewById(R.id.latTextView);
        windTextView = findViewById(R.id.longTextView);
        tempTextView = findViewById(R.id.tempTextView);
        feelsTextView = findViewById(R.id.feelsTextView);
        maxTextView = findViewById(R.id.maxTextView);
        minTextView = findViewById(R.id.minTextView);



        FloatingActionButton fab = findViewById(R.id.refreshFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("on click ", btnRef);

                refresh();
            }
        });


       String hello = "blah blah" + latVal + "blah balh";
        Log.i("the link thing is ", hello);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.i("location onchanged", location.toString());
                latVal = String.valueOf(location.getLatitude());
                longVal = String.valueOf(location.getLongitude());
                DownloadTask task = new DownloadTask();

                task.execute("https://api.openweathermap.org/data/2.5/weather?lat=" + latVal + "&lon=" + longVal + "&units=metric&appid=" + key1);





            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //ask perm

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
        else{
            //perm granted
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, 0, locationListener);

            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(loc!=null){
                latVal = String.valueOf(loc.getLatitude());
                longVal = String.valueOf(loc.getLongitude());
            }

            Log.i("permgranted val: ", latVal);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        super.onOptionsItemSelected(item);

        if (item.getItemId()==R.id.helpMenu) {

            Log.i("Menu item selected", "Settings");

            startActivity(new Intent(this, HelpActivity.class));
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    //notification
    public void ShowNoti(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, 0);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Persistent Weather Notification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Description here");

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            notificationManager.createNotificationChannel(notificationChannel);

            Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID);

            builder.setContentTitle(nameInfo)
                    .setContentText("Feels like " + tempFeel + " \u00B0C" + " | " + main)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(001, builder.build());
        }
        else{

            Notification.Builder builder = new Notification.Builder(this);
            builder.setContentTitle(nameInfo)
                    .setContentText("Feels like " + tempFeel + " \u00B0C")
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(001, builder.build());
        }
    }

    //download info
    public class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String results) {
            super.onPostExecute(results);

            try {
                String mes = "";
                String name = "";
                main = "";

                JSONObject jsonObject = new JSONObject(results);
                String weatherInfo = jsonObject.getString("weather");
                //String mainInfo = jsonObject.getString("sys");


                nameInfo = jsonObject.getString("name");



                tempCurrent = jsonObject.getJSONObject("main").getString("temp");
                tempFeel = jsonObject.getJSONObject("main").getString("feels_like");
                tempMin = jsonObject.getJSONObject("main").getString("temp_min");
                tempMax = jsonObject.getJSONObject("main").getString("temp_max");
                windSpeed = jsonObject.getJSONObject("wind").getString("speed");
                windDirection = jsonObject.getJSONObject("wind").getString("deg");

                getWindDir(windDirection);




                Log.i("weather content", weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);




                for (int i = 0; i < arr.length(); i++){
                    JSONObject jsonSec = arr.getJSONObject(i);

                    String description = "";


                    main = jsonSec.getString("main");
                    description = jsonSec.getString("description");
                    //name = jsonSec.getString("name");

                    if (main != "" && description != ""){

                        mes += main + ": " + description + "\r\n";

                    }
                }

                if (mes != ""){
                    tempHolder = tempCurrent + " \u00B0C";
                    feelsHolder = tempFeel + " \u00B0C";
                    maxHolder = tempMax + " \u00B0C";
                    minHolder = tempMin + " \u00B0C";
                    windHolder = windSpeed + " m/s " + windDirection;

                    resultTextView.setText(main);
                    cityTextView.setText(nameInfo);
                    windTextView.setText(windHolder);
                    tempTextView.setText(tempHolder);
                    feelsTextView.setText(feelsHolder);
                    maxTextView.setText(maxHolder);
                    minTextView.setText(minHolder);

                    ShowNoti();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    //convert wind
    public void getWindDir(String deg){

        double degree = Double.parseDouble(deg);

        if (degree>337.5)
            windDirection = "N";
        else if (degree>292.5)
            windDirection = "NW";
        else if(degree>247.5)
            windDirection = "W";
        else if(degree>202.5)
            windDirection = "SW";
        else if(degree>157.5)
            windDirection = "S";
        else if(degree>122.5)
            windDirection = "SE";
        else if(degree>67.5)
            windDirection = "E";
        else if(degree>22.5)
            windDirection = "NE";
        else
            windDirection = "N";
    }
}