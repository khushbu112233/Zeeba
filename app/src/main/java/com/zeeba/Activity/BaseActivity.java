package com.zeeba.Activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.zeeba.R;
import com.zeeba.utils.ConnectionDetector;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;


public class BaseActivity extends Activity {


    public ConnectionDetector cd;
    AppEventsLogger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Initialize facebook
         */
        FacebookSdk.sdkInitialize(getApplicationContext());
        /**
         * Initialize fabric
         111*/
        Fabric.with(this, new Crashlytics());
        Fabric.with(this, new Answers());


        cd=new ConnectionDetector(this);

        StatusBar();

    }
    public  void StatusBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

    }

}
