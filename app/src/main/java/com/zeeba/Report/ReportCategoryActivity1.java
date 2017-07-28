package com.zeeba.Report;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import com.zeeba.Activity.Dashboard.CategoryActivity1;
import com.zeeba.Activity.Dashboard.SelectSocialSubCategoryActivity;
import com.zeeba.Adapter.ReportSubCategoryAdapter;
import com.zeeba.Adapter.SubCategoryAdapter;
import com.zeeba.Model.AnswerImage;
import com.zeeba.Model.Category;
import com.zeeba.Model.Questions;
import com.zeeba.Model.Questions_Model;
import com.zeeba.Model.ReportSubCategory;
import com.zeeba.R;
import com.zeeba.Webservice.WebService;
import com.zeeba.utils.Constants;
import com.zeeba.utils.HeaderGridView;
import com.zeeba.utils.Pref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.zeeba.Activity.MainActivity.realm;

public class ReportCategoryActivity1 extends BaseActivity {

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
    AdView adView;
    /**
     * Declare arraylist
     */
    ArrayList<ReportSubCategory> questionsArrayList = new ArrayList<>();
    ArrayList<AnswerImage> answerImageArrayList = new ArrayList<>();
    ArrayList<String> question_id_list=new ArrayList<>();
    ArrayList<String> question_id_list_db=new ArrayList<>();
    /**
     *  Declare variable to check remaining days,hours
     */
    long elapsedDays;
    long elapsedHours;
    /**
     * Intialize flag to set expiration
     */
    int check_expiration=0;
    /**
     *  Intialize variable to set database size
     */
    int db_size=0;
    FrameLayout fm_1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category1);
        /**
         * Intialize all views
         */
        listView = (HeaderGridView) findViewById(R.id.list_view);
        headerView = findViewById(R.id.header_image_view);
        headerText = (TextView) findViewById(R.id.txt_heading);
        header_image_view = (ImageView) findViewById(R.id.header_image_view);
        img_left = (ImageView) findViewById(R.id.imgleft);
        progressBar1=(ProgressBar)findViewById(R.id.progressBar1);
        adView=(AdView)findViewById(R.id.adView);
        fm_1 = (FrameLayout)findViewById(R.id.fm_1);
        header_image_view.setClickable(false);
        progressBar1.setVisibility(View.VISIBLE);

        if(Pref.getValue(ReportCategoryActivity1.this,"add_display","").equalsIgnoreCase("0"))
        {
         /*   RelativeLayout.LayoutParams ps= (RelativeLayout.LayoutParams)fm_1.getLayoutParams();
            ps.setMargins(0,0,0,0);
            fm_1.setLayoutParams(ps);*/
            adView.setVisibility(View.GONE);
        }else
        {
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
        //getdata_deomdb();
        /**
         *  replace quetion with other if it is expired
         */
        //check_expiration();
        /**
         *  call api
         */
        // call_api();
        if(cd.isConnectingToInternet()) {
            new ExecuteTask_category_list().execute(Pref.getValue(ReportCategoryActivity1.this, "report_cate_id", ""));
        }else
        {
            cd.showToast(ReportCategoryActivity1.this, R.string.NO_INTERNET_CONNECTION);
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
    }



    private void setListViewHeader() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listHeader = inflater.inflate(R.layout.header_layout1, null, false);
        headerSpace = listHeader.findViewById(R.id.header_space);

        listView.addHeaderView(listHeader);
    }

    private AbsListView.OnScrollListener onScrollListener () {
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
                    headerText.setText(Pref.getValue(ReportCategoryActivity1.this, "report_cate_name",""));
                    // Set the image to scroll half of the amount that of HeaderGridView
                    headerView.setY(topY * 0.7f);
                    Glide.with(ReportCategoryActivity1.this)
                            .load(Pref.getValue(ReportCategoryActivity1.this, "report_cate_image", ""))
                            .into(header_image_view);
                }
                else {
                    headerText.setBackgroundColor(Color.parseColor("#ec1078"));
                }
            }
        };
    }



    class ExecuteTask_category_list extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // WebService.showProgress(CategoryActivity1.this);
        }
        @Override
        protected String doInBackground(String... params) {

            String res = WebService.GetData(WebService.REPORT_SUB_CATEGORY,Pref.getValue(ReportCategoryActivity1.this, "report_cate_id", ""));
            Log.e("res....", "" + res);
            return res;
        }
        @Override
        protected void onPostExecute(String result) {
            try {
                // WebService.dismissProgress();
                Log.v("resultlogin",result+"--");
                final JSONObject jsonObject;
                jsonObject= new JSONObject(result);
                if(jsonObject.getString("code").equals("200")) {
                    final JSONArray json = jsonObject.getJSONArray("data");
                    /**
                     * Store all data in arraylist first
                     */
                    ReportSubCategory[] questions_models=new ReportSubCategory[json.length()];
                    for (int i = 0; i < json.length(); i++) {

                        questions_models[i]=new ReportSubCategory();
                        questions_models[i].setRepost_sub_cat_id(json.getJSONObject(i).getString("id"));
                        questions_models[i].setReport_sub_cat_name(json.getJSONObject(i).getString("name"));
                        questions_models[i].setReport_sub_days(json.getJSONObject(i).getString("days"));
                        /**
                         *  Set first six record visible to user
                         */
                        questionsArrayList.add(questions_models[i]);

                    }
                }
                else if(jsonObject.getString("code").equals("150"))
                {

                    finish();
                }

                if(questionsArrayList.size()==0)
                {
                    Toast.makeText(ReportCategoryActivity1.this,"No data found!",Toast.LENGTH_LONG).show();
                    finish();
                } else {

                    ReportSubCategoryAdapter subCategoryAdapter = new ReportSubCategoryAdapter(ReportCategoryActivity1.this, questionsArrayList);
                    listView.setAdapter(subCategoryAdapter);
                    progressBar1.setVisibility(View.GONE);
                }

                /**
                 * store data from arraylist to database
                 */

                //fill_db_first();

                /**
                 * display all quetions from database
                 */

                //display_data();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Pref.setValue(ReportCategoryActivity1.this,"set_click","0");

    }
}