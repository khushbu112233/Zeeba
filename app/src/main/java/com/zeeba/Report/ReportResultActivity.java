package com.zeeba.Report;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.zeeba.Activity.BaseActivity;
import com.zeeba.Adapter.ReportCustomAdapter;
import com.zeeba.Adapter.ReportSubCategoryAdapter;
import com.zeeba.Model.ReportList;
import com.zeeba.Model.ReportSubCategory;
import com.zeeba.R;
import com.zeeba.Webservice.WebService;
import com.zeeba.utils.ConnectionDetector;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by aipxperts-ubuntu-01 on 13/5/17.
 */

public class ReportResultActivity extends BaseActivity {
    ArrayList prgmName;
    GridView grid_view_image_text;
    ArrayList<ReportList> answerImageArrayList = new ArrayList<>();
    TextView txt_heading;
    ImageView img_back;
    AdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.report_result_layout);
        grid_view_image_text = (GridView)findViewById(R.id.grid_view_image_text);
        cd= new ConnectionDetector(ReportResultActivity.this);
        txt_heading = (TextView)findViewById(R.id.txt_heading);
        img_back = (ImageView)findViewById(R.id.img_back);
        adView=(AdView)findViewById(R.id.adView);
        txt_heading.setTypeface(FontCustom.setFontBold(ReportResultActivity.this));
        txt_heading.setText("Result of all "+Pref.getValue(ReportResultActivity.this, "report_sub_cate_name",""));
        if(Pref.getValue(ReportResultActivity.this,"add_display","").equalsIgnoreCase("0"))
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
        if (cd.isConnectingToInternet()) {
            new ExecuteTask_sub_category_check().execute();
        } else {
            cd.showToast(ReportResultActivity.this, R.string.NO_INTERNET_CONNECTION);
        }
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    class ExecuteTask_sub_category_check extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(ReportResultActivity.this);
        }
        @Override
        protected String doInBackground(String... params) {

            String res = WebService.GetData(WebService.REPORT_LIST_SUB_CATEGORY,Pref.getValue(ReportResultActivity.this, "report_sub_cate_id",""));

            Log.e("res....", "" + res);
            return res;
        }
        @Override
        protected void onPostExecute(String result) {
            try {

                //Log.v("resultlogin",result+"--");
                WebService.dismissProgress();
                final JSONObject jsonObject;
                jsonObject= new JSONObject(result);
                if(jsonObject.getString("code").equals("200")) {

                    final JSONArray json = jsonObject.getJSONArray("data");
                    /**
                     * Store all data in arraylist first
                     */
                    answerImageArrayList = new ArrayList<>();
                    answerImageArrayList.clear();

                    ReportList[] answerImages=new ReportList[json.length()];
                    for (int j = 0; j < json.length(); j++) {
                        answerImages[j]=new ReportList();
                        //answerImages[j].setCategory_id(Pref.getValue(mContext,"report_cate_id", ""));
                        answerImages[j].setReport_id(json.getJSONObject(j).getString("id"));
                        answerImages[j].setAns_name(json.getJSONObject(j).getString("ans_name"));
                        answerImages[j].setPercentage(json.getJSONObject(j).getString("percentage"));
                        answerImages[j].setReport_image(json.getJSONObject(j).getString("image"));
                        answerImageArrayList.add(answerImages[j]);
                    }
                    ArrayList<String> name_list = new ArrayList<>();
                    ArrayList<String > image_list = new ArrayList<>();
                    for(int report =0 ;report<answerImageArrayList.size();report++)
                    {
                        name_list.add(answerImageArrayList.get(report).getAns_name()+" ("+answerImageArrayList.get(report).getPercentage()+"%)");
                        image_list.add(answerImageArrayList.get(report).getReport_image());
                    }


                    grid_view_image_text.setAdapter(new ReportCustomAdapter(ReportResultActivity.this, name_list,image_list));

                }
                else if(jsonObject.getString("code").equals("150"))
                {

                }else if(jsonObject.getString("code").equals("100"))
                {
                    Toast.makeText(ReportResultActivity.this,"No data found!",Toast.LENGTH_LONG).show();
                    finish();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
