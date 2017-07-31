package com.zeeba.Activity.Dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.zeeba.Activity.BaseActivity;
import com.zeeba.Activity.MainActivity;
import com.zeeba.Adapter.SubCategoryAdapter;
import com.zeeba.Model.AnswerImage;
import com.zeeba.Model.Category;
import com.zeeba.Model.Questions;
import com.zeeba.Model.Questions_Model;
import com.zeeba.R;
import com.zeeba.Webservice.WebService;
import com.zeeba.fragment.MyChallengersAcceptedFacebookListFragment;
import com.zeeba.utils.Constants;
import com.zeeba.utils.HeaderGridView;
import com.zeeba.utils.Pref;
import com.zeeba.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static com.zeeba.Activity.MainActivity.realm;

public class CategoryActivity1 extends BaseActivity {

    /**
     * DEclare all views
     */
    private TextView headerText;
    private HeaderGridView listView;
    private View headerView;
    private View headerSpace;
    private ImageView header_image_view;
    private ImageView img_left;
    private ProgressBar progressBar1;
    private RelativeLayout rl_challnges_fb_btm;
    AdView adView;
    SubCategoryAdapter subCategoryAdapter;
    /**
     * Declare arraylist
     */
    ArrayList<Questions_Model> questionsArrayList = new ArrayList<>();
    ArrayList<AnswerImage> answerImageArrayList = new ArrayList<>();
    ArrayList<String> question_id_list = new ArrayList<>();
    ArrayList<String> question_id_list_db = new ArrayList<>();
    /**
     * Declare variable to check remaining days,hours
     */
    long elapsedDays;
    long elapsedHours;
    /**
     * Intialize flag to set expiration
     */
    int check_expiration = 0;
    /**
     * Intialize variable to set database size
     */
    int db_size = 0;
    FrameLayout fm_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category1);
        /**
         * Intialize all views
         */
        listView = (HeaderGridView) findViewById(R.id.list_view);
        headerView = findViewById(R.id.rl_scroll_main);
        headerText = (TextView) findViewById(R.id.txt_heading);
        header_image_view = (ImageView) findViewById(R.id.header_image_view);
        img_left = (ImageView) findViewById(R.id.imgleft);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        adView = (AdView) findViewById(R.id.adView);
        fm_1 = (FrameLayout) findViewById(R.id.fm_1);
        rl_challnges_fb_btm = (RelativeLayout) findViewById(R.id.rl_challnges_fb_btm);
        rl_challnges_fb_btm.setClickable(true);
        header_image_view.setClickable(false);
        progressBar1.setVisibility(View.VISIBLE);
        if (Pref.getValue(CategoryActivity1.this, "add_display", "").equalsIgnoreCase("0")) {
          /*  RelativeLayout.LayoutParams ps= (RelativeLayout.LayoutParams)fm_1.getLayoutParams();
            ps.setMargins(0,0,0,0);
            fm_1.setLayoutParams(ps);*/
            adView.setVisibility(View.GONE);
        } else {
           /* RelativeLayout.LayoutParams ps= (RelativeLayout.LayoutParams)fm_1.getLayoutParams();
            ps.setMargins(0,0,0,180);
            fm_1.setLayoutParams(ps);*/
            adView.setVisibility(View.VISIBLE);
        }
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        /**
         * Provide animation effect of header
         */
        setListViewHeader();
        /**
         * Get data from db to display header
         */
        try {
            getdata_deomdb();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         *  replace quetion with other if it is expired
         */
        //check_expiration();
        /**
         *  call api
         */
        // realm = Realm.getDefaultInstance(); //for temp purpose
        if (realm != null) {
            call_api();
        }

        img_left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return false;
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //  getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }
        // Handle list View scroll events
        listView.setOnScrollListener(onScrollListener());

        if (Pref.getValue(CategoryActivity1.this, "facebook_request", "").equals("0")) {
            rl_challnges_fb_btm.setVisibility(View.VISIBLE);
        } else {
            rl_challnges_fb_btm.setVisibility(View.GONE);
        }


    }

    public void getdata_deomdb() {
        final RealmResults<Category> category = realm.where(Category.class).equalTo("id", Pref.getValue(CategoryActivity1.this, "cate_id", "")).findAll();

        Log.v("category_size", category.size() + "");
        Pref.setValue(CategoryActivity1.this, "ques_name", category.get(0).getName());
        headerText.setText(category.get(0).getName());
        Glide.with(CategoryActivity1.this)
                .load(category.get(0).getBackground())
                .into(header_image_view);
        Pref.setValue(CategoryActivity1.this, "foreground_img", category.get(0).getForeground());

    }

    public void call_api() {
        questionsArrayList.clear();
        answerImageArrayList.clear();
        question_id_list.clear();
        question_id_list_db.clear();

        final RealmResults<Questions> question_data = realm.where(Questions.class)
                .equalTo("id", Pref.getValue(CategoryActivity1.this, "cate_id", ""))
                .findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                question_data.deleteAllFromRealm();
            }
        });

        final RealmResults<AnswerImage> answerImages = realm.where(AnswerImage.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                answerImages.deleteAllFromRealm();
            }
        });

        if (cd.isConnectingToInternet()) {
            new ExecuteTask_category_list().execute(Pref.getValue(CategoryActivity1.this, "token", ""), Pref.getValue(CategoryActivity1.this, "cate_id", ""));
        } else {
            cd.showToast(CategoryActivity1.this, R.string.NO_INTERNET_CONNECTION);
        }

    }

    public void check_expiration() {
        final RealmResults<Questions> questions = realm.where(Questions.class).equalTo("id", Pref.getValue(CategoryActivity1.this, "cate_id", "")).findAll();
        if (questions.size() > 0) {
            Questions_Model[] que = new Questions_Model[questions.size()];
            for (int i = 0; i < que.length; i++) {

                printDifference(System.currentTimeMillis(), Long.parseLong(questions.get(i).getExpire_date()));
                Log.v("diff", elapsedDays + "--");
                if (elapsedDays < 1 && elapsedHours < 12) {
                    final int finalI = i;
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            /**
                             *  Set visible_to_user = 0 if any questions expired
                             */
                            questions.get(finalI).setVisible_to_user("0");
                            check_expiration = 1;

                        }
                    });

                }
            }
        }
    }

    public void display_data() {
        //progressBar1.setVisibility(View.VISIBLE);

        if (realm != null) {
            final RealmResults<Questions> questions = realm.where(Questions.class)
                    .equalTo("id", Pref.getValue(CategoryActivity1.this, "cate_id", ""))
                    .equalTo("visible_to_user", "1").findAll();

            Log.e("questions_my", questions.size() + " ");

            if (questions.size() > 0) {
                questionsArrayList.clear();
                Questions_Model[] que = new Questions_Model[questions.size()];
                for (int i = 0; i < que.length; i++) {

                    if (questions.get(i).getVisible_to_user().equals("1")) {
                        que[i] = new Questions_Model();
                        que[i].setId(questions.get(i).getId());
                        que[i].setQ_id(questions.get(i).getQ_id());
                        que[i].setQuestion(questions.get(i).getQuestion());
                        que[i].setExpire_date(questions.get(i).getExpire_date());
                        que[i].setDays(questions.get(i).getDays());
                        que[i].setPoints(questions.get(i).getPoints());
                        que[i].setPoint_criteria(questions.get(i).getPoint_criteria());
                        que[i].setVisible_to_user(questions.get(i).getVisible_to_user());
                        questionsArrayList.add(que[i]);
                    }

                }
                if (questionsArrayList.size() == 0) {
                    Toast.makeText(CategoryActivity1.this, "All questions expired!", Toast.LENGTH_LONG).show();
                    WebService.showProgress(CategoryActivity1.this);
                    finish();
                } else {
                    subCategoryAdapter = new SubCategoryAdapter(CategoryActivity1.this, questionsArrayList);
                    listView.setAdapter(subCategoryAdapter);
                    progressBar1.setVisibility(View.GONE);
                }
            }
        }

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

    private void setListViewHeader() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listHeader = inflater.inflate(R.layout.header_layout1, null, false);
        headerSpace = listHeader.findViewById(R.id.header_space);
        ImageView headView1 = (ImageView) listHeader.findViewById(R.id.header_space1);

        if (Pref.getValue(CategoryActivity1.this, "facebook_request", "").equals("0")) {
            headView1.setVisibility(View.VISIBLE);

            headView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rl_challnges_fb_btm.setVisibility(View.VISIBLE);
                    Pref.setValue(CategoryActivity1.this, "facebook_request", "0");
                    Pref.setValue(CategoryActivity1.this, "reload_data", "0");
                    finish();
                }
            });
        } else {
            headView1.setVisibility(View.GONE);
        }


        listView.addHeaderView(listHeader);
    }

    private void setListViewData() {
        List<String> modelList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            modelList.add("Item " + (i + 1));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_layout, R.id.text, modelList);
        listView.setAdapter(adapter);
    }

    private AbsListView.OnScrollListener onScrollListener() {
        return new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                // Check if the first item is already reached to top
                if (listView.getFirstVisiblePosition() == 0) {
                    View firstChild = listView.getChildAt(0);
                    int topY = 0;
                    if (firstChild != null) {
                        topY = firstChild.getTop();

                    }

                    int headerTopY = headerSpace.getTop();

                    headerText.setY(Math.max(0, headerTopY + topY));
                    headerText.setBackgroundColor(Color.parseColor("#00000000"));

                    // Set the image to scroll half of the amount that of HeaderGridView
                    headerView.setY(topY * 0.7f);
                } else {
                    headerText.setBackgroundColor(Color.parseColor("#ec1078"));
                }
            }
        };
    }


    public void fill_db_first() {

        final RealmResults<Questions> questions = realm.where(Questions.class)
                .equalTo("id", Pref.getValue(CategoryActivity1.this, "cate_id", ""))
                .findAll();
        /**
         *  when no data in database at that time if condition execute and store all data to database from arraylist
         */
        if (questions.size() == 0) {

            if (questionsArrayList.size() > 0) {
                for (int i = 0; i < questionsArrayList.size(); i++) {

                    final int finalI = i;
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            // Add a person
                            Questions questions = realm.createObject(Questions.class);
                            questions.setId(questionsArrayList.get(finalI).getId());
                            questions.setQ_id(questionsArrayList.get(finalI).getQ_id());
                            questions.setQuestion(questionsArrayList.get(finalI).getQuestion());
                            questions.setDays(questionsArrayList.get(finalI).getDays());
                            questions.setExpire_date(questionsArrayList.get(finalI).getExpire_date());
                            questions.setVisible_to_user(questionsArrayList.get(finalI).getVisible_to_user());
                            questions.setPoints(questionsArrayList.get(finalI).getPoints());
                            questions.setPoint_criteria(questionsArrayList.get(finalI).getPoint_criteria());
                        }
                    });
                }
            }
        }
        /**
         * when data already in database at that time else call
         */
        else {

            /**
             *  By using this for loop remove all data from database which is not found in arraylist
             */

            for (int i = 0; i < questions.size(); i++) {
                Log.v("check_data", question_id_list.contains(questions.get(i).getQ_id()) + " ---");

                if ((question_id_list.contains(questions.get(i).getQ_id()))) {

                } else {
                    // check_expiration=1;
                    final int finalI = i;
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            final RealmResults<Questions> questions_del = realm.where(Questions.class)
                                    .equalTo("id", Pref.getValue(CategoryActivity1.this, "cate_id", ""))
                                    .equalTo("q_id", questions.get(finalI).getQ_id())
                                    .findAll();

                            final RealmResults<AnswerImage> ans_img = realm.where(AnswerImage.class)
                                    .equalTo("category_id", Pref.getValue(CategoryActivity1.this, "cate_id", ""))
                                    .equalTo("q_id", questions.get(finalI).getQ_id())
                                    .findAll();
                            if (ans_img.size() > 0) {
                                ans_img.get(0).deleteFromRealm();
                            }
                            if (questions.size() > 0) {
                                questions_del.get(0).deleteFromRealm();
                            }
                        }
                    });
                }
            }

            /**
             * Store all qid from db to question_id_list_db arraylist
             */

            for (int k = 0; k < questions.size(); k++) {
                question_id_list_db.add(questions.get(k).getQ_id());
            }

            db_size = questions.size();

            /**
             *  By using this for loop store all new data from arraylist to database
             */

            for (int i = 0; i < questionsArrayList.size(); i++) {
                if ((question_id_list_db.contains(questionsArrayList.get(i).getQ_id()))) {

                } else {
                    final int finalI = i;
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            Questions questions = realm.createObject(Questions.class);
                            questions.setId(questionsArrayList.get(finalI).getId());
                            questions.setQ_id(questionsArrayList.get(finalI).getQ_id());
                            questions.setQuestion(questionsArrayList.get(finalI).getQuestion());
                            questions.setDays(questionsArrayList.get(finalI).getDays());
                            questions.setPoints(questionsArrayList.get(finalI).getPoints());
                            questions.setPoint_criteria(questionsArrayList.get(finalI).getPoint_criteria());
                            questions.setExpire_date(questionsArrayList.get(finalI).getExpire_date());
                            questions.setVisible_to_user("1");
                            /*if(db_size<6)
                            {
                                questions.setVisible_to_user("1");
                                db_size=db_size+1;
                            }
                            else {
                                questions.setVisible_to_user("1");
                                db_size=db_size+1;
                            }*/

                        }
                    });
                }
            }

        }

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
                    //answerImage.setSound(answerImageArrayList.get(finalI).getSound());

                }
            });
        }

        /**
         *  In this code if expiration flag is set then make next qid visible to the user
         */
       /* realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                int temp_diff=0;

               // if (check_expiration == 1) {
                    final RealmResults<Questions> ques = realm.where(Questions.class)
                            .equalTo("id", Pref.getValue(CategoryActivity1.this, "cate_id", ""))
                            .findAll();
                    String q_id="";
                    final RealmResults<Questions> questions_visible = realm.where(Questions.class)
                            .equalTo("id", Pref.getValue(CategoryActivity1.this, "cate_id", ""))
                            .equalTo("visible_to_user", "1").findAll();
                    if (questions_visible.size() <6) {
                        q_id = questions_visible.get(questions_visible.size() - 1).getQ_id();
                        temp_diff=6-questions_visible.size();
                    }

                    int pos_next_que=0;


                    for(int i=0;i<temp_diff;i++) {
                        question_id_list_db.clear();
                        pos_next_que=0;
                        final RealmResults<Questions> ques_new = realm.where(Questions.class)
                                .equalTo("id", Pref.getValue(CategoryActivity1.this, "cate_id", ""))
                                .findAll();

                        for(int k=0;k<ques_new.size();k++)
                        {
                            question_id_list_db.add(ques_new.get(k).getQ_id());
                            if(q_id.equals(ques_new.get(k).getQ_id()))
                            {
                                pos_next_que=k;
                                break;
                            }
                        }
                        if(ques_new.size()>(pos_next_que+1)) {

                            String next_q_id = ques_new.get(pos_next_que+1).getQ_id();
                            if (question_id_list_db.contains(next_q_id)) {

                                final RealmResults<Questions> que_id = realm.where(Questions.class).equalTo("q_id", next_q_id + "")
                                        .equalTo("id", Pref.getValue(CategoryActivity1.this, "cate_id", "")).findAll();
                                Log.e("que_id_size", que_id.size() + "--");
                                que_id.get(0).setVisible_to_user("1");


                            }
                        }
                    }
                    check_expiration = 0;
                //}
            }
        });*/


        /**
         *  Update question name
         */
        final RealmResults<Questions> question_data = realm.where(Questions.class)
                .equalTo("id", Pref.getValue(CategoryActivity1.this, "cate_id", ""))
                .findAll();

        for (int i = 0; i < questionsArrayList.size(); i++) {
            for (int j = 0; j < question_data.size(); j++) {
                if (questionsArrayList.get(i).getQ_id().equals(question_data.get(j).getQ_id())) {
                    final int finalI = i;
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            final RealmResults<Questions> question_update = realm.where(Questions.class)
                                    .equalTo("id", Pref.getValue(CategoryActivity1.this, "cate_id", ""))
                                    .equalTo("q_id", questionsArrayList.get(finalI).getQ_id())
                                    .findAll();
                            if (question_update.size() > 0) {
                                question_update.get(0).setQuestion(questionsArrayList.get(finalI).getQuestion());
                                question_update.get(0).setDays(questionsArrayList.get(finalI).getDays());
                            }
                        }
                    });
                }
            }
        }

    }


    class ExecuteTask_category_list extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // WebService.showProgress(CategoryActivity1.this);
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
                // WebService.dismissProgress();
                progressBar1.setVisibility(View.GONE);
                Log.v("categoryActivity1", result + "--");
                final JSONObject jsonObject;
                jsonObject = new JSONObject(result);
                if (jsonObject.getString("code").equals("200")) {
                    final JSONArray json = jsonObject.getJSONArray("data");
                    /**
                     * Store all data in arraylist first
                     */
                    Questions_Model[] questions_models = new Questions_Model[json.length()];
                    for (int i = 0; i < json.length(); i++) {

                        questions_models[i] = new Questions_Model();
                        questions_models[i].setId(Pref.getValue(CategoryActivity1.this, "cate_id", ""));
                        questions_models[i].setQ_id(json.getJSONObject(i).getString("q_id"));
                        questions_models[i].setQuestion(json.getJSONObject(i).getString("question"));
                        questions_models[i].setDays(json.getJSONObject(i).getString("days"));
                        questions_models[i].setPoints(json.getJSONObject(i).getString("points"));
                        questions_models[i].setPoint_criteria(json.getJSONObject(i).getString("point_criteria"));
                        /**
                         *  Set first six record visible to user
                         */
                        if (i < 6) {
                            questions_models[i].setVisible_to_user("1");
                        } else {
                            questions_models[i].setVisible_to_user("1");
                        }
                        /**
                         *  Set expiredate by add days in current date
                         */
                        if(Integer.parseInt(json.getJSONObject(i).getString("days"))>0) {
                            Calendar cal = Calendar.getInstance();
                            cal.add(Calendar.DATE, Integer.parseInt(json.getJSONObject(i).getString("days")));
                            questions_models[i].setExpire_date(cal.getTimeInMillis() + "");
                            questionsArrayList.add(questions_models[i]);
                            /**
                             * store all qid in questionsarraylist
                             */
                            question_id_list.add(json.getJSONObject(i).getString("q_id"));
                            /**
                             *  store all answers in answerImageArrayList
                             */
                            if (json.getJSONObject(i).getJSONArray("answer").length() > 0) {

                                AnswerImage[] answerImages = new AnswerImage[json.getJSONObject(i).getJSONArray("answer").length()];
                                for (int j = 0; j < json.getJSONObject(i).getJSONArray("answer").length(); j++) {
                                    answerImages[j] = new AnswerImage();
                                    answerImages[j].setCategory_id(Pref.getValue(CategoryActivity1.this, "cate_id", ""));
                                    answerImages[j].setQ_id(json.getJSONObject(i).getString("q_id"));
                                    answerImages[j].setAns_id(json.getJSONObject(i).getJSONArray("answer").getJSONObject(j).getString("ans_id"));
                                    answerImages[j].setName(json.getJSONObject(i).getJSONArray("answer").getJSONObject(j).getString("name"));
                                    answerImages[j].setAns_img(json.getJSONObject(i).getJSONArray("answer").getJSONObject(j).getString("images"));
                                    //answerImages[j].setSound(json.getJSONObject(i).getJSONArray("answer").getJSONObject(j).optString("sound"));
                                    answerImageArrayList.add(answerImages[j]);

                                }
                            }
                        }
                    }
                } else if (jsonObject.getString("code").equals("1000")) {
                    Utils.exitApplication(CategoryActivity1.this);
                } else if (jsonObject.getString("code").equals("150")) {
                    Pref.setValue(CategoryActivity1.this, "cate_deleted", "yes");
                    finish();
                } else {

                    if (questionsArrayList.size() == 0) {
                        Toast.makeText(CategoryActivity1.this, "No data found!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }

                /**
                 * store data from arraylist to database
                 */

                fill_db_first();

                /**
                 * display all quetions from database
                 */

                display_data();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        subCategoryAdapter.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Pref.setValue(CategoryActivity1.this, "set_click", "0");
        /**
         * Get data from db to display header
         */
        MainActivity.config = new RealmConfiguration.Builder()
                .schemaVersion(1) // Must be bumped when the schema changes
                .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(MainActivity.config);

        if (Pref.getValue(CategoryActivity1.this, "start_over", "").equals("1")) {
            finish();
        }
    }
}