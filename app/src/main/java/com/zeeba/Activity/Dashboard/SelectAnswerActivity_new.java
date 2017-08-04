package com.zeeba.Activity.Dashboard;

import android.app.Fragment;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;
import com.zeeba.Activity.BaseActivity;
import com.zeeba.Model.AnswerImage;
import com.zeeba.R;
import com.zeeba.databinding.ActivitySelectAnswerBinding;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;

import java.util.ArrayList;
import java.util.Collections;

import io.realm.RealmResults;

import static com.zeeba.Activity.MainActivity.realm;

public class SelectAnswerActivity_new extends BaseActivity {
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
    ArrayList<AnswerImage> imageArrayList = new ArrayList<>();
    ArrayList<AnswerImage> imageArrayList_temp = new ArrayList<>();
    ArrayList<Integer> soundFileList = new ArrayList<>();
    /**
     * Declare variable to claculate time
     */
    private MyCountDownTimer myCountDownTimer;
    int start_timer = 0;
    int counter = 1;
    /**
     * Intialize variable to get next image position.
     */
    int next_pos = 1;

    /**
     * Declare bitmap
     */

    Bitmap bmp_first;
    Bitmap bmp_second;
    Fragment ffMain;
    Animation anim, anim1, anim2;
    /**
     * set flag to display expiration message
     *
     * @param savedInstanceState
     */
    int display_expiration_msg = 0;

    /**
     * set counter when image load one by one.
     *
     * @param savedInstanceState
     */
    int image_count = 0;
    int pointCount = 0;
    int soundNumber = 0;
    //class object
    MediaPlayer mediaPlayer;
    MediaPlayer soundMediaPlayer;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Binding xml views
         */
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_select_answer);
        handler = new Handler();
        // mBinding.ffMain.setBackgroundColor(Color.parseColor("#000000"));
        // mBinding.ffMain.getBackground().setAlpha(128);  // 50% transparent


        /**
         * set typeface of textview
         */
        mBinding.txtSelect.setTypeface(FontCustom.setFontBold(SelectAnswerActivity_new.this));
        mBinding.pickOneTxt.setTypeface(FontCustom.setFontBold(SelectAnswerActivity_new.this));
        mBinding.tvGet.setTypeface(FontCustom.setFontBold(SelectAnswerActivity_new.this));
        mBinding.tvSet.setTypeface(FontCustom.setFontBold(SelectAnswerActivity_new.this));
        mBinding.tvG0.setTypeface(FontCustom.setFontBold(SelectAnswerActivity_new.this));
        mBinding.tvTitlePOintCriteria.setTypeface(FontCustom.setFontBold(SelectAnswerActivity_new.this));
        mBinding.tvpointCriteria.setTypeface(FontCustom.setFont(SelectAnswerActivity_new.this));
        mBinding.tvPointCalculation.setTypeface(FontCustom.setFont(SelectAnswerActivity_new.this));
        /**
         * set question name
         */
        mBinding.txtSelect.setText(Pref.getValue(SelectAnswerActivity_new.this, "ques_name", ""));
        mBinding.tvPointCalculation.setText("Score: "+pointCount);
        /**
         * Make both image clickable false , it should be clickable only when image load into imageview
         */
        mBinding.firstImg.setEnabled(false);
        mBinding.secondImg.setEnabled(false);


        StartAnimations();
        soundFileList.add(R.raw.first_opt_game_sound);
        soundFileList.add(R.raw.first_opt_game_sound);
        soundFileList.add(R.raw.second_opt_game_sound);
        soundFileList.add(R.raw.third_opt_game_sound);
        soundFileList.add(R.raw.four_opt_game_sound);
        soundFileList.add(R.raw.fifth_opt_game_sound);
        soundFileList.add(R.raw.sixth_opt_game_sound);
        soundFileList.add(R.raw.seven_opt_game_sound);
        soundFileList.add(R.raw.eight_opt_game_sound);
        soundFileList.add(R.raw.nine_opt_game_sound);
        soundFileList.add(R.raw.ten_opt_game_sound);


        mediaPlayer = MediaPlayer.create(this, R.raw.get_set_go);

        mediaPlayer.start();


        handler.postDelayed(new Runnable() {


            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                /**
                 * Countdowntimer for 3 seconds
                 */
                myCountDownTimer = new MyCountDownTimer(16000, 1000);

                /**
                 *  Get all answer images from local database
                 */
                getdata_from_db();


                mediaPlayer.stop();


            }
        }, 6000);


        mBinding.imgleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                display_expiration_msg = 1;
                finish();
            }
        });
       /* mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });*/
        /**
         *  When first image click at that time load next image in other imageview is load ,and if no image then move to next thank you screen
         */
        mBinding.firstImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cd.isConnectingToInternet()) {
                    display_expiration_msg = 1;
                    next_pos++;

                    myCountDownTimer.cancel();

                    //mBinding.selectFirst.setVisibility(View.VISIBLE);
                    //mBinding.selectSecond.setVisibility(View.GONE);
                    if (next_pos < imageArrayList.size()) {
                        mBinding.firstImg.setEnabled(false);
                        mBinding.secondImg.setEnabled(false);
                        mBinding.firstImg.setImageBitmap(null);
                        mBinding.secondImg.setImageBitmap(null);
                        mBinding.progressBar2.setVisibility(View.VISIBLE);
                        mBinding.progressBar1.setVisibility(View.VISIBLE);

                        //mBinding.secondImg.startAnimation(anim);
                        Pref.setValue(SelectAnswerActivity_new.this, "selected_image", image_first);
                        Pref.setValue(SelectAnswerActivity_new.this, "selected_ans_id", ans_id_image_first);
                        Pref.setValue(SelectAnswerActivity_new.this, "selected_name", name_image_first);
                        image_selected = image_first;
                        ans_id_image_selected = ans_id_image_first;
                        name_image_selected = name_image_first;

                        for (int i = 0; i < imageArrayList_temp.size(); i++) {
                            if (imageArrayList_temp.get(i).getAns_id().equals(ans_id_image_second)) {
                                imageArrayList_temp.remove(i);
                                break;
                            }
                        }
                        for (int i = 0; i < imageArrayList_temp.size(); i++) {
                            if (imageArrayList_temp.get(i).getAns_id().equals(ans_id_image_first)) {
                                pointCount = pointCount + Integer.parseInt(imageArrayList_temp.get(i).getPoint());
                                mBinding.tvPointCalculation.setText("Score: "+pointCount);
                                soundNumber = Integer.parseInt(imageArrayList_temp.get(i).getSound());
                                if (soundNumber > 0) {
                                    stopPlaying();
                                    mediaPlayer = MediaPlayer.create(SelectAnswerActivity_new.this, soundFileList.get(soundNumber));
                                    mediaPlayer.start();
                                }
                                else{
                                    stopPlaying();
                                    mediaPlayer = MediaPlayer.create(SelectAnswerActivity_new.this, R.raw.second_opt_game_sound);
                                    mediaPlayer.start();
                                }
                               // Log.e("ImageSelection", "&&& " + pointCount + " sound " + soundNumber);
                                break;
                            }
                        }

                        load_next_img2();
                        if (imageArrayList.size() > next_pos + 1) {
                            next_pos++;
                            load_next_img1();
                        }
                    } else {
                        for (int i = 0; i < imageArrayList_temp.size(); i++) {
                            if (imageArrayList_temp.get(i).getAns_id().equals(ans_id_image_second)) {
                                imageArrayList_temp.remove(i);
                                break;
                            }
                        }


                        for (int i = 0; i < imageArrayList_temp.size(); i++) {
                            if (imageArrayList_temp.get(i).getAns_id().equals(ans_id_image_first)) {
                                pointCount = pointCount + Integer.parseInt(imageArrayList_temp.get(i).getPoint());
                                mBinding.tvPointCalculation.setText("Score: "+pointCount);
                              //  Log.e("ImageSelection", "222 " + pointCount + " sound " + Integer.parseInt(imageArrayList_temp.get(i).getSound()));
                                soundNumber = Integer.parseInt(imageArrayList_temp.get(i).getSound());
                                if (soundNumber > 0) {
                                    stopPlaying();
                                    mediaPlayer = MediaPlayer.create(SelectAnswerActivity_new.this, soundFileList.get(soundNumber));
                                    mediaPlayer.start();
                                }
                                else{
                                    stopPlaying();
                                    mediaPlayer = MediaPlayer.create(SelectAnswerActivity_new.this, R.raw.second_opt_game_sound);
                                    mediaPlayer.start();
                                }
                                break;
                            }
                        }

                        Log.e("else", "----first:" + imageArrayList_temp.size());
                        for (int i = 0; i < imageArrayList_temp.size(); i++) {
                            Log.e("else", "----first:" + imageArrayList_temp.get(i).getName());
                        }
                        if (imageArrayList_temp.size() > 2) {
                            imageArrayList.clear();
                            imageArrayList.addAll(imageArrayList_temp);
                            imageArrayList_temp.clear();
                            imageArrayList_temp.addAll(imageArrayList);
                            next_pos = 0;
                            load_next_img1();
                            if (imageArrayList.size() > next_pos + 1) {
                                next_pos++;
                                load_next_img2();
                            }

                        } else {
                            Log.e("imageArrayList_temp", "size:" + imageArrayList_temp.size() + "");
                            if (imageArrayList_temp.size() == 2) {
                                imageArrayList.clear();
                                imageArrayList.addAll(imageArrayList_temp);
                                imageArrayList_temp.clear();
                                imageArrayList_temp.addAll(imageArrayList);
                                next_pos = 0;
                                load_next_img1();
                                if (imageArrayList.size() > next_pos + 1) {
                                    next_pos++;
                                    load_next_img2();
                                }

                            } else {


                                display_expiration_msg = 1;
                                Pref.setValue(SelectAnswerActivity_new.this, "selected_image", image_first);
                                Pref.setValue(SelectAnswerActivity_new.this, "selected_ans_id", ans_id_image_first);
                                Pref.setValue(SelectAnswerActivity_new.this, "selected_name", name_image_first);
                                image_selected = image_first;
                                ans_id_image_selected = ans_id_image_first;
                                name_image_selected = name_image_first;
                                mBinding.firstImg.setEnabled(false);
                                mBinding.secondImg.setEnabled(false);
                                mBinding.secondImg.setVisibility(View.GONE);
                                Pref.setValue(SelectAnswerActivity_new.this, "start_over", "0");
                                handler.postDelayed(new Runnable() {


                                    @Override
                                    public void run() {
                                       if(Pref.getValue(SelectAnswerActivity_new.this, "facebook_request", "").equals("1")){
                                           finish();
                                           Intent intent = new Intent(SelectAnswerActivity_new.this, ThankYouFBFrndResultActivity.class);
                                           intent.putExtra("pointCount", "" + pointCount);
                                           startActivityForResult(intent, 100);
                                           //startActivity(intent);
                                       }
                                       else {
                                           finish();
                                           Intent intent = new Intent(SelectAnswerActivity_new.this, ThankYouActivity.class);
                                           intent.putExtra("pointCount", "" + pointCount);
                                           startActivity(intent);
                                       }

                                    }
                                }, 400);

                            }
                        }
                    }
                } else {
                    cd.showToast(getApplicationContext(), R.string.NO_INTERNET_CONNECTION);
                    finish();
                }
            }


        });


        /**
         *  When first image click at that time load next image in other imageview is load ,and if no image then move to next thank you screen
         */
        mBinding.secondImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cd.isConnectingToInternet()) {
                    display_expiration_msg = 1;
                    next_pos++;
                    myCountDownTimer.cancel();
                    //  mBinding.selectFirst.setVisibility(View.GONE);
                    // mBinding.selectSecond.setVisibility(View.VISIBLE);
                    if (next_pos < imageArrayList.size()) {
                        mBinding.secondImg.setEnabled(false);
                        mBinding.firstImg.setEnabled(false);
                        mBinding.firstImg.setImageBitmap(null);
                        mBinding.secondImg.setImageBitmap(null);
                        mBinding.progressBar1.setVisibility(View.VISIBLE);
                        mBinding.progressBar2.setVisibility(View.VISIBLE);

                        //  mBinding.firstImg.startAnimation(anim);
                        Pref.setValue(SelectAnswerActivity_new.this, "selected_image", image_second);
                        Pref.setValue(SelectAnswerActivity_new.this, "selected_ans_id", ans_id_image_second);
                        Pref.setValue(SelectAnswerActivity_new.this, "selected_name", name_image_second);
                        image_selected = image_second;
                        ans_id_image_selected = ans_id_image_second;
                        name_image_selected = name_image_second;
                        for (int i = 0; i < imageArrayList_temp.size(); i++) {
                            if (imageArrayList_temp.get(i).getAns_id().equals(ans_id_image_first)) {
                                imageArrayList_temp.remove(i);
                                break;
                            }
                        }

                        for (int i = 0; i < imageArrayList_temp.size(); i++) {
                            if (imageArrayList_temp.get(i).getAns_id().equals(ans_id_image_second)) {
                                pointCount = pointCount + Integer.parseInt(imageArrayList_temp.get(i).getPoint());
                                mBinding.tvPointCalculation.setText("Score: "+pointCount);
                               // Log.e("ImageSelection", "333 " + pointCount + " sound " + Integer.parseInt(imageArrayList_temp.get(i).getSound()));
                                soundNumber = Integer.parseInt(imageArrayList_temp.get(i).getSound());
                                if (soundNumber > 0) {
                                    stopPlaying();
                                    mediaPlayer = MediaPlayer.create(SelectAnswerActivity_new.this, soundFileList.get(soundNumber));
                                    mediaPlayer.start();
                                }
                                else{
                                    stopPlaying();
                                    mediaPlayer = MediaPlayer.create(SelectAnswerActivity_new.this, R.raw.second_opt_game_sound);
                                    mediaPlayer.start();
                                }
                                break;
                            }
                        }
                        load_next_img1();
                        if (imageArrayList.size() > next_pos + 1) {
                            next_pos++;
                            load_next_img2();
                        }
                    } else {
                        Log.e("else", "----second:" + imageArrayList_temp.size());
                        for (int i = 0; i < imageArrayList_temp.size(); i++) {
                            if (imageArrayList_temp.get(i).getAns_id().equals(ans_id_image_first)) {
                                imageArrayList_temp.remove(i);
                                break;
                            }
                        }

                        for (int i = 0; i < imageArrayList_temp.size(); i++) {
                            if (imageArrayList_temp.get(i).getAns_id().equals(ans_id_image_second)) {
                                pointCount = pointCount + Integer.parseInt(imageArrayList_temp.get(i).getPoint());
                                mBinding.tvPointCalculation.setText("Score: "+pointCount);
                               // Log.e("ImageSelection", "444 " + pointCount + " sound " + Integer.parseInt(imageArrayList_temp.get(i).getSound()));
                                soundNumber = Integer.parseInt(imageArrayList_temp.get(i).getSound());
                                if (soundNumber > 0) {
                                    stopPlaying();
                                    mediaPlayer = MediaPlayer.create(SelectAnswerActivity_new.this, soundFileList.get(soundNumber));
                                    mediaPlayer.start();
                                }
                                else{
                                    stopPlaying();
                                    mediaPlayer = MediaPlayer.create(SelectAnswerActivity_new.this, R.raw.second_opt_game_sound);
                                    mediaPlayer.start();
                                }
                                break;
                            }
                        }

                        for (int i = 0; i < imageArrayList_temp.size(); i++) {
                            Log.e("else", "----second:" + imageArrayList_temp.get(i).getName());
                        }
                        if (imageArrayList_temp.size() > 2) {
                            imageArrayList.clear();
                            imageArrayList.addAll(imageArrayList_temp);
                            imageArrayList_temp.clear();
                            imageArrayList_temp.addAll(imageArrayList);
                            next_pos = 0;
                            load_next_img1();
                            if (imageArrayList.size() > next_pos + 1) {
                                next_pos++;
                                load_next_img2();
                            }

                        } else {
                            Log.e("imageArrayList_temp", "size:" + imageArrayList_temp.size() + "");

                            if (imageArrayList_temp.size() == 2) {
                                imageArrayList.clear();
                                imageArrayList.addAll(imageArrayList_temp);
                                imageArrayList_temp.clear();
                                imageArrayList_temp.addAll(imageArrayList);
                                next_pos = 0;
                                load_next_img1();
                                if (imageArrayList.size() > next_pos + 1) {
                                    next_pos++;
                                    load_next_img2();
                                }
                            } else {

                                display_expiration_msg = 1;
                                Pref.setValue(SelectAnswerActivity_new.this, "selected_image", image_second);
                                Pref.setValue(SelectAnswerActivity_new.this, "selected_ans_id", ans_id_image_second);
                                Pref.setValue(SelectAnswerActivity_new.this, "selected_name", name_image_second);
                                image_selected = image_second;
                                ans_id_image_selected = ans_id_image_second;
                                name_image_selected = name_image_second;
                                mBinding.firstImg.setEnabled(false);
                                mBinding.secondImg.setEnabled(false);
                                mBinding.firstImg.setVisibility(View.GONE);
                                Pref.setValue(SelectAnswerActivity_new.this, "start_over", "0");

                                handler.postDelayed(new Runnable() {


                                    @Override
                                    public void run() {
                                        if(Pref.getValue(SelectAnswerActivity_new.this, "facebook_request", "").equals("1")){
                                            finish();
                                            Intent intent = new Intent(SelectAnswerActivity_new.this, ThankYouFBFrndResultActivity.class);
                                            intent.putExtra("pointCount", "" + pointCount);
                                            startActivityForResult(intent, 100);
                                           // startActivity(intent);
                                        }
                                        else {
                                            finish();
                                            Intent intent = new Intent(SelectAnswerActivity_new.this, ThankYouActivity.class);
                                            intent.putExtra("pointCount", "" + pointCount);
                                            startActivity(intent);
                                        }

                                    }
                                }, 400);


                            }
                        }
                    }
                } else {
                    cd.showToast(getApplicationContext(), R.string.NO_INTERNET_CONNECTION);
                    finish();
                }

            }
        });


        mBinding.btnquit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Pref.setValue(SelectAnswerActivity_new.this, "selected_image", image_selected);
                Pref.setValue(SelectAnswerActivity_new.this, "selected_ans_id", ans_id_image_selected);
                Pref.setValue(SelectAnswerActivity_new.this, "selected_name", name_image_selected);
                Log.e("image_selected", image_selected + "--");
                Log.e("ans_id_image_selected", ans_id_image_selected + "--");
                Log.e("name_image_selected", name_image_selected + "--");
                Pref.setValue(SelectAnswerActivity_new.this, "start_over", "0");
                if (image_selected != null && ans_id_image_selected != null) {
                    display_expiration_msg = 1;
                    Intent intent = new Intent(SelectAnswerActivity_new.this, ThankYouActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SelectAnswerActivity_new.this, "Please select any answer!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    private void StartAnimations() {

        mBinding.lnMainbottom.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(Pref.getValue(SelectAnswerActivity_new.this, "point_criteria", ""))) {
            mBinding.lnPOintCriteria.setVisibility(View.VISIBLE);
            if(Pref.getValue(SelectAnswerActivity_new.this, "point_criteria", "").trim().length()>0)
            {
                 mBinding.tvTitlePOintCriteria.setVisibility(View.VISIBLE);
            }
            else{
                mBinding.tvTitlePOintCriteria.setVisibility(View.GONE);
            }
            mBinding.tvpointCriteria.setText(Pref.getValue(SelectAnswerActivity_new.this, "point_criteria", ""));
        }
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
                mBinding.lnPOintCriteria.setVisibility(View.GONE);
                mBinding.tvTitlePOintCriteria.setVisibility(View.GONE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 100) {
            if(resultCode == RESULT_OK){
                finish();
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Answer", "$$$ on pause");
        Pref.setValue(SelectAnswerActivity_new.this, "point_criteria", "");
        handler.removeCallbacksAndMessages(null);
        stopPlaying();
        // soundMediaPlayer.stop();
    }

    public void getdata_from_db() {


        final RealmResults<AnswerImage> ans_img = realm.where(AnswerImage.class)
                .equalTo("category_id", Pref.getValue(SelectAnswerActivity_new.this, "cate_id", ""))
                .equalTo("q_id", Pref.getValue(SelectAnswerActivity_new.this, "selected_qid", ""))
                .findAll();
        /**
         * Get data from db and store in arraylist
         */
        Log.v("ans_img", ans_img.size() + " " + Pref.getValue(SelectAnswerActivity_new.this, "cate_id", "") + " " + Pref.getValue(SelectAnswerActivity_new.this, "selected_qid", ""));
        if (ans_img.size() > 1) {

            AnswerImage[] answerImages = new AnswerImage[ans_img.size()];
            for (int i = 0; i < answerImages.length; i++) {
                answerImages[i] = new AnswerImage();
                answerImages[i].setCategory_id(ans_img.get(i).getCategory_id());
                answerImages[i].setQ_id(ans_img.get(i).getQ_id());
                answerImages[i].setAns_img(ans_img.get(i).getAns_img());
                answerImages[i].setAns_id(ans_img.get(i).getAns_id());
                answerImages[i].setName(ans_img.get(i).getName());
                answerImages[i].setPriority(ans_img.get(i).getPriority());
                answerImages[i].setPoint(ans_img.get(i).getPoint());
                answerImages[i].setSound(ans_img.get(i).getSound());
                // Log.e("Soundfile","0000 " + ans_img.get(i).getSound());
                imageArrayList.add(answerImages[i]);
            }
            Collections.shuffle(imageArrayList);
            imageArrayList_temp.addAll(imageArrayList);
            load_image_init();
        } else {
            Toast.makeText(SelectAnswerActivity_new.this, "No data found!", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    public void load_image_init() {
        /**
         * load first image in imageview from arraylist
         */
        bmp_first = null;
        bmp_second = null;
        image_first = imageArrayList.get(0).getAns_img();
        ans_id_image_first = imageArrayList.get(0).getAns_id();
        name_image_first = imageArrayList.get(0).getName();
        Log.v("name_image", name_image_first + " ");
        Glide.with(getApplicationContext())
                .load(imageArrayList.get(0).getAns_img())
                .asBitmap()
                .placeholder(R.drawable.img_bg)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        Log.v("glide", "onLoadStarted");
                        mBinding.firstImg.setImageDrawable(placeholder);
                        mBinding.secondImg.setImageDrawable(placeholder);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {

                        Log.v("glide", "onLoadFailed");
                        Toast.makeText(SelectAnswerActivity_new.this, "Sorry image is not available!", Toast.LENGTH_LONG).show();
                        if(Pref.getValue(SelectAnswerActivity_new.this, "facebook_request", "").equals("1")){
                            Intent resultIntent = new Intent();
                            setResult(100, resultIntent);
                            finish();
                        }
                        else {
                            finish();
                        }
                    }

                    @Override
                    public void onResourceReady(final Bitmap icon1, GlideAnimation<? super Bitmap> glideAnimation) {
                        Log.v("glide", "onResourceReady");
                        mBinding.firstImg.setEnabled(true);
                        mBinding.secondImg.setEnabled(true);
                        bmp_first = icon1;
                        image_count++;
                        if (start_timer == 1) {
                            if (bmp_first != null) {
                                mBinding.progressBar1.setVisibility(View.GONE);
                                mBinding.firstImg.setImageBitmap(bmp_first);
                            }
                            if (bmp_second != null) {
                                mBinding.progressBar2.setVisibility(View.GONE);
                                mBinding.secondImg.setImageBitmap(bmp_second);
                            }
                            myCountDownTimer.start();
                        }
                        start_timer = 1;

                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        Log.v("glide", "onLoadCleared");
                        mBinding.firstImg.setImageDrawable(placeholder);
                        mBinding.secondImg.setImageDrawable(placeholder);
                    }

                    @Override
                    public void setRequest(Request request) {
                        Log.v("glide", "setRequest");
                        mBinding.firstImg.setImageBitmap(null);
                        mBinding.secondImg.setImageBitmap(null);
                    }

                    @Override
                    public Request getRequest() {
                        Log.v("glide", "getRequest");
                        mBinding.firstImg.setImageBitmap(null);
                        mBinding.secondImg.setImageBitmap(null);
                        return null;
                    }

                    @Override
                    public void onStart() {
                        Log.v("glide", "onStart");
                       // mBinding.firstImg.setImageBitmap(null);
                       // mBinding.secondImg.setImageBitmap(null);
                    }

                    @Override
                    public void onStop() {
                        Log.v("glide", "onStop");
                    }

                    @Override
                    public void onDestroy() {
                        Log.v("glide", "onDestroy");
                    }
                });
        /**
         * load second  image in imageview from arraylist
         */
        image_second = imageArrayList.get(1).getAns_img();
        ans_id_image_second = imageArrayList.get(1).getAns_id();
        name_image_second = imageArrayList.get(1).getName();
        Log.v("name_image", name_image_second + " ");
        Glide.with(SelectAnswerActivity_new.this)
                .load(imageArrayList.get(1).getAns_img())
                .asBitmap()
                .placeholder(R.drawable.img_bg)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        Log.v("glide", "onLoadStarted");
                        mBinding.firstImg.setImageDrawable(placeholder);
                        mBinding.secondImg.setImageDrawable(placeholder);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {

                        Log.v("glide", "onLoadFailed");
                        Toast.makeText(SelectAnswerActivity_new.this, "Sorry image is not available!", Toast.LENGTH_LONG).show();
                        if(Pref.getValue(SelectAnswerActivity_new.this, "facebook_request", "").equals("1")){
                            Intent resultIntent = new Intent();
                            setResult(100, resultIntent);
                            finish();
                        }
                        else {
                            finish();
                        }
                    }

                    @Override
                    public void onResourceReady(final Bitmap icon1, GlideAnimation<? super Bitmap> glideAnimation) {
                        Log.v("glide", "onResourceReady");
                        mBinding.firstImg.setEnabled(true);
                        mBinding.secondImg.setEnabled(true);
                        bmp_second = icon1;
                        image_count++;
                        if (start_timer == 1) {
                            if (bmp_first != null) {
                                mBinding.progressBar1.setVisibility(View.GONE);
                                mBinding.firstImg.setImageBitmap(bmp_first);
                            }
                            if (bmp_second != null) {
                                mBinding.progressBar2.setVisibility(View.GONE);
                                mBinding.secondImg.setImageBitmap(bmp_second);
                            }
                            myCountDownTimer.start();
                        }
                        start_timer = 1;
                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        Log.v("glide", "onLoadCleared");
                        mBinding.firstImg.setImageDrawable(placeholder);
                        mBinding.secondImg.setImageDrawable(placeholder);
                    }

                    @Override
                    public void setRequest(Request request) {
                        Log.v("glide", "setRequest");
                        mBinding.firstImg.setImageBitmap(null);
                        mBinding.secondImg.setImageBitmap(null);
                    }

                    @Override
                    public Request getRequest() {
                        Log.v("glide", "getRequest");
                        mBinding.firstImg.setImageBitmap(null);
                        mBinding.secondImg.setImageBitmap(null);
                        return null;
                    }

                    @Override
                    public void onStart() {
                        Log.v("glide", "onStart");
                       // mBinding.firstImg.setImageBitmap(null);
                       // mBinding.secondImg.setImageBitmap(null);
                    }

                    @Override
                    public void onStop() {
                        Log.v("glide", "onStop");
                    }

                    @Override
                    public void onDestroy() {
                        Log.v("glide", "onDestroy");
                    }
                });
    }

    public void load_next_img1() {
        Log.v("last_selected", next_pos + " " + imageArrayList.get(next_pos).getAns_img());

        image_first = imageArrayList.get(next_pos).getAns_img();
        ans_id_image_first = imageArrayList.get(next_pos).getAns_id();
        name_image_first = imageArrayList.get(next_pos).getName();
        Log.v("name_image", name_image_first + " ");
        Glide.with(SelectAnswerActivity_new.this)
                .load(imageArrayList.get(next_pos).getAns_img())
                .asBitmap()
                .placeholder(R.drawable.img_bg)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        Log.v("glide", "onLoadStarted");
                        mBinding.firstImg.setImageDrawable(placeholder);
                        mBinding.secondImg.setImageDrawable(placeholder);

                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {

                        Log.v("glide", "onLoadFailed");
                        Toast.makeText(SelectAnswerActivity_new.this, "Sorry image is not available!", Toast.LENGTH_LONG).show();
                        if(Pref.getValue(SelectAnswerActivity_new.this, "facebook_request", "").equals("1")){
                            Intent resultIntent = new Intent();
                            setResult(100, resultIntent);
                            finish();
                        }
                        else {
                            finish();
                        }
                    }

                    @Override
                    public void onResourceReady(final Bitmap icon1, GlideAnimation<? super Bitmap> glideAnimation) {
                        Log.v("glide", "onResourceReady");
                        mBinding.firstImg.setImageBitmap(null);
                        mBinding.secondImg.setImageBitmap(null);
                        bmp_first = icon1;
                        image_count++;
                        if (start_timer == 1) {
                            display_expiration_msg = 0;
                            if (bmp_first != null) {
                                mBinding.progressBar1.setVisibility(View.GONE);
                                mBinding.firstImg.setImageBitmap(bmp_first);
                            }
                            if (bmp_second != null) {
                                mBinding.progressBar2.setVisibility(View.GONE);
                                mBinding.secondImg.setImageBitmap(bmp_second);
                            }
                            mBinding.firstImg.setEnabled(true);
                            mBinding.secondImg.setEnabled(true);
                            myCountDownTimer.start();
                        }
                        start_timer = 1;
                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        Log.v("glide", "onLoadCleared");
                        mBinding.firstImg.setImageDrawable(placeholder);
                        mBinding.secondImg.setImageDrawable(placeholder);
                    }

                    @Override
                    public void setRequest(Request request) {
                        Log.v("glide", "setRequest");
                        mBinding.firstImg.setImageBitmap(null);
                        mBinding.secondImg.setImageBitmap(null);
                    }

                    @Override
                    public Request getRequest() {
                        Log.v("glide", "getRequest");
                        mBinding.firstImg.setImageBitmap(null);
                        mBinding.secondImg.setImageBitmap(null);
                        return null;
                    }

                    @Override
                    public void onStart() {
                        Log.v("glide", "onStart");
                        mBinding.firstImg.setImageBitmap(null);
                        mBinding.secondImg.setImageBitmap(null);
                    }

                    @Override
                    public void onStop() {
                        Log.v("glide", "onStop");
                    }

                    @Override
                    public void onDestroy() {
                        Log.v("glide", "onDestroy");
                    }
                });


    }

    public void load_next_img2() {
        Log.v("last_selected", next_pos + " " + imageArrayList.get(next_pos).getAns_img());
        image_second = imageArrayList.get(next_pos).getAns_img();
        ans_id_image_second = imageArrayList.get(next_pos).getAns_id();
        name_image_second = imageArrayList.get(next_pos).getName();
        Log.v("name_image", name_image_second + " ");
        Glide.with(SelectAnswerActivity_new.this)
                .load(imageArrayList.get(next_pos).getAns_img())
                .asBitmap()
                .placeholder(R.drawable.img_bg)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        Log.v("glide", "onLoadStarted");
                        mBinding.firstImg.setImageDrawable(placeholder);
                        mBinding.secondImg.setImageDrawable(placeholder);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {

                        Log.v("glide", "onLoadFailed");
                        Toast.makeText(SelectAnswerActivity_new.this, "Sorry image is not available!", Toast.LENGTH_LONG).show();
                        if(Pref.getValue(SelectAnswerActivity_new.this, "facebook_request", "").equals("1")){
                            Intent resultIntent = new Intent();
                            setResult(100, resultIntent);
                            finish();
                        }
                        else {
                            finish();
                        }
                    }

                    @Override
                    public void onResourceReady(final Bitmap icon1, GlideAnimation<? super Bitmap> glideAnimation) {
                        Log.v("glide", "onResourceReady");
                        mBinding.firstImg.setImageBitmap(null);
                        mBinding.secondImg.setImageBitmap(null);
                        bmp_second = icon1;
                        image_count++;
                        if (start_timer == 1) {
                            display_expiration_msg = 0;
                            if (bmp_first != null) {
                                mBinding.progressBar1.setVisibility(View.GONE);
                                mBinding.firstImg.setImageBitmap(bmp_first);
                            }
                            if (bmp_second != null) {
                                mBinding.progressBar2.setVisibility(View.GONE);
                                mBinding.secondImg.setImageBitmap(bmp_second);
                            }
                            mBinding.firstImg.setEnabled(true);
                            mBinding.secondImg.setEnabled(true);
                            myCountDownTimer.start();
                        }
                        start_timer = 1;

                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        Log.v("glide", "onLoadCleared");
                        mBinding.firstImg.setImageDrawable(placeholder);
                        mBinding.secondImg.setImageDrawable(placeholder);
                    }

                    @Override
                    public void setRequest(Request request) {
                        mBinding.firstImg.setImageBitmap(null);
                        mBinding.secondImg.setImageBitmap(null);
                        Log.v("glide", "setRequest");
                    }

                    @Override
                    public Request getRequest() {
                        Log.v("glide", "getRequest");
                        mBinding.firstImg.setImageBitmap(null);
                        mBinding.secondImg.setImageBitmap(null);
                        return null;
                    }

                    @Override
                    public void onStart() {
                        mBinding.firstImg.setImageBitmap(null);
                        mBinding.secondImg.setImageBitmap(null);
                        Log.v("glide", "onStart");
                    }

                    @Override
                    public void onStop() {
                        Log.v("glide", "onStop");
                    }

                    @Override
                    public void onDestroy() {
                        Log.v("glide", "onDestroy");
                    }
                });


    }

    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            int progress = (int) (millisUntilFinished / 1000);
            Log.v("progress", counter + "--");

            counter++;
        }

        @Override
        public void onFinish() {
            myCountDownTimer.cancel();
            counter = 1;
            Log.v("progress", counter + "--finish");
            if (display_expiration_msg == 0 && image_count < imageArrayList.size()) {
               // stopPlaying();
                soundMediaPlayer = MediaPlayer.create(SelectAnswerActivity_new.this, R.raw.eight_opt_game_sound);
                soundMediaPlayer.start();
                Toast.makeText(SelectAnswerActivity_new.this, "Move faster, you must select an image within 15 seconds!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        display_expiration_msg = 1;
    }
}
