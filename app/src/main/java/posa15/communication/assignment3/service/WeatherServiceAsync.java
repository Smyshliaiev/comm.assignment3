package posa15.communication.assignment3.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import posa15.communication.assignment3.WeatherCall;
import posa15.communication.assignment3.WeatherData;
import posa15.communication.assignment3.WeatherRequest;
import posa15.communication.assignment3.WeatherResults;
import posa15.communication.assignment3.http.HttpManager;

/**
 * Created by anton on 03.06.15.
 */
public class WeatherServiceAsync extends Service{
    private static final String TAG = WeatherServiceAsync.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
    }

    @Override
    public IBinder onBind(Intent intent) {

        WeatherRequest.Stub weatherRequest = new WeatherRequest.Stub() {
            @Override
            public void getCurrentWeather(String Weather, WeatherResults results) throws RemoteException {

            WeatherTask wt = new WeatherTask(Weather, results);
                wt.execute();

            }
        };

        return weatherRequest;
    }

    class WeatherTask extends AsyncTask<Void, Void, List<WeatherData>> {


        private String url;
        private WeatherResults results;

        public WeatherTask(String url, WeatherResults results) {
            this.url = url;
            this.results = results;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<WeatherData> doInBackground(Void... params) {
            List<WeatherData> list = null;


            HttpManager httpManager = new HttpManager();
            String res = httpManager.GetQuery(url);
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
        protected void onPostExecute(List<WeatherData> result) {

            try {
                results.sendResults(result);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            super.onPostExecute(result);
        }
    }
}
