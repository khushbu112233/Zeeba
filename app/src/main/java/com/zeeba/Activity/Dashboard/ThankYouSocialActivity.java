package com.zeeba.Activity.Dashboard;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.zeeba.Activity.BaseActivity;
import com.zeeba.Activity.MainActivity;
import com.zeeba.R;
import com.zeeba.Webservice.WebService;
import com.zeeba.databinding.ActivityThankYouSocialBinding;
import com.zeeba.utils.Constants;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import static com.zeeba.Activity.Dashboard.SelectAnswerSocialActivity.arrayList_answer_selected;

public class ThankYouSocialActivity extends BaseActivity {

    String share_msg;
    ActivityThankYouSocialBinding mBinding;
    AnimationDrawable mAnimation;
    String user_type="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Binding all views
         */
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_thank_you_social);
        /**
         * Set typeface of text
         */
        mBinding.txtThankYou.setTypeface(FontCustom.setFontcontent(ThankYouSocialActivity.this));
        mBinding.btnshare.setTypeface(FontCustom.setFontcontent(ThankYouSocialActivity.this));
        mBinding.btnstartOver.setTypeface(FontCustom.setFontcontent(ThankYouSocialActivity.this));
        mBinding.imgMeterBar.setImageDrawable(ContextCompat.getDrawable(ThankYouSocialActivity.this, R.drawable.anim_full_speed));
        mAnimation = (AnimationDrawable) mBinding.imgMeterBar.getDrawable();
        mAnimation.start();
        /**
         * set answer jsonarray
         */
       setjson_array();
        sethelper();
        mBinding.btnshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, share_msg);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent,"rinal@gmail.com"));
            }
        });
        mBinding.btnstartOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pref.setValue(ThankYouSocialActivity.this,"start_over","1");
                finish();
            }
        });

    }

    public void setjson_array()
    {
        JSONArray jsonArray=new JSONArray();
        if(arrayList_answer_selected.size()>0) {
            for (int i = 0; i < arrayList_answer_selected.size(); i++) {

                if(arrayList_answer_selected.get(i).getCate_id().toString().equals(Pref.getValue(ThankYouSocialActivity.this,"cate_id",""))) {
                    Log.e("que", arrayList_answer_selected.get(i).getQue_id() + " " + arrayList_answer_selected.get(i).getAns_id() + " " + i);
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject();
                        jsonObject.put("q_id", arrayList_answer_selected.get(i).getQue_id().toString());
                        jsonObject.put("a_id", arrayList_answer_selected.get(i).getAns_id().toString());
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        arrayList_answer_selected.clear();
        JSONObject jsonObject_ans=new JSONObject();
        try {
            Log.v("jsonarray",jsonArray+"");
            jsonObject_ans.put("result",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String my_answer=jsonObject_ans.toString();

        try {
            JSONObject jsonObject_new=new JSONObject(my_answer);
            JSONArray jsonArray1=jsonObject_new.getJSONArray("result");
            Log.v("jsonarray",jsonArray1+"");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        /**
         *  Call API to send result & get top 10 selection
         */

        if(cd.isConnectingToInternet()) {
            new ExecuteTask_result().execute(Pref.getValue(ThankYouSocialActivity.this,"token",""),my_answer);

        }else
        {
            cd.showToast(ThankYouSocialActivity.this, R.string.NO_INTERNET_CONNECTION);
            finish();
        }


    }

    class ExecuteTask_result extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(ThankYouSocialActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {

            String res = WebService.PostData(params, Constants.RESULT_Social_param, WebService.RESULT_Social);
            Log.e("res....", "" + res);

            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                WebService.dismissProgress();
                Log.v("resultlogin",result+"--");
                final JSONObject jsonObject;
                jsonObject= new JSONObject(result);
                if(jsonObject.getString("code").equals("200")) {

                    share_msg=jsonObject.getString("share_msg");
                    user_type=jsonObject.getString("data");


                    /**
                     * fabric analytics
                     */
                    Answers.getInstance().logCustom(new CustomEvent("Opinion Added")
                            .putCustomAttribute("Opinion added in category", ""+Pref.getValue(ThankYouSocialActivity.this, "cate_name","")));
                }
                else if(jsonObject.getString("code").equals("150")) {
                    Pref.setValue(ThankYouSocialActivity.this,"cate_deleted","yes");
                    Pref.setValue(ThankYouSocialActivity.this,"answer_given","no");
                    Toast.makeText(ThankYouSocialActivity.this,jsonObject.getString("data")+" ",Toast.LENGTH_LONG).show();
                    finish();
                    finish();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void sethelper()
    {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                mBinding.txtAnswer.setVisibility(View.VISIBLE);
                mBinding.imgMeterBar.setVisibility(View.VISIBLE);

                mBinding.txtAnswer.setText(user_type);
                if(user_type.contains("low"))
                {

                    mBinding.imgMeterBar.setImageDrawable(ContextCompat.getDrawable(ThankYouSocialActivity.this, R.drawable.low));

                }
                else if(user_type.contains("moderate"))
                {

                    mBinding.imgMeterBar.setImageDrawable(ContextCompat.getDrawable(ThankYouSocialActivity.this, R.drawable.moderate));

                }
                else if(user_type.contains("high"))
                {

                    mBinding.imgMeterBar.setImageDrawable(ContextCompat.getDrawable(ThankYouSocialActivity.this, R.drawable.high));

                }
                else if(user_type.contains("extreme"))
                {

                    mBinding.imgMeterBar.setImageDrawable(ContextCompat.getDrawable(ThankYouSocialActivity.this, R.drawable.extreem));


                }

            }
        }, 4500);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }
}
