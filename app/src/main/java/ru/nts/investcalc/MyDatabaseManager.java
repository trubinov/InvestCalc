package ru.nts.investcalc;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Created by Николай on 01.02.2015.
 */
public class MyDatabaseManager {

    private static volatile MyDatabaseManager instance;
    private MyDatabaseHelper databaseHelper;

    public MyDatabaseManager() { }

    public static MyDatabaseManager getInstance() {
        if (instance == null) {
            synchronized (MyDatabaseManager.class) {
                if (instance == null) {
                    instance = new MyDatabaseManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context, MyDatabaseHelper.class);
        }
    }

    public void release() {
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
        }
    }

    public MyDatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

}
