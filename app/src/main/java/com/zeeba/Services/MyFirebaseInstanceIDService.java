package com.zeeba.Services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.zeeba.utils.Pref;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Pref.setValue(getApplicationContext(),"refreshedToken",refreshedToken);
       // stopSelf();
        sendRegistrationToServer(refreshedToken);
        //Displaying token on logcat
        //writeToFile(refreshedToken,getApplicationContext());
        Log.e(TAG, "Refreshed token: " + refreshedToken);

    }

    private void sendRegistrationToServer(String token) {
        //You can implement this method to store the token on your server
        //Not required for current project
    }


}