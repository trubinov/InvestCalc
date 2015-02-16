package ru.nts.investcalc;

import android.app.Application;

/**
 * Created by Николай on 01.02.2015.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MyDatabaseManager.getInstance().init(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        MyDatabaseManager.getInstance().release();
    }
}
