package posa15.communication.assignment3;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import posa15.communication.assignment3.service.WeatherServiceAsync;
import posa15.communication.assignment3.service.WeatherServiceSync;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    ServiceConnection sConnSync;
    ServiceConnection sConnAsync;
    //WeatherServiceSync service;
    boolean bound = false;
    Intent intentSync;
    Intent intentAsync;
    WeatherCall wcall;
    WeatherRequest wreq;
    TextView name,temp, speed, deg, hum, sset, ssrise;
    EditText city;

    public MainActivityFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Log.d(LOG_TAG, "onAttach");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCrate");
        setRetainInstance(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(LOG_TAG, "onDetach");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_main, container, false);

        name = (TextView) root.findViewById(R.id.textView_name);
        temp = (TextView) root.findViewById(R.id.textView_temp);
        speed = (TextView) root.findViewById(R.id.textView_speed);
        deg = (TextView) root.findViewById(R.id.textView_deg);
        hum = (TextView) root.findViewById(R.id.textView_hum);
        sset = (TextView) root.findViewById(R.id.textView_sunset);
        ssrise = (TextView) root.findViewById(R.id.textView_sunrise);
        name = (TextView) root.findViewById(R.id.textView_name);
        city = (EditText) root.findViewById(R.id.editText_cityName);

        final View button = root.findViewById(R.id.button_sync);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(checkCacheIsExpired()) {
                            Log.d(LOG_TAG, "sync request starting");
                            Toast.makeText(getActivity(), "Sync request calling", Toast.LENGTH_SHORT).show();
                            WeatherTask wt = new WeatherTask();
                            wt.execute();
                        }else{
                            Toast.makeText(getActivity(), "Update from cache", Toast.LENGTH_SHORT).show();
                            renewUiResults(Cache.hashMap.entrySet().iterator().next().getValue());
                        }
                    }
                }
        );

        final View buttona= root.findViewById(R.id.button_async);
        buttona.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                            WeatherResults listener = new WeatherResults() {
                                @Override
                                public void sendResults(List<WeatherData> result) throws RemoteException {
                                    Log.d(LOG_TAG, "async response got");
                                    if(result!=null && result.size()>0) {
                                       renewUiResults(result.get(0));

                                        Cache.hashMap.clear();
                                        Cache.hashMap.put(System.currentTimeMillis(), result.get(0));
                                    }
                                }

                                @Override
                                public IBinder asBinder() {
                                    return null;
                                }
                            };

                            if(checkCacheIsExpired()) {
                                Log.d(LOG_TAG, "async request starting");
                                Toast.makeText(getActivity(), "Async request calling", Toast.LENGTH_SHORT).show();
                                wreq.getCurrentWeather("http://api.openweathermap.org/data/2.5/weather?q=" + city.getText(), listener);
                            }else{
                                Toast.makeText(getActivity(), "Update from cache", Toast.LENGTH_SHORT).show();
                                renewUiResults(Cache.hashMap.entrySet().iterator().next().getValue());
                            }


                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                    }
                });

        sConnSync = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d(LOG_TAG, "MainActivityFragment onServiceSConnected");
                bound = true;
                wcall = WeatherCall.Stub.asInterface(binder);
            }

            public void onServiceDisconnected(ComponentName name) {
                Log.d(LOG_TAG, "MainActivityFragment onServiceSDisconnected");
                bound = false;
                wcall = null;
            }
        };

        sConnAsync = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d(LOG_TAG, "MainActivityFragment onServiceAConnected");
                bound = true;
                wreq = WeatherRequest.Stub.asInterface(binder);
            }

            public void onServiceDisconnected(ComponentName name) {
                Log.d(LOG_TAG, "MainActivityFragment onServiceADisconnected");
                bound = false;
                wreq = null;
            }
        };

       return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (bound) {
            getActivity().unbindService(sConnSync);
            getActivity().unbindService(sConnAsync);
        }
        Log.d(LOG_TAG, "MainActivityFragment onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        intentSync = new Intent(getActivity(), WeatherServiceSync.class);
        getActivity().bindService(intentSync, sConnSync, Context.BIND_AUTO_CREATE);

        intentAsync = new Intent(getActivity(), WeatherServiceAsync.class);
        getActivity().bindService(intentAsync, sConnAsync, Context.BIND_AUTO_CREATE);

        if(Cache.hashMap!=null && !Cache.hashMap.isEmpty()){
            renewUiResults(Cache.hashMap.entrySet().iterator().next().getValue());
        }


        Log.d(LOG_TAG, "MainActivityFragment onResume");
    }


    class WeatherTask extends AsyncTask<Void, Void, List<WeatherData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<WeatherData> doInBackground(Void... params) {
            List<WeatherData> list = null;
            try {
                list = wcall.getCurrentWeather("http://api.openweathermap.org/data/2.5/weather?q=" + city.getText());
                Log.d(LOG_TAG, "list size: " + list.size());
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            return list;
        }

        @Override
        protected void onPostExecute(List<WeatherData> result) {

            if(result.size()>0) {
                renewUiResults(result.get(0));

                Cache.hashMap.clear();
                Cache.hashMap.put(System.currentTimeMillis(), result.get(0));
            }



            super.onPostExecute(result);
        }
    }


    private void renewUiResults(WeatherData result){
        Log.d(LOG_TAG, "renew results: " + result.toString());
        name.setText(result.getmName());
        temp.setText(String.valueOf(result.getmTemp()));
        speed.setText(String.valueOf(result.getmSpeed()));
        deg.setText(String.valueOf(result.getmDeg()));
        hum.setText(String.valueOf(result.getmHumidity()));
        sset.setText(String.valueOf(result.getmSunset()));
        ssrise.setText(String.valueOf(result.getmSunrise()));

    }

    private boolean checkCacheIsExpired(){

        if (Cache.hashMap == null) {
            Cache.hashMap = new HashMap<>();
            return true;
        }

        if(Cache.hashMap.isEmpty()){
            return true;
        }

        for ( Long key : Cache.hashMap.keySet() ) {
            long cur = System.currentTimeMillis();
            Log.d(LOG_TAG, "cur: " + cur);
            Log.d(LOG_TAG, "key: " + key);
            if(cur > (key+10000)){
                Cache.hashMap.clear();
                return true;
            }
        }
        return false;
    }

    private boolean checkCache(long time, WeatherData data){

        if (Cache.hashMap == null) {
            Cache.hashMap = new HashMap<>();
            return false;
        }

        if(Cache.hashMap.isEmpty()){
            Cache.hashMap.put(time, data);
        }

        for ( Long key : Cache.hashMap.keySet() ) {
            long cur = System.currentTimeMillis();
            if(cur > (key+10000)){
                Cache.hashMap.clear();
                Cache.hashMap.put(time, data);
            }
        }
        return true;
    }
}
