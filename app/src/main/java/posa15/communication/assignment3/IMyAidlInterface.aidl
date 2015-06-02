// IMyAidlInterface.aidl
package posa15.communication.assignment3;

// Declare any non-default types here with import statements
import posa15.communication.assignment3.TestData2;

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    List<TestData2> getCurrentWeather(in String Weather);
}
