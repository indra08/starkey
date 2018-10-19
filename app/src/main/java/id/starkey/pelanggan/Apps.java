package id.starkey.pelanggan;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Dani on 6/7/2018.
 */

public class Apps extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        /**
         * For crash report firebase.
         */
        Fabric.with(this, Crashlytics.getInstance());
    }

    public static Context getmContext() {
        return mContext;
    }
}
