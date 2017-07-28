package com.zeeba.Activity.Dashboard;

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
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ShareActionProvider;
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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.zeeba.Activity.BaseActivity;
import com.zeeba.Activity.MainActivity;
import com.zeeba.R;
import com.zeeba.Webservice.WebService;
import com.zeeba.databinding.ActivityThankYouBinding;
import com.zeeba.utils.Constant;
import com.zeeba.utils.Constants;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;
import com.zeeba.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


public class ThankYouActivity extends BaseActivity {
    /**
     * Binding all views of xml
     */
    ActivityThankYouBinding mBinding;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Binding all views
         */
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_thank_you);
        mContext = ThankYouActivity.this;
        mIntent = getIntent();

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
        mBinding.txtSelect.setTypeface(FontCustom.setFontcontent(ThankYouActivity.this));
        mBinding.txtThankYou.setTypeface(FontCustom.setFontcontent(ThankYouActivity.this));
        mBinding.txtYourscore.setTypeface(FontCustom.setFontcontent(ThankYouActivity.this));
        mBinding.txtTop.setTypeface(FontCustom.setFontcontent(ThankYouActivity.this));
        mBinding.btnshare.setTypeface(FontCustom.setFontcontent(ThankYouActivity.this));
        mBinding.btnstartOver.setTypeface(FontCustom.setFontcontent(ThankYouActivity.this));
        mBinding.txtLeaderboard.setTypeface(FontCustom.setFont(ThankYouActivity.this));

        if (!TextUtils.isEmpty(mIntent.getStringExtra("pointCount"))) {
            pointCount = Integer.parseInt(mIntent.getStringExtra("pointCount"));
            Log.e("ImageSelection", "thnkws  " + pointCount);
        }
        mBinding.txtYourscore.setText("Your Score: " + pointCount);
        if (Pref.getValue(ThankYouActivity.this, "selected_name", "").length() > 0) {
            //String ans_first_char = Pref.getValue(ThankYouActivity.this, "selected_name", "").substring(0, 1).toUpperCase();

            String wordStr = UppercaseFirstLetters(Pref.getValue(ThankYouActivity.this, "selected_name", ""));

            int length = Pref.getValue(ThankYouActivity.this, "selected_name", "").length();
            mBinding.txtSelect.setText("You selected " + wordStr + " as your " + Pref.getValue(ThankYouActivity.this, "ques_name", ""));

            // mBinding.txtSelect.setText("You selected "+ans_first_char + Pref.getValue(ThankYouActivity.this, "selected_name", "").substring(1, length).toLowerCase() + " as the " + Pref.getValue(ThankYouActivity.this, "ques_name", "").toLowerCase());
        }
        Pref.setValue(ThankYouActivity.this, "answer_given", "no");

        fb_app_user_id = Pref.getValue(this, Constants.PREF_USER_FB_ID, "");
        invite_id_noti = Pref.getValue(this, "invite_id", "");

        /**
         *  Set image of selected answer
         */
        Glide.with(ThankYouActivity.this)
                .load(Pref.getValue(ThankYouActivity.this, "selected_image", ""))
                .into(mBinding.selectedImg);

        /**
         *  Answer which is given in social question ,set tag to yes
         */

        if (MainActivity.socialQuestionsArrayList.size() > 0) {
            for (int i = 0; i < MainActivity.socialQuestionsArrayList.size(); i++) {
                if (MainActivity.socialQuestionsArrayList.get(i).getQ_id().equals(Pref.getValue(ThankYouActivity.this, "selected_qid", ""))) {
                    MainActivity.socialQuestionsArrayList.get(i).setAnswer_given("yes");
                    break;
                }
            }
        }
        if (MainActivity.socialQuestionsArrayList_paid.size() > 0) {
            for (int i = 0; i < MainActivity.socialQuestionsArrayList_paid.size(); i++) {
                if (MainActivity.socialQuestionsArrayList_paid.get(i).getQ_id().equals(Pref.getValue(ThankYouActivity.this, "selected_qid", ""))) {
                    MainActivity.socialQuestionsArrayList_paid.get(i).setAnswer_given("yes");
                    break;
                }
            }
        }

        if (Pref.getValue(mContext, "facebook_request", "").equals("1")) {

            if (cd.isConnectingToInternet()) {
                new ExecuteTaskForPlayGameinviteFacebookFriend().execute(Pref.getValue(mContext, "cate_id", ""), Pref.getValue(ThankYouActivity.this, "selected_qid", ""), Pref.getValue(mContext, Constants.PREF_USER_FB_ID, ""), Pref.getValue(mContext, "send_request_user_facebook_id", ""));
                Pref.setValue(mContext, "facebook_request", "0");
            } else {
                cd.showToast(mContext, R.string.NO_INTERNET_CONNECTION);
            }


            Log.e("Subcategory", "if");
        } else {
            Log.e("Subcategory", "else");
            /**
             *  Call API to send result & get top 10 selection
             */

            if (cd.isConnectingToInternet()) {
                new ExecuteTask_result().execute(Pref.getValue(ThankYouActivity.this, "token", ""), Pref.getValue(ThankYouActivity.this, "selected_qid", ""), Pref.getValue(ThankYouActivity.this, "selected_ans_id", ""), String.valueOf(pointCount), fb_app_user_id, Pref.getValue(getApplicationContext(), "invite_id", ""));

            } else {
                cd.showToast(ThankYouActivity.this, R.string.NO_INTERNET_CONNECTION);
            }
        }


        mBinding.txtLeaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ThankYouActivity.this, SubCategoryLeaderboardUserListActivity.class).putExtra("cat_id_leader", Pref.getValue(ThankYouActivity.this, "cate_id", "")).putExtra("sub_cat_id_leader", Pref.getValue(ThankYouActivity.this, "selected_qid", "")).putExtra("total_point", String.valueOf(pointCount)));
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
                Pref.setValue(ThankYouActivity.this, "start_over", "1");
                finish();

            }
        });


        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                if (Pref.getValue(ThankYouActivity.this, "answer_given", "").equals("yes")) {
                    FacebookSdk.sdkInitialize(ThankYouActivity.this);
                    AppEventsLogger.activateApp(ThankYouActivity.this);
                    AppEventsLogger logger = AppEventsLogger.newLogger(ThankYouActivity.this);
                    Bundle bundle = new Bundle();
                    bundle.putString("category_name", Pref.getValue(ThankYouActivity.this, "cate_name", ""));
                    logger.logEvent("Opinion added", bundle);
                    AppEventsLogger.deactivateApp(ThankYouActivity.this);
                    Pref.setValue(ThankYouActivity.this, "answer_given", "no");
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
                        new ExecuteTask_result().execute(Pref.getValue(ThankYouActivity.this, "token", ""), Pref.getValue(ThankYouActivity.this, "selected_qid", ""), Pref.getValue(ThankYouActivity.this, "selected_ans_id", ""), String.valueOf(pointCount), fb_app_user_id, Pref.getValue(getApplicationContext(), "invite_id", ""));

                    } else {
                        cd.showToast(ThankYouActivity.this, R.string.NO_INTERNET_CONNECTION);
                    }

                } else {
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
            WebService.showProgress(ThankYouActivity.this);
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
                    Pref.setValue(getApplicationContext(), "invite_id", "");
                    Pref.setValue(ThankYouActivity.this, "answer_given", "yes");

                    percentage = jsonObject.getString("percentage");
                    name = jsonObject.optString("name");
                    is_topTen = jsonObject.optString("is_topTen");

                    if (is_topTen.equals("1")) {
                        openEnterLeaderboardDetail(ThankYouActivity.this, name);
                    } else {
                        Log.e("Thanks", "You are not in leaderboard view");
                        //openEnterLeaderboardDetail(ThankYouActivity.this, name);
                    }

                    JSONArray jsonArray = jsonObject.getJSONArray("topten");
                    share_msg = jsonObject.getString("share_msg");

                    /**
                     *  Intialize views
                     */
                    img_up = new ImageView[jsonArray.length()];
                    txt_percentage = new TextView[jsonArray.length()];
                    frameLayouts_img = new FrameLayout[jsonArray.length()];
                    /**
                     * In one row display max 4 image
                     */

                    String ques_first_char = Pref.getValue(ThankYouActivity.this, "ques_name", "").substring(0, 1).toUpperCase();
                    int length_string = Pref.getValue(ThankYouActivity.this, "ques_name", "").length();

                    if (jsonArray.length() > 0) {
                        mBinding.txtTop.setText("Top " + jsonArray.length() + " " + ques_first_char + Pref.getValue(ThankYouActivity.this, "ques_name", "").substring(1, length_string) + " according to the world are");

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
                                frameLayouts_img[i] = new FrameLayout(ThankYouActivity.this);
                                img_up[i] = new ImageView(ThankYouActivity.this);
                                txt_percentage[i] = new TextView(ThankYouActivity.this);
                                txt_percentage[i].setText(jsonArray.getJSONObject(i).getString("result") + "%");
                                txt_percentage[i].setGravity(Gravity.CENTER);
                                txt_percentage[i].setPadding(5, 5, 5, 5);
                                txt_percentage[i].setTextColor(Color.parseColor("#ffffff"));
                                txt_percentage[i].setBackgroundColor(Color.parseColor("#75000000"));
                                txt_percentage[i].setLayoutParams(textParam);
                                Glide.with(ThankYouActivity.this)
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
                                    frameLayouts_img[i] = new FrameLayout(ThankYouActivity.this);
                                    img_up[i] = new ImageView(ThankYouActivity.this);
                                    txt_percentage[i] = new TextView(ThankYouActivity.this);
                                    txt_percentage[i].setText(jsonArray.getJSONObject(i).getString("result") + "%");
                                    txt_percentage[i].setGravity(Gravity.CENTER);
                                    txt_percentage[i].setPadding(5, 5, 5, 5);
                                    txt_percentage[i].setTextColor(Color.parseColor("#ffffff"));
                                    txt_percentage[i].setBackgroundColor(Color.parseColor("#75000000"));
                                    txt_percentage[i].setLayoutParams(textParam);
                                    Glide.with(ThankYouActivity.this)
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
                                    frameLayouts_img[i] = new FrameLayout(ThankYouActivity.this);
                                    img_up[i] = new ImageView(ThankYouActivity.this);
                                    txt_percentage[i] = new TextView(ThankYouActivity.this);
                                    txt_percentage[i].setText(jsonArray.getJSONObject(i).getString("result") + "%");
                                    txt_percentage[i].setGravity(Gravity.CENTER);
                                    txt_percentage[i].setPadding(5, 5, 5, 5);
                                    txt_percentage[i].setTextColor(Color.parseColor("#ffffff"));
                                    txt_percentage[i].setBackgroundColor(Color.parseColor("#75000000"));
                                    txt_percentage[i].setLayoutParams(textParam);
                                    Glide.with(ThankYouActivity.this)
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
                            .putCustomAttribute("Opinion added in category", "" + Pref.getValue(ThankYouActivity.this, "cate_name", "")));
                }
                else if (jsonObject.optString("code").equals("1000")) {
                    Utils.exitApplication(ThankYouActivity.this);
                }
                else if (jsonObject.getString("code").equals("150")) {
                    Pref.setValue(ThankYouActivity.this, "cate_deleted", "yes");
                    Pref.setValue(ThankYouActivity.this, "answer_given", "no");

                    Toast.makeText(ThankYouActivity.this, jsonObject.getString("data") + " ", Toast.LENGTH_LONG).show();
                    finish();
                    finish();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * dialog for enter leaderboard user top ten name
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
        mEnterNameHintTv.setTypeface(FontCustom.setFontcontent(ThankYouActivity.this));
        mYourScoreTv.setTypeface(FontCustom.setFontcontent(ThankYouActivity.this));
        mSubmitNameTv.setTypeface(FontCustom.setFontcontent(ThankYouActivity.this));
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
                        new ExecuteTask_ToptenWinnerName().execute(Pref.getValue(ThankYouActivity.this, "token", ""), mUsername.getText().toString().trim());

                    } else {
                        cd.showToast(ThankYouActivity.this, R.string.NO_INTERNET_CONNECTION);
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
            WebService.showProgress(ThankYouActivity.this);
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
                    Toast.makeText(ThankYouActivity.this, jsonObject.optString("msg") + " ", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ThankYouActivity.this, jsonObject.optString("msg") + " ", Toast.LENGTH_LONG).show();
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
