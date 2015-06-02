package posa15.communication.assignment3;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import posa15.communication.assignment3.service.WeatherService;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    ServiceConnection sConn;
    WeatherService service;
    boolean bound = false;
    Intent intent;
    WeatherCall wcall;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_main, container, false);

        final View button = root.findViewById(R.id.button_sync);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Thread thread = new Thread(new Runnable(){
                            @Override
                            public void run() {
                                try {
                                    wcall.getCurrentWeather(null);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        thread.start();

                    }
                }
        );

        sConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d(LOG_TAG, "MainActivityFragment onServiceConnected");
                bound = true;
                wcall = WeatherCall.Stub.asInterface(binder);
            }

            public void onServiceDisconnected(ComponentName name) {
                Log.d(LOG_TAG, "MainActivityFragment onServiceDisconnected");
                bound = false;
                wcall = null;
            }
        };
       return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (bound) {
            getActivity().unbindService(sConn);
        }
        Log.d(LOG_TAG, "MainActivityFragment onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        intent = new Intent(getActivity(), WeatherService.class);
        getActivity().bindService(intent, sConn, Context.BIND_AUTO_CREATE);
        Log.d(LOG_TAG, "MainActivityFragment onResume");
    }



}
