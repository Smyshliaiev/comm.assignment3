package posa15.communication.assignment3.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;

import posa15.communication.assignment3.IMyAidlInterface;
import posa15.communication.assignment3.TestData2;
import posa15.communication.assignment3.aidl.TestData;


/**
 * Created by Toxa on 01.06.2015.
 */
public class WeatherService extends Service {
    private static final String TAG = WeatherService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
    }

    @Override
    public IBinder onBind(Intent intent) {

        TestData asd2 = new TestData();
        IMyAidlInterface.Stub asd = new IMyAidlInterface.Stub() {
            @Override
            public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

            }

            @Override
            public List<TestData2> getCurrentWeather(String Weather) throws RemoteException {
                return null;
            }
        };


        return asd;
    }
}
