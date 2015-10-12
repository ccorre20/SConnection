package co.edu.eafit.pi1.sconnection.Extras;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by tflr on 10/11/15.
 */
public class ActivityExtra implements Parcelable {

    AppCompatActivity appCompatActivity;

    public int describeContents() {
        return 0;
    }

    // Write the values we want to save to the `Parcel`.

    public void writeToParcel(Parcel out, int flags) {
        out.writeValue(appCompatActivity);
    }

    public static final Parcelable.Creator<ActivityExtra> CREATOR
            = new Parcelable.Creator<ActivityExtra>() {
        public ActivityExtra createFromParcel(Parcel in) {
            return new ActivityExtra(in);
        }

        public ActivityExtra[] newArray(int size) {
            return new ActivityExtra[size];
        }
    };

    // Using the `in` variable, we can retrieve the values that
    // we originally wrote into the `Parcel`.  This constructor is usually
    // private so that only the `CREATOR` field can access.

    private ActivityExtra(Parcel in) {
        appCompatActivity = in.readParcelable(ActivityExtra.class.getClassLoader());
    }

    public ActivityExtra( AppCompatActivity appCompatActivity){
        this.appCompatActivity = appCompatActivity;
    }

    public AppCompatActivity getAppCompatActivity(){
        return appCompatActivity;
    }


}
