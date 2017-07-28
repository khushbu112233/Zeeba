package com.zeeba.Activity.Dashboard;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zeeba.Activity.BaseActivity;
import com.zeeba.Adapter.SubCategoryAdapter;
import com.zeeba.Model.AnswerImage;
import com.zeeba.Model.Category;
import com.zeeba.Model.Questions_Model;
import com.zeeba.R;
import com.zeeba.databinding.ActivitySelectAnswerBinding;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import io.realm.RealmResults;

import static com.zeeba.Activity.MainActivity.realm;

public class SelectAnswerActivity extends BaseActivity {
    /**
     * Binding all xml views
     */
    ActivitySelectAnswerBinding mBinding;
    /**
     * Decalre variable to store image url
     */
    String image_first;
    String image_second;
    String image_selected;
    /**
     * DEcalre variable to store answer id
     */
    String ans_id_image_first;
    String ans_id_image_second;
    String ans_id_image_selected;
    String name_image_first;
    String name_image_second;
    String name_image_selected;

    /**
     * DEcalre arraylist
     */
    ArrayList<AnswerImage> imageArrayList=new ArrayList<>();
    ArrayList<AnswerImage> imageArrayList_temp=new ArrayList<>();
    /**
     *  Declare variable to claculate time
     */
    private MyCountDownTimer myCountDownTimer;
    int start_timer=0;
    int counter=1;
    /**
     * Intialize variable to get next image position.
     */
    int next_pos=1;

    /**
     *
     * Declare bitmap
     */

    Bitmap bmp_first;
    Bitmap bmp_second;

    /**
     * set flag to display expiration message
     * @param savedInstanceState
     */
    int display_expiration_msg=0;

    /**
     * set counter when image load one by one.
     * @param savedInstanceState
     */
    int image_count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Binding xml views
         */
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_select_answer);
        /**
         * set typeface of textview
         */
        mBinding.txtSelect.setTypeface(FontCustom.setFontBold(SelectAnswerActivity.this));
        mBinding.pickOneTxt.setTypeface(FontCustom.setFontBold(SelectAnswerActivity.this));
        /**
         * set question name
         */
        mBinding.txtSelect.setText(Pref.getValue(SelectAnswerActivity.this,"ques_name",""));
        /**
         * Make both image clickable false , it should be clickable only when image load into imageview
         */
        mBinding.firstImg.setEnabled(false);
        mBinding.secondImg.setEnabled(false);
        /**
         * Countdowntimer for 2 seconds
         */
        myCountDownTimer = new MyCountDownTimer(3000, 1000);
        /**
         *  Get all answer images from local database
         */
        getdata_from_db();
        /**
         *  When first image click at that time load next image in other imageview is load ,and if no image then move to next thank you screen
         */
        mBinding.firstImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display_expiration_msg=1;
                next_pos++;

                    myCountDownTimer.cancel();

                    //mBinding.selectFirst.setVisibility(View.VISIBLE);
                    //mBinding.selectSecond.setVisibility(View.GONE);
                    if (next_pos < imageArrayList.size()) {
                        mBinding.firstImg.setEnabled(false);
                        mBinding.secondImg.setEnabled(false);
                        mBinding.secondImg.setImageBitmap(null);
                        mBinding.progressBar2.setVisibility(View.VISIBLE);
                        Animation anim = AnimationUtils.loadAnimation(SelectAnswerActivity.this, R.anim.flip);
                        anim.reset();
                        mBinding.secondImg.clearAnimation();
                        //mBinding.secondImg.startAnimation(anim);
                        Pref.setValue(SelectAnswerActivity.this, "selected_image", image_first);
                        Pref.setValue(SelectAnswerActivity.this, "selected_ans_id", ans_id_image_first);
                        Pref.setValue(SelectAnswerActivity.this,"selected_name",name_image_first);
                        image_selected=image_first;
                        ans_id_image_selected=ans_id_image_first;
                        name_image_selected=name_image_first;
                        load_next_img2();
                    } else {
                        display_expiration_msg=1;
                        Pref.setValue(SelectAnswerActivity.this, "selected_image", image_first);
                        Pref.setValue(SelectAnswerActivity.this, "selected_ans_id", ans_id_image_first);
                        Pref.setValue(SelectAnswerActivity.this,"selected_name",name_image_first);
                        image_selected=image_first;
                        ans_id_image_selected=ans_id_image_first;
                        name_image_selected=name_image_first;
                        mBinding.firstImg.setEnabled(false);
                        mBinding.secondImg.setEnabled(false);
                        mBinding.secondImg.setVisibility(View.GONE);
                        Pref.setValue(SelectAnswerActivity.this,"start_over","0");
                        finish();
                        Intent intent = new Intent(SelectAnswerActivity.this, ThankYouActivity.class);
                        startActivity(intent);
                    }

            }
        });


        /**
         *  When first image click at that time load next image in other imageview is load ,and if no image then move to next thank you screen
         */
        mBinding.secondImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display_expiration_msg=1;
                next_pos++;
                myCountDownTimer.cancel();
              //  mBinding.selectFirst.setVisibility(View.GONE);
               // mBinding.selectSecond.setVisibility(View.VISIBLE);
                    if(next_pos<imageArrayList.size()) {
                        mBinding.secondImg.setEnabled(false);
                        mBinding.firstImg.setEnabled(false);
                        mBinding.firstImg.setImageBitmap(null);
                        mBinding.progressBar1.setVisibility(View.VISIBLE);
                        Animation anim = AnimationUtils.loadAnimation(SelectAnswerActivity.this, R.anim.flip);
                        anim.reset();
                        mBinding.firstImg.clearAnimation();
                      //  mBinding.firstImg.startAnimation(anim);
                        Pref.setValue(SelectAnswerActivity.this,"selected_image",image_second);
                        Pref.setValue(SelectAnswerActivity.this,"selected_ans_id",ans_id_image_second);
                        Pref.setValue(SelectAnswerActivity.this,"selected_name",name_image_second);
                        image_selected=image_second;
                        ans_id_image_selected=ans_id_image_second;
                        name_image_selected=name_image_second;
                        load_next_img1();
                    }
                    else {
                        display_expiration_msg=1;
                        Pref.setValue(SelectAnswerActivity.this,"selected_image",image_second);
                        Pref.setValue(SelectAnswerActivity.this,"selected_ans_id",ans_id_image_second);
                        Pref.setValue(SelectAnswerActivity.this,"selected_name",name_image_second);
                        image_selected=image_second;
                        ans_id_image_selected=ans_id_image_second;
                        name_image_selected=name_image_second;
                        mBinding.firstImg.setEnabled(false);
                        mBinding.secondImg.setEnabled(false);
                        mBinding.firstImg.setVisibility(View.GONE);
                        Pref.setValue(SelectAnswerActivity.this,"start_over","0");
                        finish();
                        Intent intent=new Intent(SelectAnswerActivity.this,ThankYouActivity.class);
                        startActivity(intent);
                    }

            }
        });


        mBinding.btnquit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Pref.setValue(SelectAnswerActivity.this, "selected_image", image_selected);
                Pref.setValue(SelectAnswerActivity.this, "selected_ans_id", ans_id_image_selected);
                Pref.setValue(SelectAnswerActivity.this,"selected_name",name_image_selected);
                Log.e("image_selected",image_selected+"--");
                Log.e("ans_id_image_selected",ans_id_image_selected+"--");
                Log.e("name_image_selected",name_image_selected+"--");
                Pref.setValue(SelectAnswerActivity.this,"start_over","0");
                if(image_selected!=null && ans_id_image_selected!=null) {
                    display_expiration_msg=1;
                    Intent intent = new Intent(SelectAnswerActivity.this, ThankYouActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(SelectAnswerActivity.this,"Please select any answer!",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void getdata_from_db() {


        final RealmResults<AnswerImage> ans_img = realm.where(AnswerImage.class)
                .equalTo("category_id", Pref.getValue(SelectAnswerActivity.this, "cate_id", ""))
                .equalTo("q_id", Pref.getValue(SelectAnswerActivity.this,"selected_qid",""))
                .findAll();
        /**
         * Get data from db and store in arraylist
         */
        Log.v("ans_img", ans_img.size() + " "+Pref.getValue(SelectAnswerActivity.this, "cate_id", "")+" "+Pref.getValue(SelectAnswerActivity.this,"selected_qid",""));
        if (ans_img.size() > 0) {

            AnswerImage[] answerImages = new AnswerImage[ans_img.size()];
            for (int i = 0; i < answerImages.length; i++) {
                answerImages[i] = new AnswerImage();
                answerImages[i].setCategory_id(ans_img.get(i).getCategory_id());
                answerImages[i].setQ_id(ans_img.get(i).getQ_id());
                answerImages[i].setAns_img(ans_img.get(i).getAns_img());
                answerImages[i].setAns_id(ans_img.get(i).getAns_id());
                answerImages[i].setName(ans_img.get(i).getName());

                imageArrayList.add(answerImages[i]);
            }
            Collections.shuffle(imageArrayList);
            imageArrayList_temp.addAll(imageArrayList);
            load_image_init();
        }
        else {
            Toast.makeText(SelectAnswerActivity.this,"No data found!",Toast.LENGTH_LONG).show();
            finish();
        }

    }

    public void load_image_init()
    {
        /**
         * load first image in imageview from arraylist
         */
        bmp_first=null;
        bmp_second=null;
        image_first=imageArrayList.get(0).getAns_img();
        ans_id_image_first=imageArrayList.get(0).getAns_id();
        name_image_first=imageArrayList.get(0).getName();
        Log.v("name_image",name_image_first+" ");
        Glide.with(SelectAnswerActivity.this)
                .load(imageArrayList.get(0).getAns_img())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        Log.v("glide","onLoadStarted");
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {

                        Log.v("glide","onLoadFailed");
                        Toast.makeText(SelectAnswerActivity.this,"Sorry image is not available!",Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onResourceReady(final Bitmap icon1, GlideAnimation<? super Bitmap> glideAnimation) {
                        Log.v("glide","onResourceReady");
                        mBinding.firstImg.setEnabled(true);
                        mBinding.secondImg.setEnabled(true);
                        bmp_first=icon1;
                        image_count++;
                        if(start_timer==1)
                        {
                            if(bmp_first!=null) {
                                mBinding.progressBar1.setVisibility(View.GONE);
                                mBinding.firstImg.setImageBitmap(bmp_first);
                            }
                            if(bmp_second!=null)
                            {
                                mBinding.progressBar2.setVisibility(View.GONE);
                                mBinding.secondImg.setImageBitmap(bmp_second);
                            }
                            myCountDownTimer.start();
                        }
                        start_timer=1;

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
        /**
         * load second  image in imageview from arraylist
         */
        image_second=imageArrayList.get(1).getAns_img();
        ans_id_image_second=imageArrayList.get(1).getAns_id();
        name_image_second=imageArrayList.get(1).getName();
        Log.v("name_image",name_image_second+" ");
        Glide.with(SelectAnswerActivity.this)
                .load(imageArrayList.get(1).getAns_img())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        Log.v("glide","onLoadStarted");
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {

                        Log.v("glide","onLoadFailed");
                        Toast.makeText(SelectAnswerActivity.this,"Sorry image is not available!",Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onResourceReady(final Bitmap icon1, GlideAnimation<? super Bitmap> glideAnimation) {
                        Log.v("glide","onResourceReady");
                        mBinding.firstImg.setEnabled(true);
                        mBinding.secondImg.setEnabled(true);
                        bmp_second=icon1;
                        image_count++;
                        if(start_timer==1)
                        {
                            if(bmp_first!=null) {
                                mBinding.progressBar1.setVisibility(View.GONE);
                                mBinding.firstImg.setImageBitmap(bmp_first);
                            }
                            if(bmp_second!=null)
                            {
                                mBinding.progressBar2.setVisibility(View.GONE);
                                mBinding.secondImg.setImageBitmap(bmp_second);
                            }
                            myCountDownTimer.start();
                        }
                        start_timer=1;
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
    public void load_next_img1()
    {
        Log.v("last_selected",next_pos+" "+imageArrayList.get(next_pos).getAns_img());

        image_first=imageArrayList.get(next_pos).getAns_img();
        ans_id_image_first=imageArrayList.get(next_pos).getAns_id();
        name_image_first=imageArrayList.get(next_pos).getName();
        Log.v("name_image",name_image_first+" ");
        Glide.with(SelectAnswerActivity.this)
                .load(imageArrayList.get(next_pos).getAns_img())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        Log.v("glide","onLoadStarted");
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {

                        Log.v("glide","onLoadFailed");
                        Toast.makeText(SelectAnswerActivity.this,"Sorry image is not available!",Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onResourceReady(final Bitmap icon1, GlideAnimation<? super Bitmap> glideAnimation) {
                        Log.v("glide","onResourceReady");

                        bmp_first=icon1;
                        image_count++;
                        if(start_timer==1)
                        {
                            display_expiration_msg=0;
                            if(bmp_first!=null) {
                                mBinding.progressBar1.setVisibility(View.GONE);
                                mBinding.firstImg.setImageBitmap(bmp_first);
                            }
                            if(bmp_second!=null)
                            {
                                mBinding.progressBar2.setVisibility(View.GONE);
                                mBinding.secondImg.setImageBitmap(bmp_second);
                            }
                            mBinding.firstImg.setEnabled(true);
                            mBinding.secondImg.setEnabled(true);
                            myCountDownTimer.start();
                        }
                        start_timer=1;
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

    public void load_next_img2()
    {
        Log.v("last_selected",next_pos+" "+imageArrayList.get(next_pos).getAns_img());
        image_second=imageArrayList.get(next_pos).getAns_img();
        ans_id_image_second=imageArrayList.get(next_pos).getAns_id();
        name_image_second=imageArrayList.get(next_pos).getName();
        Log.v("name_image",name_image_second+" ");
        Glide.with(SelectAnswerActivity.this)
                .load(imageArrayList.get(next_pos).getAns_img())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        Log.v("glide","onLoadStarted");
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {

                        Log.v("glide","onLoadFailed");
                        Toast.makeText(SelectAnswerActivity.this,"Sorry image is not available!",Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onResourceReady(final Bitmap icon1, GlideAnimation<? super Bitmap> glideAnimation) {
                        Log.v("glide","onResourceReady");

                        bmp_second=icon1;
                        image_count++;
                        if(start_timer==1)
                        {
                            display_expiration_msg=0;
                            if(bmp_first!=null) {
                                mBinding.progressBar1.setVisibility(View.GONE);
                                mBinding.firstImg.setImageBitmap(bmp_first);
                            }
                            if(bmp_second!=null)
                            {
                                mBinding.progressBar2.setVisibility(View.GONE);
                                mBinding.secondImg.setImageBitmap(bmp_second);
                            }
                            mBinding.firstImg.setEnabled(true);
                            mBinding.secondImg.setEnabled(true);
                            myCountDownTimer.start();
                        }
                        start_timer=1;

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
            if(display_expiration_msg==0 && image_count<imageArrayList.size()) {
                Toast.makeText(SelectAnswerActivity.this, "Move faster, you must select an image within 2 seconds!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        display_expiration_msg=1;
    }
}
