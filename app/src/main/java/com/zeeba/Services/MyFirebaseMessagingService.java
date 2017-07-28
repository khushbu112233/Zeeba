package com.zeeba.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zeeba.Activity.Dashboard.CategoryActivity1;
import com.zeeba.Activity.Dashboard.DashBoardMainActivity;
import com.zeeba.Activity.Dashboard.SelectAnswerActivity_new;
import com.zeeba.Activity.Dashboard.ThankYouActivity;
import com.zeeba.Activity.Dashboard.ThankYouFBFrndResultActivity;
import com.zeeba.Activity.MainActivity;
import com.zeeba.Activity.Wizard.WizardActivity;
import com.zeeba.R;
import com.zeeba.Webservice.WebService;
import com.zeeba.utils.Constants;
import com.zeeba.utils.Pref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    int id;
    String msg = "wallet";
    String category = "";
    String alert;
    Context mContext;
    Intent intent;
    PendingIntent pendingIntent;
    private static final int NOTIFICATION_ID = 5;

    private static NotificationManager mNotificationManager = null;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        //  Log.d(TAG, "From: " + remoteMessage.getFrom());


        Random random = new Random();
        id = random.nextInt(9999 - 1000) + 1000;
        Log.e(TAG, "Notification Message Body: " + remoteMessage.getData().get("message") + " data " + remoteMessage.getData());

        //Calling method to generate notification
        // sendNotification(alert, remoteMessage.getData().get("message"), remoteMessage);
        //for signal push notification ....

        try {
            category = remoteMessage.getData().get("category");
            alert = remoteMessage.getData().get("alert");

            //if (category.equals("Invitation")) {
            sendNotification(alert, remoteMessage.getData().get("message"), remoteMessage);
            /*} else if(category.equalsIgnoreCase("Invitation Response")) {
                sendNotificationForAlreadyacceptReject(alert, remoteMessage.getData().get("message"), remoteMessage);
            }
            else{
                sendNotification(alert, remoteMessage.getData().get("message"), remoteMessage);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String alert, String messageBody, RemoteMessage remoteMessage) {

        if (category.equals("Invitation")) {
            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.row_custom_notification);
            contentView.setTextViewText(R.id.tvAccept, "Accept");
            contentView.setTextViewText(R.id.tvDeny, "Reject");
            contentView.setTextViewText(R.id.tvTitleNotification, "Zeeba");
            contentView.setTextViewText(R.id.tvmessagetoUser, alert);


            Pref.setValue(getApplicationContext(), "invite_facebook", "1");
            //this not work
            Intent intent = new Intent(this, NotificationButtonListener.class);
            intent.putExtra("acceptreject", "accept");
            intent.putExtra("dataValue", remoteMessage.getData().get("data"));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this.getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            contentView.setOnClickPendingIntent(R.id.tvAccept,
                    pendingIntent);


            Intent intent1 = new Intent(this, NotificationButtonListener.class);
            intent1.putExtra("acceptreject", "reject");
            intent1.putExtra("dataValue", remoteMessage.getData().get("data"));
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(
                    this.getApplicationContext(), 1, intent1, PendingIntent.FLAG_CANCEL_CURRENT);

            contentView.setOnClickPendingIntent(R.id.tvDeny,
                    pendingIntent1);


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.mipmap.app_icon)
                    // .setLargeIcon(albumArtBitmap)
                    .setCustomBigContentView(contentView)
                    .setAutoCancel(true);

            Notification notification = mBuilder.build();

            notification.contentView = contentView;

            // this is to return to my activity if click somwhere else in notification
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            notification.contentIntent = contentIntent;

            mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(NOTIFICATION_ID, notification);

        } else if (category.equalsIgnoreCase("Invitation Response")) {
            //Intent intent = new Intent(this, MainActivity.class);
            Intent intent = new Intent(this, DashBoardMainActivity.class);
            intent.putExtra("menuFragment", "challengerscreenFragment");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.app_icon)
                    .setContentTitle("Zeeba")
                    .setContentText(alert)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(id, notificationBuilder.build());
        } else if (category.equalsIgnoreCase("Result")) {
            String resultData = remoteMessage.getData().get("data");
            JSONObject dataObject = null;
            try {
                dataObject = new JSONObject(resultData);
                Pref.setValue(getApplicationContext(), "invite_id", dataObject.optString("invite_id"));
                Pref.setValue(getApplicationContext(), "view_result", "0");
                Intent intent = new Intent(this, ThankYouFBFrndResultActivity.class);
                intent.putExtra("challage_id", dataObject.optString("invite_id"));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_ONE_SHOT);
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.app_icon)
                        .setContentTitle("Zeeba")
                        .setContentText(alert)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(id, notificationBuilder.build());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    //This method is only generating push notification
    //It is same as we did in earlier posts
   /* private void sendNotificationForAlreadyacceptReject(String alert, String messageBody, RemoteMessage remoteMessage) {

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.row_custom_notification);
        contentView.setTextViewText(R.id.tvAccept, "Accept");
        contentView.setTextViewText(R.id.tvDeny, "Deny");
        contentView.setTextViewText(R.id.tvTitleNotification, "Zeeba");
        contentView.setTextViewText(R.id.tvmessagetoUser, alert);


        Pref.setValue(getApplicationContext(), "invite_facebook", "1");
        //this not work
        Intent intent = new Intent(this, NotificationButtonListener.class);
        intent.putExtra("acceptreject", "accept");
        intent.putExtra("dataValue",remoteMessage.getData().get("data"));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        contentView.setOnClickPendingIntent(R.id.tvAccept,
                pendingIntent);


        Intent intent1 = new Intent(this, NotificationButtonListener.class);
        intent1.putExtra("acceptreject", "reject");
        intent1.putExtra("dataValue",remoteMessage.getData().get("data"));
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(
                this.getApplicationContext(), 1, intent1, PendingIntent.FLAG_CANCEL_CURRENT);

        contentView.setOnClickPendingIntent(R.id.tvDeny,
                pendingIntent1);
        *//*Notification notification = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.app_icon)
                // .setLargeIcon(albumArtBitmap)
                .setCustomContentView(contentView)
                .setAutoCancel(true)
                .setStyle(new Notification.DecoratedCustomViewStyle())
                .build();*//*

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.app_icon)
                // .setLargeIcon(albumArtBitmap)
                .setCustomBigContentView(contentView)
                .setAutoCancel(true);

        Notification notification = mBuilder.build();

        notification.contentView = contentView;

        // this is to return to my activity if click somwhere else in notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.contentIntent = contentIntent;

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, notification);



    }*/
    public static void cancelNotification() {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(NOTIFICATION_ID);
        }
    }

}
