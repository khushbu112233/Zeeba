package com.zeeba.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.databinding.DataBindingUtil;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.iid.FirebaseInstanceId;
import com.zeeba.Activity.Dashboard.DashBoardMainActivity;
import com.zeeba.Activity.Dashboard.SelectAnswerSocialActivity;
import com.zeeba.Activity.Dashboard.ThankYouFBFrndResultActivity;
import com.zeeba.Model.Category;
import com.zeeba.Model.SocialQuestions;
import com.zeeba.Model.SubCategory;
import com.zeeba.R;
import com.zeeba.Activity.Wizard.WizardActivity;
import com.zeeba.databinding.ActivityMainBinding;
import com.crashlytics.android.answers.Answers;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Locale;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends BaseActivity  implements
        TextToSpeech.OnInitListener {

    public static Realm realm;
    public static RealmConfiguration config;

    ActivityMainBinding mBinding;
    TextToSpeech tts;
    /**
     *  store social questions
     */
    public static ArrayList<SocialQuestions> socialQuestionsArrayList=new ArrayList<>();
    public static ArrayList<SubCategory> category_sub=new ArrayList<>();
    public static ArrayList<SocialQuestions> socialQuestionsArrayList_paid=new ArrayList<>();

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }
    /** Called when the activity is first created. */
    Thread splashTread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.txtMain.setTypeface(FontCustom.setFont(MainActivity.this));
        StartAnimations();
        config = new RealmConfiguration.Builder()
                .schemaVersion(1) // Must be bumped when the schema changes
                .deleteRealmIfMigrationNeeded()
                .build();

        realm= Realm.getInstance(config);
/**
 * fabric analytics
 */
        Answers.getInstance().logCustom(new CustomEvent("Zeeba Start")
                        .putCustomAttribute("Start", "Yes"));

/**
 * facebook analytics
 */
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        logger.logEvent("Zeeba Start");
        Pref.setValue(MainActivity.this,"found_paid","no");
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) {

        }
        catch (NoSuchAlgorithmException e) {

        }
        Pref.setValue(MainActivity.this,"facebook_request","0");
        Pref.setValue(MainActivity.this, "view_result", "0");
        Pref.setValue(MainActivity.this,"reload_data","1");
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Pref.setValue(getApplicationContext(), "refreshedToken", refreshedToken);
        Log.e("refresh", "" + Pref.getValue(MainActivity.this, "refreshedToken", ""));

    }
    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        mBinding.txtMain.clearAnimation();
        mBinding.txtMain.startAnimation(anim);

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 2000) {
                        sleep(100);
                        waited += 100;
                    }

                    if(Pref.getValue(MainActivity.this,"token","").equals(""))
                    {
                        Intent intent = new Intent(MainActivity.this,
                                FacebookLoginActivity.class);
                        /*Intent intent = new Intent(MainActivity.this,
                                WizardActivity.class);*/
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        finish();

                    }
                    else {
                        Intent intent = new Intent(MainActivity.this, DashBoardMainActivity.class);
                        startActivity(intent);
                       finish();
                    }


                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    finish();
                }

            }
        };
        splashTread.start();

    }
   @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        config = new RealmConfiguration.Builder()
                .schemaVersion(1) // Must be bumped when the schema changes
                .deleteRealmIfMigrationNeeded()
                .build();

        realm= Realm.getInstance(config);
      //  tts = new TextToSpeech(this, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
       // AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.ENGLISH);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {

                speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    private void speakOut() {

        tts.speak("Serious Questions Serious Fun", TextToSpeech.QUEUE_FLUSH, null);
    }
}