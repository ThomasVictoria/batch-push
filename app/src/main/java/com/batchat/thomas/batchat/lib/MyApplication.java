package com.batchat.thomas.batchat.lib;

import android.app.Application;
import android.content.Context;

/**
 * Created by thomas on 04/04/2017.
 */

public class MyApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
