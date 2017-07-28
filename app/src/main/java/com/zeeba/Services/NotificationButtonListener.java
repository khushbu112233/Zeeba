package com.zeeba.Services;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.zeeba.Activity.Dashboard.CategoryActivity1;
import com.zeeba.Webservice.WebService;
import com.zeeba.utils.Constants;
import com.zeeba.utils.Pref;
import com.zeeba.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by aipxperts on 4/4/17.
 */
public class NotificationButtonListener extends BroadcastReceiver {

    Context mContext;
    private String TAG="NotificationButtonListener";
    PowerManager.WakeLock wl;
    @Override
    public void onReceive(Context context, Intent intent) {

        this.mContext=context;
        MyFirebaseMessagingService.cancelNotification();

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //Object flags;
        if (wl == null)
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "MATH_ALARM");
        wl.acquire();

        Bundle bundle = intent.getExtras();

        if(bundle!=null){
            if(bundle.getString("acceptreject").equals("accept")){

                String resultData=bundle.getString("dataValue");
                try {
                    JSONObject dataObject = new JSONObject(resultData);
                    Pref.setValue(getApplicationContext(), "cate_id", dataObject.optString("cat_id"));
                    Pref.setValue(getApplicationContext(), "selected_qid", dataObject.optString("sub_cat_id"));
                    Pref.setValue(getApplicationContext(),"invite_id",dataObject.optString("invite_id"));
                    Log.e(TAG,"Broadcast111 " +Pref.getValue(getApplicationContext(), "send_request_user_facebook_id", ""));
                    new ExecuteTaskForAcceptorDeny(mContext).execute(dataObject.optString("invite_id"), "accept");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            else if(bundle.getString("acceptreject").equals("reject")){

                String resultData=bundle.getString("dataValue");
                try {
                    JSONObject dataObject = new JSONObject(resultData);
                    Pref.setValue(getApplicationContext(), "cate_id", dataObject.optString("cat_id"));
                    Pref.setValue(getApplicationContext(), "selected_qid", dataObject.optString("sub_cat_id"));
                    Pref.setValue(getApplicationContext(),"invite_id",dataObject.optString("invite_id"));
                    Log.e(TAG,"Broadcast " +Pref.getValue(getApplicationContext(), "send_request_user_facebook_id", ""));
                    new ExecuteTaskForAcceptorDeny(mContext).execute(dataObject.optString("invite_id"), "reject");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{

            }
        }

    }

    public class ExecuteTaskForAcceptorDeny extends AsyncTask<String, Integer, String> {

        public ExecuteTaskForAcceptorDeny(Context context) {
            //   this.trad_no = trad_no;
            //   this.device_token_id = device_token_id;
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(getApplicationContext());
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "";
            res = WebService.PostData(params, Constants.InviteResponseByUserAcceptorDeny, WebService.INVITE_RESPONSE_BY_USER_ACCEPT_DENY);
            ;
            Log.e(TAG, "res " + res);
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            WebService.dismissProgress();
            try {
                Log.e(TAG, "result " + result.toString());
                JSONObject jsonObject = new JSONObject(result);

                if(jsonObject.optString("code").equals("200")) {

                    if(jsonObject.optString("msg").equalsIgnoreCase("Request accepted Successful")) {
                        Log.e(TAG,"%%%% " + jsonObject.optString("msg"));
                        Pref.setValue(mContext, "start_over", "0");
                        Pref.setValue(mContext,"view_result","1");
                        Pref.setValue(mContext, "facebook_request", "1");
                      Intent  intent = new Intent(mContext, CategoryActivity1.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        try {
                            if (wl != null)
                                wl.release();
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    }
                    else {
                        Log.e("Broadcase","rejected notificaiton");
                    }

                }
                else if (jsonObject.optString("code").equals("1000")) {
                    Utils.exitApplication(mContext);
                }
                else{
                    Toast.makeText(mContext, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}