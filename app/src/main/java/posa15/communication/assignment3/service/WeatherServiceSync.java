package posa15.communication.assignment3.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import posa15.communication.assignment3.WeatherCall;
import posa15.communication.assignment3.WeatherData;
import posa15.communication.assignment3.http.HttpManager;
import posa15.communication.assignment3.jsonweather.JsonWeather;
import posa15.communication.assignment3.jsonweather.Sys;
import posa15.communication.assignment3.jsonweather.WeatherJSONParser;


/**
 * Created by Toxa on 01.06.2015.
 */
public class WeatherServiceSync extends Service {
    private static final String TAG = WeatherServiceSync.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
    }

    @Override
    public IBinder onBind(Intent intent) {

//        TestData asd2 = new TestData();
//        IMyAidlInterface.Stub asd = new IMyAidlInterface.Stub() {
//            @Override
//            public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
//
//            }
//
//            @Override
//            public List<TestData2> getCurrentWeather(String Weather) throws RemoteException {
//                return null;
//            }
//        };

        WeatherCall.Stub wc = new WeatherCall.Stub() {
            @Override
            public List<WeatherData> getCurrentWeather(String Weather) throws RemoteException {

                HttpManager httpManager = new HttpManager();
                String res = httpManager.GetQuery(Weather);
                Log.d(TAG, "res: " + res);

                if(res == null) {
                    return null;
                }

                WeatherData wdata = null;

                try {
//                    WeatherJSONParser parser = new WeatherJSONParser();
//                    InputStream stream = new ByteArrayInputStream(res.getBytes("UTF-8"));
//                    List<JsonWeather> list = parser.parseJsonStream(stream);
//
                    JSONObject jObj = new JSONObject(res);

                    JSONObject wind = jObj.getJSONObject("wind");
                    JSONObject main = jObj.getJSONObject("main");
                    JSONObject sys = jObj.getJSONObject("sys");
                    double speed = wind.getDouble("speed");
                    double deg = wind.getDouble("deg");
                    double temp = main.getDouble("temp");
                    String name = jObj.getString("name");
                    long hum = main.getLong("humidity");
                    long sunrise = sys.getLong("sunrise");
                    long sunset = sys.getLong("sunset");

                    Log.d(TAG, "speed: " + speed);
                    Log.d(TAG, "temp: " + temp);
                    Log.d(TAG, "deg: " + deg);
                    Log.d(TAG, "name: " + name);
                    Log.d(TAG, "hum: " + hum);
                    Log.d(TAG, "sunrise: " + sunrise);
                    Log.d(TAG, "sunset: " + sunset);
                    //InputStream stream = new ByteArrayInputStream(exampleString.getBytes(StandardCharsets.UTF_8));

                    wdata = new WeatherData(name, speed, deg, temp, hum, sunrise, sunset);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                List<WeatherData> wd = new ArrayList<>();
                wd.add(wdata);

                return wd;
            }

            @Override
            public IBinder asBinder() {
                return null;
            }
        };

        return wc;
    }

}
