package io.agora.chatdemo;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by wei on 2016/9/27.
 */
public class DemoApplication extends Application {

    private static DemoApplication instance;

    private Context applicationContext;

    public static DemoApplication getInstance() {
        return instance;
    }

    @Override
        public void onCreate() {
        MultiDex.install(this);
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        instance = this;
        applicationContext = this;

        //init demo helper
        DemoHelper.getInstance().init(applicationContext);
    }

    @Override protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}