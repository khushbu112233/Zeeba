package com.zeeba.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zeeba.Activity.Dashboard.CategoryActivity1;
import com.zeeba.Activity.Dashboard.DashboardFragment;
import com.zeeba.Activity.Dashboard.SelectAnswerActivity;
import com.zeeba.Activity.Dashboard.SelectAnswerActivity_new;
import com.zeeba.Activity.Dashboard.SubCategoryLeaderboardUserListActivity;
import com.zeeba.Activity.Dashboard.ThankYouActivity;
import com.zeeba.Activity.Dashboard.ThankYouFBFrndResultActivity;
import com.zeeba.Model.AnswerImage;
import com.zeeba.Model.Questions;
import com.zeeba.Model.Questions_Model;
import com.zeeba.R;
import com.zeeba.Webservice.WebService;
import com.zeeba.databinding.SubcategoryGridLayoutBinding;
import com.zeeba.utils.ConnectionDetector;
import com.zeeba.utils.Constants;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;


import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.zeeba.Activity.MainActivity.realm;

/**
 * Created by aipxperts-ubuntu-01 on 25/3/17.
 */

public class SubCategoryAdapter extends BaseAdapter {
    Context mContext;
    SubcategoryGridLayoutBinding mBinding;
    ConnectionDetector cd;
    ArrayList<Questions_Model> question_detail = new ArrayList<>();
    ArrayList<AnswerImage> answerImageArrayList = new ArrayList<>();
    int set_click = 0;
    long elapsedDays;
    long elapsedHours;
    Dialog challengeDialog;

    public SubCategoryAdapter(Context context, ArrayList<Questions_Model> question_detail) {
        this.mContext = context;
        this.question_detail = question_detail;
        cd = new ConnectionDetector(mContext);

    }

    @Override
    public int getCount() {
        return question_detail.size();
    }

    @Override
    public Object getItem(int pos) {
        return pos;
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(final int pos, View view, ViewGroup viewGroup) {


        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.subcategory_grid_layout, viewGroup, false);
        mBinding.txtTitle.setTypeface(FontCustom.setFontcontent(mContext));
        //  mBinding.txtTitle1.setTypeface(FontCustom.setFontcontent(mContext));
        mBinding.txtDaysremaining.setTypeface(FontCustom.setFontcontent(mContext));
        mBinding.txtViewLeaderboard.setTypeface(FontCustom.setFont(mContext));
        printDifference(System.currentTimeMillis(), Long.parseLong(question_detail.get(pos).getExpire_date()));
        mBinding.txtTitle.setText(question_detail.get(pos).getQuestion());

       /* if(elapsedDays>1) {
            if(elapsedHours>12) {
                mBinding.txtDaysremaining.setText("Expires in " + (elapsedDays+1) + " days");
            }
            else {
                mBinding.txtDaysremaining.setText("Expires in " + (elapsedDays) + " days");
            }
        }
        else if(elapsedDays==1) {
            if(elapsedHours>12) {
                mBinding.txtDaysremaining.setText("Expires in " + (elapsedDays+1) + " days");
            }
            else {
                mBinding.txtDaysremaining.setText("Expires in " + (elapsedDays) + " days");
            }
        }
        else if(elapsedDays==0) {
            if(elapsedHours>12) {
                mBinding.txtDaysremaining.setText("Expires in " + (elapsedDays+1) + " days");
            }

        }*/

        if (question_detail.get(pos).getDays().equals("1")) {
            mBinding.txtDaysremaining.setText("Expires in " + question_detail.get(pos).getDays() + " day");
        } else {
            mBinding.txtDaysremaining.setText("Expires in " + question_detail.get(pos).getDays() + " days");
        }

        if (!question_detail.get(pos).getPoints().equals("0")) {
            mBinding.tvPointSystem.setVisibility(View.VISIBLE);
            mBinding.tvPointSystem.setText(question_detail.get(pos).getPoints());
        } else {
            mBinding.tvPointSystem.setVisibility(View.GONE);
        }

        Glide.with(mContext)
                .load(Pref.getValue(mContext, "foreground_img", ""))
                .into(mBinding.imgSubCategory);

        if (Pref.getValue(mContext, "invite_facebook", "").equals("1")) {

            for (int i = 0; i < question_detail.size(); i++) {
                if (question_detail.get(i).getQ_id().equals(Pref.getValue(mContext, "selected_qid", ""))) {
                    Pref.setValue(mContext, "ques_name", question_detail.get(i).getQuestion());
                    Pref.setValue(mContext, "selected_qid", question_detail.get(i).getQ_id());
                    if (cd.isConnectingToInternet()) {
                        new ExecuteTask_sub_category_check().execute(Pref.getValue(mContext, "token", ""), Pref.getValue(mContext, "cate_id", ""));
                        Pref.setValue(mContext, "invite_facebook", "0");
                    } else {
                        cd.showToast(mContext, R.string.NO_INTERNET_CONNECTION);
                    }
                }
            }


        }

        mBinding.rlViewleaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, SubCategoryLeaderboardUserListActivity.class).putExtra("cat_id_leader", Pref.getValue(mContext, "cate_id", "")).putExtra("sub_cat_id_leader", question_detail.get(pos).getQ_id()).putExtra("total_point", question_detail.get(pos).getPoints()));

            }
        });

        mBinding.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("set_click", Pref.getValue(mContext, "set_click", "") + "---");

                if (Pref.getValue(mContext, "set_click", "").equals("0")) {
                    Log.v("qid", question_detail.get(pos).getQ_id() + " ");
                    Pref.setValue(mContext, "ques_name", question_detail.get(pos).getQuestion());
                    Pref.setValue(mContext, "selected_qid", question_detail.get(pos).getQ_id());
                    Pref.setValue(mContext, "point_criteria", question_detail.get(pos).getPoint_criteria());
                    if (Pref.getValue(mContext, "facebook_request", "").equals("1")) {
                        confirmationforChallengeGame(mContext);
                    } else {
                        if (cd.isConnectingToInternet()) {
                            Pref.setValue(mContext, "set_click", "1");
                            new ExecuteTask_sub_category_check().execute(Pref.getValue(mContext, "token", ""), Pref.getValue(mContext, "cate_id", ""));
                        } else {
                            cd.showToast(mContext, R.string.NO_INTERNET_CONNECTION);
                        }
                    }

                }


            }
        });
        
        /*if(pos==0)
        {
            mBinding.txtTitle.setText(question_detail.get(pos));
            mBinding.txtTitle1.setText(question_detail.get(pos+1));
        }
        else if(pos%2==0)
        {
            mBinding.txtTitle.setText(question_detail.get(pos));
        }
        else if(pos%2!=0)
        {
            mBinding.txtTitle1.setText(question_detail.get(pos));
        }
*/
        return mBinding.getRoot();

    }

    public void printDifference(long startDate, long endDate) {

        //milliseconds
        long different = endDate - startDate;

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);


    }

    /**
     * dialog for expired signal when user click on expire signal then popup
     */
    private void confirmationforChallengeGame(Context context) {

        challengeDialog = new Dialog(context);
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        challengeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        challengeDialog.setContentView(R.layout.confirmation_for_challenge_game);
        challengeDialog.setCanceledOnTouchOutside(false);
        challengeDialog.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        /*Window window = challengeDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        DisplayMetrics displaymetrics = new DisplayMetrics();
        //getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        window.setAttributes(lp);*/
        lp.copyFrom(challengeDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        challengeDialog.show();
        challengeDialog.getWindow().setAttributes(lp);

        challengeDialog.getWindow().setGravity(Gravity.CENTER);
        challengeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        Button mOkbtn = (Button) challengeDialog.findViewById(R.id.btn_ok_start_game);
        Button mCancelGameBtn=(Button)challengeDialog.findViewById(R.id.btn_cancel_game);
        TextView mChallengeDesc = (TextView) challengeDialog.findViewById(R.id.tv_challenge_desc);
        mChallengeDesc.setTypeface(FontCustom.setFontcontent(context));

        mCancelGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pref.setValue(mContext, "set_click", "0");
                challengeDialog.dismiss();
            }
        });
        mOkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cd.isConnectingToInternet()) {
                    Pref.setValue(mContext, "set_click", "1");
                    new ExecuteTask_sub_category_check().execute(Pref.getValue(mContext, "token", ""), Pref.getValue(mContext, "cate_id", ""));
                    Pref.setValue(mContext, "invite_facebook", "0");
                    challengeDialog.dismiss();
                } else {
                    cd.showToast(mContext, R.string.NO_INTERNET_CONNECTION);
                }
            }
        });
        challengeDialog.show();
    }


    class ExecuteTask_sub_category_check extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(mContext);
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

                //Log.e("Soundfile","sound subcategory " +result );
                final JSONObject jsonObject;
                jsonObject = new JSONObject(result);
                if (jsonObject.getString("code").equals("200")) {

                    final JSONArray json = jsonObject.getJSONArray("data");
                    /**
                     * Store all data in arraylist first
                     */
                    answerImageArrayList.clear();
                    ArrayList<String> temp_qid = new ArrayList<>();
                    for (int i = 0; i < json.length(); i++) {


                        if (json.getJSONObject(i).getJSONArray("answer").length() > 0) {

                            AnswerImage[] answerImages = new AnswerImage[json.getJSONObject(i).getJSONArray("answer").length()];
                            for (int j = 0; j < json.getJSONObject(i).getJSONArray("answer").length(); j++) {
                                answerImages[j] = new AnswerImage();
                                answerImages[j].setCategory_id(Pref.getValue(mContext, "cate_id", ""));
                                answerImages[j].setQ_id(json.getJSONObject(i).getString("q_id"));
                                answerImages[j].setAns_id(json.getJSONObject(i).getJSONArray("answer").getJSONObject(j).getString("ans_id"));
                                answerImages[j].setAns_img(json.getJSONObject(i).getJSONArray("answer").getJSONObject(j).getString("images"));
                                answerImages[j].setName(json.getJSONObject(i).getJSONArray("answer").getJSONObject(j).getString("name"));
                                answerImages[j].setPriority(json.getJSONObject(i).getJSONArray("answer").getJSONObject(j).getString("priority"));
                                answerImages[j].setPoint(json.getJSONObject(i).getJSONArray("answer").getJSONObject(j).getString("point"));
                                answerImages[j].setSound(json.getJSONObject(i).getJSONArray("answer").getJSONObject(j).getString("sound"));
                                // Log.e("Soundfile","9999 adapter " + json.getJSONObject(i).getJSONArray("answer").getJSONObject(j).getString("sound"));
                                answerImageArrayList.add(answerImages[j]);

                            }
                        }
                        /**
                         * store all qid in questionsarraylist
                         */
                        temp_qid.add(json.getJSONObject(i).getString("q_id"));

                    }

                    if (temp_qid.contains(Pref.getValue(mContext, "selected_qid", ""))) {
                        /**
                         *  By using this code remove all images from table.
                         */

                        final RealmResults<AnswerImage> answerImages = realm.where(AnswerImage.class).findAll();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                answerImages.deleteAllFromRealm();
                            }
                        });

                        /**
                         * By using this code store all image to db answerImageArrayList from arraylist
                         */

                        for (int i = 0; i < answerImageArrayList.size(); i++) {

                            final int finalI = i;
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    // Add a person
                                    AnswerImage answerImage = realm.createObject(AnswerImage.class);
                                    answerImage.setCategory_id(answerImageArrayList.get(finalI).getCategory_id());
                                    answerImage.setQ_id(answerImageArrayList.get(finalI).getQ_id());
                                    answerImage.setAns_id(answerImageArrayList.get(finalI).getAns_id());
                                    answerImage.setAns_img(answerImageArrayList.get(finalI).getAns_img());
                                    answerImage.setName(answerImageArrayList.get(finalI).getName());
                                    answerImage.setPriority(answerImageArrayList.get(finalI).getPriority());
                                    answerImage.setPoint(answerImageArrayList.get(finalI).getPoint());
                                    answerImage.setSound(answerImageArrayList.get(finalI).getSound());
                                    //  Log.e("Soundfile","111 adapter " + answerImageArrayList.get(finalI).getSound());
                                }
                            });
                        }

                        WebService.dismissProgress();
                        Intent intent = new Intent(mContext, SelectAnswerActivity_new.class);
                        ((Activity)mContext).startActivityForResult(intent, 100);
                    } else {
                        WebService.dismissProgress();
                        Intent intent = new Intent(mContext, CategoryActivity1.class);
                        mContext.startActivity(intent);
                        ((Activity) mContext).overridePendingTransition(0, 0);
                        ((Activity) mContext).finish();
                    }


                } else if (jsonObject.getString("code").equals("150")) {


                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public  void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("MyAdapter", "onActivityResult");
        if (requestCode == 100 && resultCode==RESULT_OK) {
            ((Activity)mContext).finish();
        }

        if(Pref.getValue(mContext, "reload_data", "").equals("0")){
            ((Activity)mContext).finish();
        }
    }

}
