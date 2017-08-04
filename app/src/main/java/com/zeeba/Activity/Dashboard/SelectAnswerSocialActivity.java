package com.zeeba.Activity.Dashboard;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.zeeba.Activity.BaseActivity;
import com.zeeba.Activity.MainActivity;
import com.zeeba.Model.AnswerImage;
import com.zeeba.Model.Questions_Model;
import com.zeeba.Model.SocialAnswerCategory;
import com.zeeba.Model.SocialAnswerImage;
import com.zeeba.Model.SocialQuestions;
import com.zeeba.R;
import com.zeeba.Webservice.WebService;
import com.zeeba.databinding.ActivitySelectAnswerBinding;
import com.zeeba.databinding.ActivitySelectAnswerSocialBinding;
import com.zeeba.utils.Constants;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;
import com.zeeba.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

public class SelectAnswerSocialActivity extends BaseActivity {

    /**
     * Binding all xml views
     */
    ActivitySelectAnswerSocialBinding mBinding;
    /**
     *  Intialize flag to make all imageview clickable when all flag set to 1
     */
    int first_image_selection=0;
    int second_image_selection=0;
    int third_image_selection=0;
    int fourth_image_selection=0;
    /**
     * Decalre following variable to store image_url and answer id
     */
    String first_img_url;
    String first_img_ans_id;
    String second_img_url;
    String second_img_ans_id;
    String third_img_url;
    String third_img_ans_id;
    String fourth_img_url;
    String fourth_img_ans_id;
    /**
     * Declare variable for countdown
     */
    MyCountDownTimer myCountDownTimer;
    int counter=1;
    int is_paid=0;
    /**
     * Declare arraylst
     */
    ArrayList<SocialAnswerImage> socialAnswerImages_list=new ArrayList<>();
    ArrayList<SocialQuestions> socialQuestionsArrayList_new=new ArrayList<>();
    ArrayList<SocialQuestions> socialQuestionsArrayList_new_paid=new ArrayList<>();
    ArrayList<SocialQuestions> socialQuestionsArrayList_new_load_data=new ArrayList<>();
    ArrayList<Integer> index_image=new ArrayList<>();
    public static ArrayList<SocialAnswerCategory> arrayList_answer_selected=new ArrayList<>();

    /**
     * Declare Bitmap
     */
    Bitmap bmp_first=null;
    Bitmap bmp_second=null;
    Bitmap bmp_third=null;
    Bitmap bmp_fourth=null;

    /**
     * SEt button click flag
     * @param savedInstanceState
     */
    int btn_click=0;


    //class object
    MediaPlayer mediaPlayer;
    MediaPlayer soundMediaPlayer;
    Handler handler;
    Animation anim, anim1, anim2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         *  Binding all xml views
         */
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_select_answer_social);
        handler = new Handler();
        /**
         * Set typeface for textviews
         */
        mBinding.txtSelect.setTypeface(FontCustom.setFontBold(SelectAnswerSocialActivity.this));
        mBinding.pickOneTxt.setTypeface(FontCustom.setFontBold(SelectAnswerSocialActivity.this));
        mBinding.tvGet.setTypeface(FontCustom.setFontBold(SelectAnswerSocialActivity.this));
        mBinding.tvSet.setTypeface(FontCustom.setFontBold(SelectAnswerSocialActivity.this));
        mBinding.tvG0.setTypeface(FontCustom.setFontBold(SelectAnswerSocialActivity.this));
        /**
         *  Set all imageview clikable false intially
         */
        mBinding.firstImg.setEnabled(false);
        mBinding.secondImg.setEnabled(false);
        mBinding.thirdImg.setEnabled(false);
        mBinding.fourthImg.setEnabled(false);

        /**
         * Set default values
         */
        mBinding.firstImg.setImageBitmap(null);
        mBinding.secondImg.setImageBitmap(null);
        mBinding.thirdImg.setImageBitmap(null);
        mBinding.fourthImg.setImageBitmap(null);
        mBinding.progressBar1.setVisibility(View.VISIBLE);
        mBinding.progressBar2.setVisibility(View.VISIBLE);
        mBinding.progressBar3.setVisibility(View.VISIBLE);
        mBinding.progressBar4.setVisibility(View.VISIBLE);


        /*
            SEt index list
         */
        index_image=new ArrayList<>();
        index_image.add(0);
        index_image.add(1);
        index_image.add(2);
        index_image.add(3);
        // Collections.shuffle(index_image);

        if(Pref.getValue(SelectAnswerSocialActivity.this,"is_FirstTime","").equalsIgnoreCase("yes") && !Pref.getValue(SelectAnswerSocialActivity.this,"is_FirstTime","").isEmpty()) {
            Log.e("Social","111 " + Pref.getValue(SelectAnswerSocialActivity.this,"is_FirstTime",""));
            prepareViewForGetsetGo();
        }
        else{

            Log.e("Social","22222 " + Pref.getValue(SelectAnswerSocialActivity.this,"is_FirstTime",""));
            /**
             *  Set timer for 2 seconds
             */
            myCountDownTimer = new MyCountDownTimer(16000, 1000);

            Log.e("connection",cd.isConnectingToInternet()+"--");
            if(MainActivity.socialQuestionsArrayList.size()==0)
            {
                /**
                 * call API if array list is blank
                 */
                if(cd.isConnectingToInternet()) {
                    new ExecuteTask_category_list().execute(Pref.getValue(SelectAnswerSocialActivity.this, "token", ""),Pref.getValue(SelectAnswerSocialActivity.this, "cate_id", ""));
                }else
                {
                    cd.showToast(SelectAnswerSocialActivity.this, R.string.NO_INTERNET_CONNECTION);
                    finish();
                }
            }
            else {

                Collections.shuffle(MainActivity.socialQuestionsArrayList);
                socialQuestionsArrayList_new=new ArrayList<>();
                if(MainActivity.socialQuestionsArrayList.size()>0) {

                    /**
                     * Move all data from main arraylist to temp arraylist which is not answered still yet
                     */
                    for(int i=0;i<MainActivity.socialQuestionsArrayList.size();i++)
                    {
                        if(MainActivity.socialQuestionsArrayList.get(i).getAnswer_given().equals("no") && MainActivity.socialQuestionsArrayList.get(i).getId().equals(Pref.getValue(SelectAnswerSocialActivity.this, "cate_id", "")))
                        {
                            socialQuestionsArrayList_new.add(MainActivity.socialQuestionsArrayList.get(i));
                        }

                    }
                    /**
                     *  If all answers given by user then load data once again
                     *
                     */

                    if (socialQuestionsArrayList_new.size() > 0) {
                        loaddata();
                    } else {
                        if (cd.isConnectingToInternet()) {
                            new ExecuteTask_category_list().execute(Pref.getValue(SelectAnswerSocialActivity.this, "token", ""),Pref.getValue(SelectAnswerSocialActivity.this, "cate_id", ""));
                        } else {
                            cd.showToast(SelectAnswerSocialActivity.this, R.string.NO_INTERNET_CONNECTION);
                            finish();

                        }
                    }
                }
            }
        }


        mBinding.imgleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_click=1;
                finish();
            }
        });


        mBinding.firstImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mBinding.selectFirst.setVisibility(View.VISIBLE);
                mBinding.secondImg.setEnabled(false);
                mBinding.thirdImg.setEnabled(false);
                mBinding.fourthImg.setEnabled(false);
                Pref.setValue(SelectAnswerSocialActivity.this,"selected_image",first_img_url);
                Pref.setValue(SelectAnswerSocialActivity.this,"selected_ans_id",first_img_ans_id);
                btn_click=1;
                if(is_paid==1)
                {
                    MainActivity.socialQuestionsArrayList.clear();
                    MainActivity.socialQuestionsArrayList_paid.clear();
                    socialQuestionsArrayList_new.clear();
                    socialQuestionsArrayList_new_load_data.clear();
                    socialQuestionsArrayList_new_paid.clear();
                }
                set_answer_given_yes();
                if(cd.isConnectingToInternet()) {
                    submit_result();
                }else
                {
                    cd.showToast(SelectAnswerSocialActivity.this, R.string.NO_INTERNET_CONNECTION);
                    finish();
                }

                /*if(cd.isConnectingToInternet()) {
                    new ExecuteTask_result().execute(Pref.getValue(SelectAnswerSocialActivity.this,"token",""),Pref.getValue(SelectAnswerSocialActivity.this,"selected_qid",""),Pref.getValue(SelectAnswerSocialActivity.this,"selected_ans_id",""));

                }else
                {
                    cd.showToast(SelectAnswerSocialActivity.this, R.string.NO_INTERNET_CONNECTION);
                }*/



            }
        });
        mBinding.secondImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  mBinding.selectSecond.setVisibility(View.VISIBLE);
                mBinding.firstImg.setEnabled(false);
                mBinding.thirdImg.setEnabled(false);
                mBinding.fourthImg.setEnabled(false);
                btn_click=1;
                Pref.setValue(SelectAnswerSocialActivity.this,"selected_image",second_img_url);
                Pref.setValue(SelectAnswerSocialActivity.this,"selected_ans_id",second_img_ans_id);
                set_answer_given_yes();
                if(is_paid==1)
                {
                    MainActivity.socialQuestionsArrayList.clear();
                    MainActivity.socialQuestionsArrayList_paid.clear();
                    socialQuestionsArrayList_new.clear();
                    socialQuestionsArrayList_new_load_data.clear();
                    socialQuestionsArrayList_new_paid.clear();
                }
                if(cd.isConnectingToInternet()) {
                    submit_result();
                }else
                {
                    cd.showToast(SelectAnswerSocialActivity.this, R.string.NO_INTERNET_CONNECTION);
                    finish();
                }
                /*if(cd.isConnectingToInternet()) {
                    new ExecuteTask_result().execute(Pref.getValue(SelectAnswerSocialActivity.this,"token",""),Pref.getValue(SelectAnswerSocialActivity.this,"selected_qid",""),Pref.getValue(SelectAnswerSocialActivity.this,"selected_ans_id",""));

                }else
                {
                    cd.showToast(SelectAnswerSocialActivity.this, R.string.NO_INTERNET_CONNECTION);
                }*/
            }
        });
        mBinding.thirdImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   mBinding.selectThird.setVisibility(View.VISIBLE);
                mBinding.secondImg.setEnabled(false);
                mBinding.firstImg.setEnabled(false);
                mBinding.fourthImg.setEnabled(false);
                Pref.setValue(SelectAnswerSocialActivity.this,"selected_image",third_img_url);
                Pref.setValue(SelectAnswerSocialActivity.this,"selected_ans_id",third_img_ans_id);
                set_answer_given_yes();
                btn_click=1;
                if(is_paid==1)
                {
                    MainActivity.socialQuestionsArrayList.clear();
                    MainActivity.socialQuestionsArrayList_paid.clear();
                    socialQuestionsArrayList_new.clear();
                    socialQuestionsArrayList_new_load_data.clear();
                    socialQuestionsArrayList_new_paid.clear();
                }
                if(cd.isConnectingToInternet()) {
                    submit_result();
                }else
                {
                    cd.showToast(SelectAnswerSocialActivity.this, R.string.NO_INTERNET_CONNECTION);
                    finish();
                }
                /*if(cd.isConnectingToInternet()) {
                    new ExecuteTask_result().execute(Pref.getValue(SelectAnswerSocialActivity.this,"token",""),Pref.getValue(SelectAnswerSocialActivity.this,"selected_qid",""),Pref.getValue(SelectAnswerSocialActivity.this,"selected_ans_id",""));

                }else
                {
                    cd.showToast(SelectAnswerSocialActivity.this, R.string.NO_INTERNET_CONNECTION);
                }*/
            }
        });
        mBinding.fourthImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mBinding.selectFourth.setVisibility(View.VISIBLE);
                mBinding.secondImg.setEnabled(false);
                mBinding.thirdImg.setEnabled(false);
                mBinding.firstImg.setEnabled(false);
                Pref.setValue(SelectAnswerSocialActivity.this,"selected_image",fourth_img_url);
                Pref.setValue(SelectAnswerSocialActivity.this,"selected_ans_id",fourth_img_ans_id);
                set_answer_given_yes();
                btn_click=1;
                if(is_paid==1)
                {
                    MainActivity.socialQuestionsArrayList.clear();
                    MainActivity.socialQuestionsArrayList_paid.clear();
                    socialQuestionsArrayList_new.clear();
                    socialQuestionsArrayList_new_load_data.clear();
                    socialQuestionsArrayList_new_paid.clear();
                }
                if(cd.isConnectingToInternet()) {
                    submit_result();
                }else
                {
                    cd.showToast(SelectAnswerSocialActivity.this, R.string.NO_INTERNET_CONNECTION);
                    finish();
                }
            }
        });



    }

    private void prepareViewForGetsetGo() {
        StartAnimations();

        mediaPlayer = MediaPlayer.create(this, R.raw.get_set_go);

        mediaPlayer.start();

        handler.postDelayed(new Runnable() {


            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                /**
                 *  Set timer for 2 seconds
                 */
                myCountDownTimer = new MyCountDownTimer(16000, 1000);

                Log.e("connection",cd.isConnectingToInternet()+"--");
                if(MainActivity.socialQuestionsArrayList.size()==0)
                {
                    /**
                     * call API if array list is blank
                     */
                    if(cd.isConnectingToInternet()) {
                        new ExecuteTask_category_list().execute(Pref.getValue(SelectAnswerSocialActivity.this, "token", ""),Pref.getValue(SelectAnswerSocialActivity.this, "cate_id", ""));
                    }else
                    {
                        cd.showToast(SelectAnswerSocialActivity.this, R.string.NO_INTERNET_CONNECTION);
                        finish();
                    }
                }
                else {

                    Collections.shuffle(MainActivity.socialQuestionsArrayList);
                    socialQuestionsArrayList_new=new ArrayList<>();
                    if(MainActivity.socialQuestionsArrayList.size()>0) {

                        /**
                         * Move all data from main arraylist to temp arraylist which is not answered still yet
                         */
                        for(int i=0;i<MainActivity.socialQuestionsArrayList.size();i++)
                        {
                            if(MainActivity.socialQuestionsArrayList.get(i).getAnswer_given().equals("no") && MainActivity.socialQuestionsArrayList.get(i).getId().equals(Pref.getValue(SelectAnswerSocialActivity.this, "cate_id", "")))
                            {
                                socialQuestionsArrayList_new.add(MainActivity.socialQuestionsArrayList.get(i));
                            }

                        }
                        /**
                         *  If all answers given by user then load data once again
                         *
                         */

                        if (socialQuestionsArrayList_new.size() > 0) {
                            loaddata();
                        } else {
                            if (cd.isConnectingToInternet()) {
                                new ExecuteTask_category_list().execute(Pref.getValue(SelectAnswerSocialActivity.this, "token", ""),Pref.getValue(SelectAnswerSocialActivity.this, "cate_id", ""));
                            } else {
                                cd.showToast(SelectAnswerSocialActivity.this, R.string.NO_INTERNET_CONNECTION);
                                finish();

                            }
                        }
                    }
                }



                mediaPlayer.stop();


            }
        }, 6000);
    }

    private void StartAnimations() {

        mBinding.lnMainbottom.setVisibility(View.VISIBLE);

        statusbar();
        anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim1 = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim2 = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        anim1.reset();
        anim2.reset();


        blink();
        mBinding.rlGetsetgo.setVisibility(View.VISIBLE);


    }


    private void blink() {

        mBinding.tvGet.setVisibility(View.VISIBLE);
        mBinding.tvSet.setVisibility(View.GONE);
        mBinding.tvG0.setVisibility(View.GONE);
        mBinding.tvGet.startAnimation(anim);
        mBinding.tvSet.startAnimation(anim);

        handler.postDelayed(new Runnable() {


            @Override
            public void run() {
                blinkSet();
            }
        }, 2000);

    }

    private void blinkSet() {
        mBinding.tvGet.setVisibility(View.VISIBLE);
        mBinding.tvSet.setVisibility(View.VISIBLE);
        mBinding.tvG0.setVisibility(View.GONE);
        mBinding.tvSet.startAnimation(anim1);
        mBinding.tvG0.startAnimation(anim1);
        handler.postDelayed(new Runnable() {


            @Override
            public void run() {
                blinkGo();
            }
        }, 2000);

    }

    private void blinkGo() {
        mBinding.tvGet.setVisibility(View.VISIBLE);
        mBinding.tvSet.setVisibility(View.VISIBLE);
        mBinding.tvG0.setVisibility(View.VISIBLE);
        mBinding.tvG0.startAnimation(anim2);
        handler.postDelayed(new Runnable() {


            @Override
            public void run() {

                mBinding.lnMainbottom.setVisibility(View.GONE);
                mBinding.rlGetsetgo.setVisibility(View.GONE);
                statusbar();

            }
        }, 2000);

    }

    private void statusbar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mBinding.lnMainbottom.getVisibility() == View.GONE) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
            } else {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.mypinkWithAlpha));
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Answer", "$$$ on pause");
      //  Pref.setValue(SelectAnswerSocialActivity.this,"is_FirstTime","yes");
        handler.removeCallbacksAndMessages(null);
        stopPlaying();
        // soundMediaPlayer.stop();
    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            int progress = (int) (millisUntilFinished/1000);
            Log.v("progress",counter+"--");

            counter++;
        }
        @Override
        public void onFinish() {
            myCountDownTimer.cancel();
            counter=1;
            Log.v("progress",counter+"--finish");
            if(btn_click==0) {
              //  stopPlaying();
                soundMediaPlayer= MediaPlayer.create(SelectAnswerSocialActivity.this, R.raw.eight_opt_game_sound);
                soundMediaPlayer.start();
                Toast.makeText(SelectAnswerSocialActivity.this, "Move faster, you must select an image within 15 seconds!", Toast.LENGTH_LONG).show();
            }

            finish();


        }
    }


    public void loaddata()
    {
        socialQuestionsArrayList_new_paid.clear();
        socialQuestionsArrayList_new_load_data.clear();
        if(MainActivity.socialQuestionsArrayList_paid.size()>0)
        {
            for(int i=0;i<MainActivity.socialQuestionsArrayList_paid.size();i++)
            {
                if(MainActivity.socialQuestionsArrayList_paid.get(i).getId().equals(Pref.getValue(SelectAnswerSocialActivity.this, "cate_id", "")) && MainActivity.socialQuestionsArrayList_paid.get(i).getAnswer_given().equals("no"))
                {
                    socialQuestionsArrayList_new_paid.add(MainActivity.socialQuestionsArrayList_paid.get(i));
                    break;
                }
            }
        }

        if(socialQuestionsArrayList_new_paid.size()>0)
        {
            for(int i=0;i<socialQuestionsArrayList_new.size()-1;i++)
            {
                socialQuestionsArrayList_new_load_data.add(socialQuestionsArrayList_new.get(i));
            }
            socialQuestionsArrayList_new_load_data.add(socialQuestionsArrayList_new_paid.get(0));
            Pref.setValue(SelectAnswerSocialActivity.this,"found_paid","yes");
        }
        else {
            for(int i=0;i<socialQuestionsArrayList_new.size();i++)
            {
                socialQuestionsArrayList_new_load_data.add(socialQuestionsArrayList_new.get(i));
                Pref.setValue(SelectAnswerSocialActivity.this,"found_paid","no");
            }
        }

        /**
         * load all image of random question comes
         */

        if(socialQuestionsArrayList_new_load_data.get(0).getIs_paid_cate().equals("1"))
        {
            is_paid=1;
        }
        else {
            is_paid=0;
        }
        Pref.setValue(SelectAnswerSocialActivity.this,"ques_name",socialQuestionsArrayList_new_load_data.get(0).getQuestion());
        Pref.setValue(SelectAnswerSocialActivity.this,"selected_qid",socialQuestionsArrayList_new_load_data.get(0).getQ_id());
        mBinding.txtSelect.setText(socialQuestionsArrayList_new_load_data.get(0).getQuestion());
        if(socialQuestionsArrayList_new_load_data.get(0).getImg_url().size()>0) {

            first_img_url = socialQuestionsArrayList_new_load_data.get(0).getImg_url().get(index_image.get(0)).getAns_img();
            first_img_ans_id = socialQuestionsArrayList_new_load_data.get(0).getImg_url().get(index_image.get(0)).getAns_id();
            mBinding.txtpriority1.setText(socialQuestionsArrayList_new_load_data.get(0).getImg_url().get(index_image.get(0)).getPriority());
        }
        if(socialQuestionsArrayList_new_load_data.get(0).getImg_url().size()>1) {

            second_img_url = socialQuestionsArrayList_new_load_data.get(0).getImg_url().get(index_image.get(1)).getAns_img();
            second_img_ans_id = socialQuestionsArrayList_new_load_data.get(0).getImg_url().get(index_image.get(1)).getAns_id();
            mBinding.txtpriority2.setText(socialQuestionsArrayList_new_load_data.get(0).getImg_url().get(index_image.get(1)).getPriority());
        }
        if(socialQuestionsArrayList_new_load_data.get(0).getImg_url().size()>2) {
            third_img_url = socialQuestionsArrayList_new_load_data.get(0).getImg_url().get(index_image.get(2)).getAns_img();
            third_img_ans_id = socialQuestionsArrayList_new_load_data.get(0).getImg_url().get(index_image.get(2)).getAns_id();
            mBinding.txtpriority3.setText(socialQuestionsArrayList_new_load_data.get(0).getImg_url().get(index_image.get(2)).getPriority());
        }
        if(socialQuestionsArrayList_new_load_data.get(0).getImg_url().size()>3) {
            fourth_img_url = socialQuestionsArrayList_new_load_data.get(0).getImg_url().get(index_image.get(3)).getAns_img();
            fourth_img_ans_id = socialQuestionsArrayList_new_load_data.get(0).getImg_url().get(index_image.get(3)).getAns_id();
            mBinding.txtpriority4.setText(socialQuestionsArrayList_new_load_data.get(0).getImg_url().get(index_image.get(3)).getPriority());
        }
        Glide.with(SelectAnswerSocialActivity.this)
                .load(first_img_url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        Log.v("glide","onLoadStarted");
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {

                        Log.v("glide","onLoadFailed");
                    }

                    @Override
                    public void onResourceReady(final Bitmap icon1, GlideAnimation<? super Bitmap> glideAnimation) {
                        Log.v("glide","onResourceReady");

                        bmp_first=icon1;
                        if(second_image_selection==1 && third_image_selection==1 && fourth_image_selection==1)
                        {
                            myCountDownTimer.start();
                            mBinding.firstImg.setEnabled(true);
                            mBinding.secondImg.setEnabled(true);
                            mBinding.thirdImg.setEnabled(true);
                            mBinding.fourthImg.setEnabled(true);
                            mBinding.firstImg.setImageBitmap(bmp_first);
                            mBinding.progressBar1.setVisibility(View.GONE);
                            mBinding.secondImg.setImageBitmap(bmp_second);
                            mBinding.progressBar2.setVisibility(View.GONE);
                            mBinding.thirdImg.setImageBitmap(bmp_third);
                            mBinding.progressBar3.setVisibility(View.GONE);
                            mBinding.fourthImg.setImageBitmap(bmp_fourth);
                            mBinding.progressBar4.setVisibility(View.GONE);
                        }
                        first_image_selection=1;

                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        Log.v("glide","onLoadCleared");
                    }
                    @Override
                    public void setRequest(Request request) {
                        Log.v("glide","setRequest");
                    }

                    @Override
                    public Request getRequest() {
                        Log.v("glide","getRequest");
                        return null;
                    }

                    @Override
                    public void onStart() {
                        Log.v("glide","onStart");
                    }

                    @Override
                    public void onStop() {
                        Log.v("glide","onStop");
                    }

                    @Override
                    public void onDestroy() {
                        Log.v("glide","onDestroy");
                    }
                });
        Glide.with(SelectAnswerSocialActivity.this)
                .load(second_img_url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        Log.v("glide","onLoadStarted");
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {

                        Log.v("glide","onLoadFailed");
                    }

                    @Override
                    public void onResourceReady(final Bitmap icon1, GlideAnimation<? super Bitmap> glideAnimation) {
                        Log.v("glide","onResourceReady");

                        bmp_second=icon1;

                        if(first_image_selection==1 && third_image_selection==1 && fourth_image_selection==1)
                        {
                            myCountDownTimer.start();
                            mBinding.firstImg.setEnabled(true);
                            mBinding.secondImg.setEnabled(true);
                            mBinding.thirdImg.setEnabled(true);
                            mBinding.fourthImg.setEnabled(true);

                            mBinding.firstImg.setImageBitmap(bmp_first);
                            mBinding.progressBar1.setVisibility(View.GONE);
                            mBinding.secondImg.setImageBitmap(bmp_second);
                            mBinding.progressBar2.setVisibility(View.GONE);
                            mBinding.thirdImg.setImageBitmap(bmp_third);
                            mBinding.progressBar3.setVisibility(View.GONE);
                            mBinding.fourthImg.setImageBitmap(bmp_fourth);
                            mBinding.progressBar4.setVisibility(View.GONE);
                        }
                        second_image_selection=1;


                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        Log.v("glide","onLoadCleared");
                    }
                    @Override
                    public void setRequest(Request request) {
                        Log.v("glide","setRequest");
                    }

                    @Override
                    public Request getRequest() {
                        Log.v("glide","getRequest");
                        return null;
                    }

                    @Override
                    public void onStart() {
                        Log.v("glide","onStart");
                    }

                    @Override
                    public void onStop() {
                        Log.v("glide","onStop");
                    }

                    @Override
                    public void onDestroy() {
                        Log.v("glide","onDestroy");
                    }
                });
        Glide.with(SelectAnswerSocialActivity.this)
                .load(third_img_url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        Log.v("glide","onLoadStarted");
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {

                        Log.v("glide","onLoadFailed");
                    }

                    @Override
                    public void onResourceReady(final Bitmap icon1, GlideAnimation<? super Bitmap> glideAnimation) {
                        Log.v("glide","onResourceReady");

                        bmp_third=icon1;

                        if(first_image_selection==1 && second_image_selection==1 && fourth_image_selection==1)
                        {
                            myCountDownTimer.start();
                            mBinding.firstImg.setEnabled(true);
                            mBinding.secondImg.setEnabled(true);
                            mBinding.thirdImg.setEnabled(true);
                            mBinding.fourthImg.setEnabled(true);

                            mBinding.firstImg.setImageBitmap(bmp_first);
                            mBinding.progressBar1.setVisibility(View.GONE);
                            mBinding.secondImg.setImageBitmap(bmp_second);
                            mBinding.progressBar2.setVisibility(View.GONE);
                            mBinding.thirdImg.setImageBitmap(bmp_third);
                            mBinding.progressBar3.setVisibility(View.GONE);
                            mBinding.fourthImg.setImageBitmap(bmp_fourth);
                            mBinding.progressBar4.setVisibility(View.GONE);
                        }
                        third_image_selection=1;

                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        Log.v("glide","onLoadCleared");
                    }
                    @Override
                    public void setRequest(Request request) {
                        Log.v("glide","setRequest");
                    }

                    @Override
                    public Request getRequest() {
                        Log.v("glide","getRequest");
                        return null;
                    }

                    @Override
                    public void onStart() {
                        Log.v("glide","onStart");
                    }

                    @Override
                    public void onStop() {
                        Log.v("glide","onStop");
                    }

                    @Override
                    public void onDestroy() {
                        Log.v("glide","onDestroy");
                    }
                });
        Glide.with(SelectAnswerSocialActivity.this)
                .load(fourth_img_url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        Log.v("glide","onLoadStarted");
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {

                        Log.v("glide","onLoadFailed");
                    }
                    @Override
                    public void onResourceReady(final Bitmap icon1, GlideAnimation<? super Bitmap> glideAnimation) {
                        Log.v("glide","onResourceReady");

                        bmp_fourth=icon1;

                        if(first_image_selection==1 && third_image_selection==1 && second_image_selection==1)
                        {
                            myCountDownTimer.start();
                            mBinding.firstImg.setEnabled(true);
                            mBinding.secondImg.setEnabled(true);
                            mBinding.thirdImg.setEnabled(true);
                            mBinding.fourthImg.setEnabled(true);

                            mBinding.firstImg.setImageBitmap(bmp_first);
                            mBinding.progressBar1.setVisibility(View.GONE);
                            mBinding.secondImg.setImageBitmap(bmp_second);
                            mBinding.progressBar2.setVisibility(View.GONE);
                            mBinding.thirdImg.setImageBitmap(bmp_third);
                            mBinding.progressBar3.setVisibility(View.GONE);
                            mBinding.fourthImg.setImageBitmap(bmp_fourth);
                            mBinding.progressBar4.setVisibility(View.GONE);
                        }
                        fourth_image_selection=1;

                    }
                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        Log.v("glide","onLoadCleared");
                    }
                    @Override
                    public void setRequest(Request request) {
                        Log.v("glide","setRequest");
                    }

                    @Override
                    public Request getRequest() {
                        Log.v("glide","getRequest");
                        return null;
                    }

                    @Override
                    public void onStart() {
                        Log.v("glide","onStart");
                    }

                    @Override
                    public void onStop() {
                        Log.v("glide","onStop");
                    }

                    @Override
                    public void onDestroy() {
                        Log.v("glide","onDestroy");
                    }
                });

    }

    class ExecuteTask_category_list extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(SelectAnswerSocialActivity.this);
        }
        @Override
        protected String doInBackground(String... params) {

            String res = WebService.PostData(params, Constants.SUBCategory_param, WebService.SUB_CATEGORY);
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
                    final JSONArray json = jsonObject.getJSONArray("data");

                    MainActivity.socialQuestionsArrayList.clear();
                    MainActivity.socialQuestionsArrayList_paid.clear();
                    socialQuestionsArrayList_new.clear();
                    socialQuestionsArrayList_new_load_data.clear();
                    socialQuestionsArrayList_new_paid.clear();

                    SocialQuestions[] socialQuestionses=new SocialQuestions[json.length()];
                    for (int i = 0; i < json.length(); i++) {

                        if(json.getJSONObject(i).getString("is_paid").equals("0")) {
                            socialQuestionses[i] = new SocialQuestions();
                            socialQuestionses[i].setId(Pref.getValue(SelectAnswerSocialActivity.this, "cate_id", ""));
                            socialQuestionses[i].setQ_id(json.getJSONObject(i).getString("q_id"));
                            socialQuestionses[i].setQuestion(json.getJSONObject(i).getString("question"));
                            socialQuestionses[i].setDays(json.getJSONObject(i).getString("days"));
                            socialQuestionses[i].setAnswer_given("no");
                            socialQuestionses[i].setIs_paid_cate(json.getJSONObject(i).getString("is_paid"));

                            ArrayList<SocialAnswerImage> socialAnswerImageArrayList = new ArrayList<>();
                            if (json.getJSONObject(i).getJSONArray("answer").length() > 0) {
                                Log.e("priority",json.getJSONObject(i).getJSONArray("answer")+" ");
                                SocialAnswerImage[] answerImages = new SocialAnswerImage[json.getJSONObject(i).getJSONArray("answer").length()];
                                for (int j = 0; j < json.getJSONObject(i).getJSONArray("answer").length(); j++) {
                                    answerImages[j] = new SocialAnswerImage();
                                    answerImages[j].setQ_id(json.getJSONObject(i).getString("q_id"));
                                    answerImages[j].setAns_id(json.getJSONObject(i).getJSONArray("answer").getJSONObject(j).getString("ans_id"));
                                    answerImages[j].setAns_img(json.getJSONObject(i).getJSONArray("answer").getJSONObject(j).getString("images"));
                                    answerImages[j].setPriority(json.getJSONObject(i).getJSONArray("answer").getJSONObject(j).getString("priority"));
                                    socialAnswerImageArrayList.add(answerImages[j]);
                                }
                                socialQuestionses[i].setImg_url(socialAnswerImageArrayList);
                            }
                            MainActivity.socialQuestionsArrayList.add(socialQuestionses[i]);
                        }
                        else {
                            socialQuestionses[i] = new SocialQuestions();
                            socialQuestionses[i].setId(Pref.getValue(SelectAnswerSocialActivity.this, "cate_id", ""));
                            socialQuestionses[i].setQ_id(json.getJSONObject(i).getString("q_id"));
                            socialQuestionses[i].setQuestion(json.getJSONObject(i).getString("question"));
                            socialQuestionses[i].setDays(json.getJSONObject(i).getString("days"));
                            socialQuestionses[i].setAnswer_given("no");
                            socialQuestionses[i].setIs_paid_cate(json.getJSONObject(i).getString("is_paid"));

                            ArrayList<SocialAnswerImage> socialAnswerImageArrayList = new ArrayList<>();
                            if (json.getJSONObject(i).getJSONArray("answer").length() > 0) {
                                Log.e("priority",json.getJSONObject(i).getJSONArray("answer")+" ");
                                SocialAnswerImage[] answerImages = new SocialAnswerImage[json.getJSONObject(i).getJSONArray("answer").length()];
                                for (int j = 0; j < json.getJSONObject(i).getJSONArray("answer").length(); j++) {
                                    answerImages[j] = new SocialAnswerImage();
                                    answerImages[j].setQ_id(json.getJSONObject(i).getString("q_id"));
                                    answerImages[j].setAns_id(json.getJSONObject(i).getJSONArray("answer").getJSONObject(j).getString("ans_id"));
                                    answerImages[j].setAns_img(json.getJSONObject(i).getJSONArray("answer").getJSONObject(j).getString("images"));
                                    answerImages[j].setPriority(json.getJSONObject(i).getJSONArray("answer").getJSONObject(j).optString("priority"));

                                    socialAnswerImageArrayList.add(answerImages[j]);
                                }
                                socialQuestionses[i].setImg_url(socialAnswerImageArrayList);
                            }
                            MainActivity.socialQuestionsArrayList_paid.add(socialQuestionses[i]);
                        }

                    }
                    Collections.shuffle(MainActivity.socialQuestionsArrayList);
                    for(int i=0;i<MainActivity.socialQuestionsArrayList.size();i++)
                    {
                        if(MainActivity.socialQuestionsArrayList.get(i).getAnswer_given().equals("no")  && MainActivity.socialQuestionsArrayList.get(i).getId().equals(Pref.getValue(SelectAnswerSocialActivity.this, "cate_id", "")))
                        {
                            socialQuestionsArrayList_new.add(MainActivity.socialQuestionsArrayList.get(i));
                        }

                    }
                    loaddata();

                }
                else if(jsonObject.getString("code").equals("1000")){
                    Utils.exitApplication(SelectAnswerSocialActivity.this);
                }
                else if(jsonObject.getString("code").equals("150"))
                {
                    Pref.setValue(SelectAnswerSocialActivity.this,"cate_deleted","yes");
                    finish();
                }
                else {
                    Toast.makeText(SelectAnswerSocialActivity.this,jsonObject.getString("data"),Toast.LENGTH_LONG).show();
                    finish();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void set_answer_given_yes()
    {
        /**
         *  Answer which is given in social question ,set tag to yes
         */

        if(MainActivity.socialQuestionsArrayList.size()>0)
        {
            for(int i=0;i<MainActivity.socialQuestionsArrayList.size();i++)
            {
                if(MainActivity.socialQuestionsArrayList.get(i).getQ_id().equals(Pref.getValue(SelectAnswerSocialActivity.this,"selected_qid","")))
                {
                    MainActivity.socialQuestionsArrayList.get(i).setAnswer_given("yes");
                    MainActivity.socialQuestionsArrayList.get(i).setSelected_answer(Pref.getValue(SelectAnswerSocialActivity.this,"selected_ans_id",""));

                    break;
                }
            }
        }
        if(MainActivity.socialQuestionsArrayList_paid.size()>0)
        {
            for(int i=0;i<MainActivity.socialQuestionsArrayList_paid.size();i++)
            {
                if(MainActivity.socialQuestionsArrayList_paid.get(i).getQ_id().equals(Pref.getValue(SelectAnswerSocialActivity.this,"selected_qid","")))
                {
                    MainActivity.socialQuestionsArrayList_paid.get(i).setAnswer_given("yes");
                    MainActivity.socialQuestionsArrayList_paid.get(i).setSelected_answer(Pref.getValue(SelectAnswerSocialActivity.this,"selected_ans_id",""));

                    break;
                }
            }
        }
    }

    public void submit_result()
    {

        SocialAnswerCategory[] socialAnswerCategories=new SocialAnswerCategory[1];
        socialAnswerCategories[0]=new SocialAnswerCategory();
        socialAnswerCategories[0].setQue_id(Pref.getValue(SelectAnswerSocialActivity.this,"selected_qid",""));
        socialAnswerCategories[0].setAns_id(Pref.getValue(SelectAnswerSocialActivity.this,"selected_ans_id",""));
        socialAnswerCategories[0].setCate_id(Pref.getValue(SelectAnswerSocialActivity.this,"cate_id",""));
        arrayList_answer_selected.add(socialAnswerCategories[0]);

        socialQuestionsArrayList_new=new ArrayList<>();
        if(MainActivity.socialQuestionsArrayList.size()>0) {

            /**
             * Move all data from main arraylist to temp arraylist which is not answered still yet
             */
            for (int i = 0; i < MainActivity.socialQuestionsArrayList.size(); i++) {
                if (MainActivity.socialQuestionsArrayList.get(i).getAnswer_given().equals("no") && MainActivity.socialQuestionsArrayList.get(i).getId().equals(Pref.getValue(SelectAnswerSocialActivity.this, "cate_id", ""))) {
                    socialQuestionsArrayList_new.add(MainActivity.socialQuestionsArrayList.get(i));
                }

            }
        }

        socialQuestionsArrayList_new_paid=new ArrayList<>();
        if(MainActivity.socialQuestionsArrayList_paid.size()>0) {

            /**
             * Move all data from main arraylist to temp arraylist which is not answered still yet
             */
            for (int i = 0; i < MainActivity.socialQuestionsArrayList_paid.size(); i++) {
                if (MainActivity.socialQuestionsArrayList_paid.get(i).getAnswer_given().equals("no") && MainActivity.socialQuestionsArrayList_paid.get(i).getId().equals(Pref.getValue(SelectAnswerSocialActivity.this, "cate_id", ""))) {
                    socialQuestionsArrayList_new_paid.add(MainActivity.socialQuestionsArrayList_paid.get(i));
                }

            }
        }

        if (socialQuestionsArrayList_new.size() > 0 || socialQuestionsArrayList_new_paid.size()>0) {

            finish();
            Intent intent=new Intent(SelectAnswerSocialActivity.this,SelectAnswerSocialActivity.class);
            startActivity(intent);
            overridePendingTransition(0,0);
            Pref.setValue(SelectAnswerSocialActivity.this,"is_FirstTime","no");
            Log.e("Social","$$$$ " + Pref.getValue(SelectAnswerSocialActivity.this,"is_FirstTime",""));
        }
        else {
            finish();
            Intent intent=new Intent(SelectAnswerSocialActivity.this,ThankYouSocialActivity.class);
            startActivity(intent);
        }
    }


    class ExecuteTask_result extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(SelectAnswerSocialActivity.this);
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
                Log.v("resultlogin",result+"--");
                final JSONObject jsonObject;
                jsonObject= new JSONObject(result);
                if(jsonObject.getString("code").equals("200")) {

                    socialQuestionsArrayList_new=new ArrayList<>();
                    if(MainActivity.socialQuestionsArrayList.size()>0) {

                        /**
                         * Move all data from main arraylist to temp arraylist which is not answered still yet
                         */
                        for (int i = 0; i < MainActivity.socialQuestionsArrayList.size(); i++) {
                            if (MainActivity.socialQuestionsArrayList.get(i).getAnswer_given().equals("no") && MainActivity.socialQuestionsArrayList.get(i).getId().equals(Pref.getValue(SelectAnswerSocialActivity.this, "cate_id", ""))) {
                                socialQuestionsArrayList_new.add(MainActivity.socialQuestionsArrayList.get(i));
                            }

                        }
                    }

                    socialQuestionsArrayList_new_paid=new ArrayList<>();
                    if(MainActivity.socialQuestionsArrayList_paid.size()>0) {

                        /**
                         * Move all data from main arraylist to temp arraylist which is not answered still yet
                         */
                        for (int i = 0; i < MainActivity.socialQuestionsArrayList_paid.size(); i++) {
                            if (MainActivity.socialQuestionsArrayList_paid.get(i).getAnswer_given().equals("no") && MainActivity.socialQuestionsArrayList_paid.get(i).getId().equals(Pref.getValue(SelectAnswerSocialActivity.this, "cate_id", ""))) {
                                socialQuestionsArrayList_new_paid.add(MainActivity.socialQuestionsArrayList_paid.get(i));
                            }

                        }
                    }

                    if (socialQuestionsArrayList_new.size() > 0 || socialQuestionsArrayList_new_paid.size()>0) {
                        finish();
                        Pref.setValue(SelectAnswerSocialActivity.this,"is_FirstTime","no");
                        Intent intent=new Intent(SelectAnswerSocialActivity.this,SelectAnswerSocialActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                    }
                    else {
                        finish();
                        Intent intent=new Intent(SelectAnswerSocialActivity.this,ThankYouSocialActivity.class);
                        startActivity(intent);
                    }

                }
                else if(jsonObject.getString("code").equals("150")) {
                    Pref.setValue(SelectAnswerSocialActivity.this,"cate_deleted","yes");
                    Pref.setValue(SelectAnswerSocialActivity.this,"answer_given","no");
                    Toast.makeText(SelectAnswerSocialActivity.this,jsonObject.getString("data")+" ",Toast.LENGTH_LONG).show();
                    finish();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        btn_click=1;
    }
}