package posa15.communication.assignment3;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Toxa on 01.06.2015.
 */
public class TestData2 implements Parcelable {
    public TestData2(Parcel in) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    public static final Parcelable.Creator<TestData2> CREATOR =
            new Parcelable.Creator<TestData2>() {
                public TestData2 createFromParcel(Parcel in) {
                    return new TestData2(in);
                }

                public TestData2[] newArray(int size) {
                    return new TestData2[size];
                }
            };
}
