package com.zeeba.Activity.Dashboard;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.zeeba.Activity.BaseActivity;
import com.zeeba.Activity.MainActivity;
import com.zeeba.R;
import com.zeeba.Webservice.WebService;
import com.zeeba.databinding.ActivityThankYouBinding;
import com.zeeba.databinding.ActivityThankYouFbFrndResultBinding;
import com.zeeba.utils.Constants;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;
import com.zeeba.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ThankYouFBFrndResultActivity extends BaseActivity {
    /**
     * Binding all views of xml
     */
    ActivityThankYouFbFrndResultBinding mBinding;
    Context mContext;
    /**
     * DEclare variable
     */
    String percentage;
    String name;
    String is_topTen;
    /**
     * Declare views for dynamic generation
     */
    ImageView[] img_up;
    TextView[] txt_percentage;
    FrameLayout[] frameLayouts_img;
    Handler handler;
    String share_msg;
    ShareDialog shareDialog;
    int is_fb = 0;
    //CallbackManager callbackManager;

    Intent mIntent;

    int pointCount = 0;
    String fb_app_user_id = "";
    String invite_id_noti = "";
    Dialog listDialog;

    //for final name of the thankyou screen..
    String from_name = "";
    String to_name = "";
    String from_ansname = "";
    String to_ansname = "";
    String from_ans_url = "";
    String to_ans_url = "";
    String q_name = "";
    String from_result = "";
    String to_result = "";
    String challengeStatus = "";
    String sub_cate_name = "";
    String request_from_fbid = "";
    String request_to_fbid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Binding all views
         */
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_thank_you_fb_frnd_result);
        mIntent = getIntent();
        mContext = ThankYouFBFrndResultActivity.this;
        // callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
    /*    shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.e("success","success");
            }

            @Override
            public void onCancel() {
                Log.e("success","cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("success","error");
            } });*/

        /**
         * Set typeface of text
         */
        mBinding.txtSelect.setTypeface(FontCustom.setFontcontent(ThankYouFBFrndResultActivity.this));
        mBinding.txtThankYou.setTypeface(FontCustom.setFontcontent(ThankYouFBFrndResultActivity.this));
        //mBinding.txtYourscore.setTypeface(FontCustom.setFontcontent(ThankYouFBFrndResultActivity.this));
        mBinding.txtTop.setTypeface(FontCustom.setFontcontent(ThankYouFBFrndResultActivity.this));
        mBinding.btnshare.setTypeface(FontCustom.setFontcontent(ThankYouFBFrndResultActivity.this));
        mBinding.btnstartOver.setTypeface(FontCustom.setFontcontent(ThankYouFBFrndResultActivity.this));
        //mBinding.txtLeaderboard.setTypeface(FontCustom.setFont(ThankYouFBFrndResultActivity.this));

        if (!TextUtils.isEmpty(mIntent.getStringExtra("pointCount"))) {
            pointCount = Integer.parseInt(mIntent.getStringExtra("pointCount"));
            Log.e("ImageSelection", "thnkws  " + pointCount);
        }
        // mBinding.txtYourscore.setText("Your Score: " + pointCount);
        if (Pref.getValue(ThankYouFBFrndResultActivity.this, "selected_name", "").length() > 0) {
            //String ans_first_char = Pref.getValue(ThankYouActivity.this, "selected_name", "").substring(0, 1).toUpperCase();

            String wordStr = UppercaseFirstLetters(Pref.getValue(ThankYouFBFrndResultActivity.this, "selected_name", ""));

            int length = Pref.getValue(ThankYouFBFrndResultActivity.this, "selected_name", "").length();
            mBinding.txtSelect.setText("You selected " + wordStr + " as your " + Pref.getValue(ThankYouFBFrndResultActivity.this, "ques_name", ""));

            // mBinding.txtSelect.setText("You selected "+ans_first_char + Pref.getValue(ThankYouActivity.this, "selected_name", "").substring(1, length).toLowerCase() + " as the " + Pref.getValue(ThankYouActivity.this, "ques_name", "").toLowerCase());
        }
        Pref.setValue(ThankYouFBFrndResultActivity.this, "answer_given", "no");

        fb_app_user_id = Pref.getValue(this, Constants.PREF_USER_FB_ID, "");
        invite_id_noti = mIntent.getStringExtra("challage_id");


        /**
         *  Answer which is given in social question ,set tag to yes
         */

        if (MainActivity.socialQuestionsArrayList.size() > 0) {
            for (int i = 0; i < MainActivity.socialQuestionsArrayList.size(); i++) {
                if (MainActivity.socialQuestionsArrayList.get(i).getQ_id().equals(Pref.getValue(ThankYouFBFrndResultActivity.this, "selected_qid", ""))) {
                    MainActivity.socialQuestionsArrayList.get(i).setAnswer_given("yes");
                    break;
                }
            }
        }
        if (MainActivity.socialQuestionsArrayList_paid.size() > 0) {
            for (int i = 0; i < MainActivity.socialQuestionsArrayList_paid.size(); i++) {
                if (MainActivity.socialQuestionsArrayList_paid.get(i).getQ_id().equals(Pref.getValue(ThankYouFBFrndResultActivity.this, "selected_qid", ""))) {
                    MainActivity.socialQuestionsArrayList_paid.get(i).setAnswer_given("yes");
                    break;
                }
            }
        }
        /**
         *  Call API to send result & get top 10 selection
         */
        if (Pref.getValue(ThankYouFBFrndResultActivity.this, "invite_id", "").equals("")) {
            if (cd.isConnectingToInternet()) {
                Log.e("Facebook222", "000");
                new ExecuteTaskForPlayGameinviteFacebookFriend().execute(Pref.getValue(mContext, "cate_id", ""), Pref.getValue(ThankYouFBFrndResultActivity.this, "selected_qid", ""), Pref.getValue(ThankYouFBFrndResultActivity.this, Constants.PREF_USER_FB_ID, ""), Pref.getValue(ThankYouFBFrndResultActivity.this, "send_request_user_facebook_id", ""));
                // Pref.setValue(mContext, "facebook_request", "0");
            } else {
                cd.showToast(mContext, R.string.NO_INTERNET_CONNECTION);
            }
        } else {
            if (Pref.getValue(ThankYouFBFrndResultActivity.this, "view_result", "").equals("1")) {
                Log.e("Facebook222", "111");
                if (cd.isConnectingToInternet()) {
                    new ExecuteTask_result().execute(Pref.getValue(ThankYouFBFrndResultActivity.this, "token", ""), Pref.getValue(ThankYouFBFrndResultActivity.this, "selected_qid", ""), Pref.getValue(ThankYouFBFrndResultActivity.this, "selected_ans_id", ""), String.valueOf(pointCount), fb_app_user_id, Pref.getValue(getApplicationContext(), "invite_id", ""));

                } else {
                    cd.showToast(ThankYouFBFrndResultActivity.this, R.string.NO_INTERNET_CONNECTION);
                }
            } else {
                Log.e("Facebook222", "222");
                if (cd.isConnectingToInternet()) {
                    new ExecuteTask_resultForFacebook().execute(Pref.getValue(ThankYouFBFrndResultActivity.this, "token", ""), Pref.getValue(ThankYouFBFrndResultActivity.this, "invite_id", ""));

                } else {
                    cd.showToast(ThankYouFBFrndResultActivity.this, R.string.NO_INTERNET_CONNECTION);
                }
            }
        }
        /*if(Pref.getValue(ThankYouFBFrndResultActivity.this,"facebook_request","").equals("1")){
            if (cd.isConnectingToInternet()) {
                new ExecuteTaskForPlayGameinviteFacebookFriend().execute(Pref.getValue(mContext, "cate_id", ""), Pref.getValue(ThankYouFBFrndResultActivity.this, "selected_qid", ""), Pref.getValue(ThankYouFBFrndResultActivity.this, Constants.PREF_USER_FB_ID, ""), Pref.getValue(ThankYouFBFrndResultActivity.this, "send_request_user_facebook_id", ""));
                Pref.setValue(mContext, "facebook_request", "0");
            } else {
                cd.showToast(mContext, R.string.NO_INTERNET_CONNECTION);
            }
        }
        else {

            if (cd.isConnectingToInternet()) {
                new ExecuteTask_resultForFacebook().execute(Pref.getValue(ThankYouFBFrndResultActivity.this, "token", ""), invite_id_noti);

            } else {
                cd.showToast(ThankYouFBFrndResultActivity.this, R.string.NO_INTERNET_CONNECTION);
            }
        }*/


        mBinding.imgleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pref.setValue(ThankYouFBFrndResultActivity.this, "facebook_request", "0");
                Pref.setValue(ThankYouFBFrndResultActivity.this, "reload_data", "0");
                Intent resultIntent = new Intent();
                setResult(100, resultIntent);
                finish();

               // finish();
            }
        });

        mBinding.btnshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



              /*  ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("http://developers.facebook.com/android")).setQuote("gdgdfgdfgdg")
                        .build();
                shareDialog.show(linkContent);*/
                //ShareSub();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, share_msg);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

            }
        });
        mBinding.btnstartOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pref.setValue(ThankYouFBFrndResultActivity.this, "start_over", "1");
                finish();

            }
        });


        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                if (Pref.getValue(ThankYouFBFrndResultActivity.this, "answer_given", "").equals("yes")) {
                    FacebookSdk.sdkInitialize(ThankYouFBFrndResultActivity.this);
                    AppEventsLogger.activateApp(ThankYouFBFrndResultActivity.this);
                    AppEventsLogger logger = AppEventsLogger.newLogger(ThankYouFBFrndResultActivity.this);
                    Bundle bundle = new Bundle();
                    bundle.putString("category_name", Pref.getValue(ThankYouFBFrndResultActivity.this, "cate_name", ""));
                    logger.logEvent("Opinion added", bundle);
                    AppEventsLogger.deactivateApp(ThankYouFBFrndResultActivity.this);
                    Pref.setValue(ThankYouFBFrndResultActivity.this, "answer_given", "no");
                }
            }
        };

        handler.postDelayed(r, 2000);


    }

    public String UppercaseFirstLetters(String str) {
        boolean prevWasWhiteSp = true;
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isLetter(chars[i])) {
                if (prevWasWhiteSp) {
                    chars[i] = Character.toUpperCase(chars[i]);
                }
                prevWasWhiteSp = false;
            } else {
                prevWasWhiteSp = Character.isWhitespace(chars[i]);
            }
        }
        return new String(chars);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Pref.setValue(ThankYouFBFrndResultActivity.this, "facebook_request", "0");
        Pref.setValue(ThankYouFBFrndResultActivity.this, "reload_data", "0");
        Intent resultIntent = new Intent();
        setResult(100, resultIntent);
        finish();

    }

    private void ShareSub() {
        final Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, share_msg);
        Log.e("share_msg", "" + share_msg);

        final List<ResolveInfo> activities = getPackageManager().queryIntentActivities(i, 0);

        List<String> appNames = new ArrayList<String>();
        for (ResolveInfo info : activities) {
            appNames.add(info.loadLabel(getPackageManager()).toString());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Complete Action using...");
        builder.setItems(appNames.toArray(new CharSequence[appNames.size()]), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                ResolveInfo info = activities.get(item);
                if (info.activityInfo.packageName.equals("com.facebook.katana")) {

                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse(share_msg))
                            .build();
                    shareDialog.show(linkContent);
                    is_fb = 1;


                } else {
                   /* Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, share_msg);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);*/
                    i.setPackage(info.activityInfo.packageName);
                    startActivity(i);
                }

                // start the selected activity

            }
        });
        AlertDialog alert = builder.create();


        alert.show();

    }


    /**
     * for invite play game using facebook friend with subcategory..
     */
    class ExecuteTaskForPlayGameinviteFacebookFriend extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(mContext);
        }

        @Override
        protected String doInBackground(String... params) {

            String res = WebService.PostData(params, Constants.InviteFacebookFrndPlayingGame, WebService.INVITE_FACEBOOK_FRND_PLAYING_GAME);
            Log.e("res....", "" + res);
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                WebService.dismissProgress();
                Log.e("Subcategory", result + "--");
                final JSONObject jsonObject;
                jsonObject = new JSONObject(result);
                Log.e("Subcategory", "$$  " + result);
                if (jsonObject.getString("code").equals("200")) {

                    JSONObject dataJsonObject1 = jsonObject.optJSONObject("data");

                    Pref.setValue(getApplicationContext(), "invite_id", dataJsonObject1.optString("id"));
                    Pref.setValue(mContext, "facebook_request", "0");

                    /**
                     *  Call API to send result & get top 10 selection
                     */

                    if (cd.isConnectingToInternet()) {
                        new ExecuteTask_result().execute(Pref.getValue(ThankYouFBFrndResultActivity.this, "token", ""), Pref.getValue(ThankYouFBFrndResultActivity.this, "selected_qid", ""), Pref.getValue(ThankYouFBFrndResultActivity.this, "selected_ans_id", ""), String.valueOf(pointCount), fb_app_user_id, Pref.getValue(getApplicationContext(), "invite_id", ""));

                    } else {
                        cd.showToast(ThankYouFBFrndResultActivity.this, R.string.NO_INTERNET_CONNECTION);
                    }

                }
                else if (jsonObject.optString("code").equals("1000")) {
                    Utils.exitApplication(ThankYouFBFrndResultActivity.this);
                }
                else {
                    Toast.makeText(mContext, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ExecuteTask_result extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(ThankYouFBFrndResultActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {

            String res = WebService.PostData(params, Constants.RESULT_param, WebService.RESULT);
            Log.e("res....", "" + res);

            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                WebService.dismissProgress();
                Log.v("resultlogin", result + "--");
                final JSONObject jsonObject;
                jsonObject = new JSONObject(result);
                if (jsonObject.getString("code").equals("200")) {


                    if (cd.isConnectingToInternet()) {
                        new ExecuteTask_resultForFacebook().execute(Pref.getValue(ThankYouFBFrndResultActivity.this, "token", ""), Pref.getValue(getApplicationContext(), "invite_id", ""));

                    } else {
                        cd.showToast(ThankYouFBFrndResultActivity.this, R.string.NO_INTERNET_CONNECTION);
                    }


                }
                else if (jsonObject.optString("code").equals("1000")) {
                    Utils.exitApplication(ThankYouFBFrndResultActivity.this);
                }

                else if (jsonObject.getString("code").equals("150")) {
                    Pref.setValue(ThankYouFBFrndResultActivity.this, "cate_deleted", "yes");
                    Pref.setValue(ThankYouFBFrndResultActivity.this, "answer_given", "no");

                    Toast.makeText(ThankYouFBFrndResultActivity.this, jsonObject.getString("data") + " ", Toast.LENGTH_LONG).show();
                    finish();
                    finish();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    class ExecuteTask_resultForFacebook extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(ThankYouFBFrndResultActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {

            String res = WebService.PostData(params, Constants.RESULT_OF_FB_FRND_param, WebService.RESULT_OF_CHALLENGES_FB_FRND);
            Log.e("res....", "" + res);

            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                WebService.dismissProgress();
                Log.e("ThanksFB", result + "--");
                final JSONObject jsonObject;
                jsonObject = new JSONObject(result);
                if (jsonObject.getString("code").equals("200")) {
                    Pref.setValue(getApplicationContext(), "invite_id", "");
                    Pref.setValue(ThankYouFBFrndResultActivity.this, "answer_given", "yes");
                    Pref.setValue(ThankYouFBFrndResultActivity.this, "view_result", "0");
                    Pref.setValue(mContext, "facebook_request", "0");
                    request_from_fbid = jsonObject.optString("request_from_fbid");
                    request_to_fbid = jsonObject.optString("request_to_fbid");
                    from_name = jsonObject.optString("from_name");
                    to_name = jsonObject.optString("to_name");
                    from_ansname = jsonObject.optString("from_ansname");
                    to_ansname = jsonObject.optString("to_ansname");
                    from_ans_url = jsonObject.optString("from_ans_url");
                    to_ans_url = jsonObject.optString("to_ans_url");
                    q_name = jsonObject.optString("q_name");
                    challengeStatus = jsonObject.optString("status");
                    sub_cate_name = jsonObject.optString("sub_cate_name");
                    from_result = jsonObject.optString("from_result");
                    to_result = jsonObject.optString("to_result");

                    if (request_from_fbid.equals(Pref.getValue(ThankYouFBFrndResultActivity.this, Constants.PREF_USER_FB_ID, ""))) {
                        mBinding.tvYouUserCate.setText("You selected " + from_ansname + " as " + sub_cate_name);
                        if (challengeStatus.equals("0")) {
                            mBinding.tvOppoToUserCate.setText(to_name + " " + to_ansname + " yet to accept your challenge");
                        } else if (challengeStatus.equals("1")) {
                            mBinding.tvOppoToUserCate.setText(to_name + " " + to_ansname + " yet to complete your challenge");
                        } else {
                            mBinding.tvOppoToUserCate.setText(to_name + " selected " + to_ansname + " as " + sub_cate_name);
                        }
                        if (challengeStatus.equals("2")) {
                            if (from_result.equals(to_result)) {
                                mBinding.txtThankYou.setText("It's a Tie!");
                            } else if (Integer.parseInt(from_result) > Integer.parseInt(to_result)) {
                                mBinding.txtThankYou.setText("You won this Challenge");
                            } else if (Integer.parseInt(from_result) < Integer.parseInt(to_result)) {
                                mBinding.txtThankYou.setText(to_name + " won this Challenge");
                            }
                        }
                        mBinding.tvYouUserScore.setText("Score: " + from_result);
                        mBinding.tvOppoToUserScore.setText("Score: " + to_result);
                        Glide.with(ThankYouFBFrndResultActivity.this).load(from_ans_url).placeholder(R.mipmap.app_icon).into(mBinding.imgYouUserCate);
                        Glide.with(ThankYouFBFrndResultActivity.this).load(to_ans_url).placeholder(R.mipmap.app_icon).into(mBinding.imgOppoToUserCate);

                    }
                    else{
                        mBinding.tvYouUserCate.setText("You selected " + to_ansname + " as " + sub_cate_name);
                        if (challengeStatus.equals("0")) {
                            mBinding.tvOppoToUserCate.setText(from_name + " " + from_ansname + " yet to accept your challenge");
                        } else if (challengeStatus.equals("1")) {
                            mBinding.tvOppoToUserCate.setText(from_name + " " + from_ansname + " yet to complete your challenge");
                        } else {
                            mBinding.tvOppoToUserCate.setText(from_name + " selected " + from_ansname + " as " + sub_cate_name);
                        }
                        if (challengeStatus.equals("2")) {
                            if (from_result.equals(to_result)) {
                                mBinding.txtThankYou.setText("It's a Tie!");
                            } else if (Integer.parseInt(from_result) < Integer.parseInt(to_result)) {
                                mBinding.txtThankYou.setText("You won this Challenge");
                            } else if (Integer.parseInt(from_result) > Integer.parseInt(to_result)) {
                                mBinding.txtThankYou.setText(from_name + " won this Challenge");
                            }
                        }
                        mBinding.tvYouUserScore.setText("Score: " + to_result);
                        mBinding.tvOppoToUserScore.setText("Score: " + from_result);

                        Glide.with(ThankYouFBFrndResultActivity.this).load(from_ans_url).placeholder(R.mipmap.app_icon).into(mBinding.imgOppoToUserCate);
                        Glide.with(ThankYouFBFrndResultActivity.this).load(to_ans_url).placeholder(R.mipmap.app_icon).into(mBinding.imgYouUserCate);

                    }



                    JSONArray jsonArray = jsonObject.getJSONArray("topten");
                    // share_msg = jsonObject.getString("share_msg");

                    /**
                     *  Intialize views
                     */
                    img_up = new ImageView[jsonArray.length()];
                    txt_percentage = new TextView[jsonArray.length()];
                    frameLayouts_img = new FrameLayout[jsonArray.length()];
                    /**
                     * In one row display max 4 image
                     */

                    String ques_first_char = Pref.getValue(ThankYouFBFrndResultActivity.this, "ques_name", "").substring(0, 1).toUpperCase();
                    int length_string = Pref.getValue(ThankYouFBFrndResultActivity.this, "ques_name", "").length();

                    if (jsonArray.length() > 0) {
                        mBinding.txtTop.setText("Top " + jsonArray.length() + " " + ques_first_char + Pref.getValue(ThankYouFBFrndResultActivity.this, "ques_name", "").substring(1, length_string) + " according to the world are");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            if (i < 4) {
                                final float inPixels = getResources().getDimension(R.dimen.image_size);
                                final float inPixels_w = getResources().getDimension(R.dimen.image_width);
                                final float inPixels1 = getResources().getDimension(R.dimen.text_size);
                                final float inPixels_t_w = getResources().getDimension(R.dimen.text_width);

                                FrameLayout.LayoutParams childParam = new FrameLayout.LayoutParams((int) inPixels_w, (int) inPixels);
                                childParam.setMargins(10, 10, 10, 10);
                                childParam.gravity = Gravity.CENTER;
                                FrameLayout.LayoutParams textParam = new FrameLayout.LayoutParams((int) inPixels_t_w, (int) inPixels1);
                                textParam.setMargins(2, 1, 15, 2);
                                textParam.gravity = Gravity.BOTTOM;
                                frameLayouts_img[i] = new FrameLayout(ThankYouFBFrndResultActivity.this);
                                img_up[i] = new ImageView(ThankYouFBFrndResultActivity.this);
                                txt_percentage[i] = new TextView(ThankYouFBFrndResultActivity.this);
                                txt_percentage[i].setText(jsonArray.getJSONObject(i).getString("result") + "%");
                                txt_percentage[i].setGravity(Gravity.CENTER);
                                txt_percentage[i].setPadding(5, 5, 5, 5);
                                txt_percentage[i].setTextColor(Color.parseColor("#ffffff"));
                                txt_percentage[i].setBackgroundColor(Color.parseColor("#75000000"));
                                txt_percentage[i].setLayoutParams(textParam);
                                Glide.with(ThankYouFBFrndResultActivity.this)
                                        .load(jsonArray.getJSONObject(i).getString("img"))
                                        .into(img_up[i]);
                                frameLayouts_img[i].setLayoutParams(childParam);
                                frameLayouts_img[i].setPadding(2, 2, 2, 2);
                                img_up[i].setLayoutParams(childParam);
                                img_up[i].setPadding(3, 3, 3, 3);
                                img_up[i].setScaleType(ImageView.ScaleType.FIT_XY);
                                img_up[i].setBackgroundResource(R.drawable.img_bg);
                                frameLayouts_img[i].addView(img_up[i]);
                                frameLayouts_img[i].addView(txt_percentage[i]);
                                mBinding.layoutUp.addView(frameLayouts_img[i]);
                            } else {

                                if (i < 8) {
                                    final float inPixels = getResources().getDimension(R.dimen.image_size);
                                    final float inPixels_w = getResources().getDimension(R.dimen.image_width);
                                    final float inPixels1 = getResources().getDimension(R.dimen.text_size);
                                    final float inPixels_t_w = getResources().getDimension(R.dimen.text_width);

                                    FrameLayout.LayoutParams childParam = new FrameLayout.LayoutParams((int) inPixels_w, (int) inPixels);
                                    childParam.setMargins(10, 10, 10, 10);
                                    childParam.gravity = Gravity.CENTER;
                                    FrameLayout.LayoutParams textParam = new FrameLayout.LayoutParams((int) inPixels_t_w, (int) inPixels1);
                                    textParam.setMargins(2, 1, 15, 2);
                                    textParam.gravity = Gravity.BOTTOM;
                                    frameLayouts_img[i] = new FrameLayout(ThankYouFBFrndResultActivity.this);
                                    img_up[i] = new ImageView(ThankYouFBFrndResultActivity.this);
                                    txt_percentage[i] = new TextView(ThankYouFBFrndResultActivity.this);
                                    txt_percentage[i].setText(jsonArray.getJSONObject(i).getString("result") + "%");
                                    txt_percentage[i].setGravity(Gravity.CENTER);
                                    txt_percentage[i].setPadding(5, 5, 5, 5);
                                    txt_percentage[i].setTextColor(Color.parseColor("#ffffff"));
                                    txt_percentage[i].setBackgroundColor(Color.parseColor("#75000000"));
                                    txt_percentage[i].setLayoutParams(textParam);
                                    Glide.with(ThankYouFBFrndResultActivity.this)
                                            .load(jsonArray.getJSONObject(i).getString("img"))
                                            .into(img_up[i]);
                                    frameLayouts_img[i].setLayoutParams(childParam);
                                    frameLayouts_img[i].setPadding(2, 2, 2, 2);
                                    img_up[i].setLayoutParams(childParam);
                                    img_up[i].setPadding(3, 3, 3, 3);
                                    img_up[i].setScaleType(ImageView.ScaleType.FIT_XY);
                                    img_up[i].setBackgroundResource(R.drawable.img_bg);
                                    frameLayouts_img[i].addView(img_up[i]);
                                    frameLayouts_img[i].addView(txt_percentage[i]);
                                    mBinding.layoutBottom.addView(frameLayouts_img[i]);
                                    mBinding.layoutBottom.setVisibility(View.VISIBLE);
                                } else {
                                    final float inPixels = getResources().getDimension(R.dimen.image_size);
                                    final float inPixels_w = getResources().getDimension(R.dimen.image_width);
                                    final float inPixels1 = getResources().getDimension(R.dimen.text_size);
                                    final float inPixels_t_w = getResources().getDimension(R.dimen.text_width);

                                    FrameLayout.LayoutParams childParam = new FrameLayout.LayoutParams((int) inPixels_w, (int) inPixels);
                                    childParam.setMargins(10, 10, 10, 10);
                                    childParam.gravity = Gravity.CENTER;
                                    FrameLayout.LayoutParams textParam = new FrameLayout.LayoutParams((int) inPixels_t_w, (int) inPixels1);
                                    textParam.setMargins(2, 1, 15, 2);
                                    textParam.gravity = Gravity.BOTTOM;
                                    frameLayouts_img[i] = new FrameLayout(ThankYouFBFrndResultActivity.this);
                                    img_up[i] = new ImageView(ThankYouFBFrndResultActivity.this);
                                    txt_percentage[i] = new TextView(ThankYouFBFrndResultActivity.this);
                                    txt_percentage[i].setText(jsonArray.getJSONObject(i).getString("result") + "%");
                                    txt_percentage[i].setGravity(Gravity.CENTER);
                                    txt_percentage[i].setPadding(5, 5, 5, 5);
                                    txt_percentage[i].setTextColor(Color.parseColor("#ffffff"));
                                    txt_percentage[i].setBackgroundColor(Color.parseColor("#75000000"));
                                    txt_percentage[i].setLayoutParams(textParam);
                                    Glide.with(ThankYouFBFrndResultActivity.this)
                                            .load(jsonArray.getJSONObject(i).getString("img"))
                                            .into(img_up[i]);
                                    frameLayouts_img[i].setLayoutParams(childParam);
                                    frameLayouts_img[i].setPadding(2, 2, 2, 2);
                                    img_up[i].setLayoutParams(childParam);
                                    img_up[i].setPadding(3, 3, 3, 3);
                                    img_up[i].setScaleType(ImageView.ScaleType.FIT_XY);
                                    img_up[i].setBackgroundResource(R.drawable.img_bg);
                                    frameLayouts_img[i].addView(img_up[i]);
                                    frameLayouts_img[i].addView(txt_percentage[i]);
                                    mBinding.layoutBottomDown.addView(frameLayouts_img[i]);
                                    mBinding.layoutBottomDown.setVisibility(View.VISIBLE);
                                }
                            }

                        }
                    }


                    /**
                     * fabric analytics
                     */
                    Answers.getInstance().logCustom(new CustomEvent("Opinion Added")
                            .putCustomAttribute("Opinion added in category", "" + Pref.getValue(ThankYouFBFrndResultActivity.this, "cate_name", "")));
                }
                else if (jsonObject.optString("code").equals("1000")) {
                    Utils.exitApplication(ThankYouFBFrndResultActivity.this);
                }
                else if (jsonObject.getString("code").equals("150")) {
                    Pref.setValue(ThankYouFBFrndResultActivity.this, "cate_deleted", "yes");
                    Pref.setValue(ThankYouFBFrndResultActivity.this, "answer_given", "no");

                    Toast.makeText(ThankYouFBFrndResultActivity.this, jsonObject.getString("data") + " ", Toast.LENGTH_LONG).show();
                    finish();
                    finish();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * dialog for option of create unlinked with device funOs and smart watch
     */
    private void openEnterLeaderboardDetail(Context context, String name) {

        listDialog = new Dialog(context);
        final LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        listDialog.setContentView(R.layout.cust_leader_board_detail_dialog);
        listDialog.setCanceledOnTouchOutside(false);
        listDialog.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = listDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.copyFrom(window.getAttributes());


        listDialog.getWindow().setGravity(Gravity.CENTER);
        listDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        TextView mYourScoreTv = (TextView) listDialog.findViewById(R.id.tvYourScore);
        TextView mEnterNameHintTv = (TextView) listDialog.findViewById(R.id.tvEnterNameHint);
        final EditText mUsername = (EditText) listDialog.findViewById(R.id.edt_user_name);
        TextView mSubmitNameTv = (TextView) listDialog.findViewById(R.id.txtSubmit);
        mEnterNameHintTv.setTypeface(FontCustom.setFontcontent(ThankYouFBFrndResultActivity.this));
        mYourScoreTv.setTypeface(FontCustom.setFontcontent(ThankYouFBFrndResultActivity.this));
        mSubmitNameTv.setTypeface(FontCustom.setFontcontent(ThankYouFBFrndResultActivity.this));
        mYourScoreTv.setText("Your Score: " + pointCount);
        if (!TextUtils.isEmpty(name)) {
            mUsername.setText(name);
        }


        mSubmitNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mUsername.getText().toString().trim())) {
                    mUsername.setError("Please enter name!");
                } else {
                    if (cd.isConnectingToInternet()) {
                        new ExecuteTask_ToptenWinnerName().execute(Pref.getValue(ThankYouFBFrndResultActivity.this, "token", ""), mUsername.getText().toString().trim());

                    } else {
                        cd.showToast(ThankYouFBFrndResultActivity.this, R.string.NO_INTERNET_CONNECTION);
                    }
                    listDialog.dismiss();
                }
            }
        });


        listDialog.show();
    }


    class ExecuteTask_ToptenWinnerName extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(ThankYouFBFrndResultActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {

            String res = WebService.PostData(params, Constants.TOP_TEN_USERNAME, WebService.TOP_TEN_USERNAME);
            Log.e("res....", "" + res);

            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                WebService.dismissProgress();
                Log.e("Thankyou", result + "--");
                final JSONObject jsonObject;
                jsonObject = new JSONObject(result);
                if (jsonObject.getString("code").equals("200")) {
                    Toast.makeText(ThankYouFBFrndResultActivity.this, jsonObject.optString("msg") + " ", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ThankYouFBFrndResultActivity.this, jsonObject.optString("msg") + " ", Toast.LENGTH_LONG).show();
                    finish();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();


        //  AppEventsLogger.deactivateApp(ThankYouActivity.this);
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
