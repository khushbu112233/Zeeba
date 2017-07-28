package com.zeeba.Activity.Dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zeeba.Adapter.SubcategoryLeaderboardUserlistAdapter;
import com.zeeba.Model.SubcategoryLeaderBoardUserListModel;
import com.zeeba.R;
import com.zeeba.Webservice.WebService;
import com.zeeba.databinding.ActivitySubcategoryLeaderboradusrelistBinding;
import com.zeeba.utils.ConnectionDetector;
import com.zeeba.utils.Constants;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class SubCategoryLeaderboardUserListActivity extends Activity {

    ActivitySubcategoryLeaderboradusrelistBinding mBinding;
    ArrayList<SubcategoryLeaderBoardUserListModel> leaderBoardUserListModelArrayList = new ArrayList<>();
    SubcategoryLeaderboardUserlistAdapter leaderboardUserlistAdapter;
    View rootView;
    Context context;
    ConnectionDetector cd;
    private String TAG = "SubCategoryLeaderboardUserListActivity";
    Intent mIntent;
    String cat_id = "";
    String sub_cat_id = "";
    String total_point = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_subcategory_leaderboradusrelist);
        rootView = mBinding.getRoot();
        context = SubCategoryLeaderboardUserListActivity.this;
        cd = new ConnectionDetector(context);
        mIntent = getIntent();
        StatusBar();

        cat_id = mIntent.getStringExtra("cat_id_leader");
        sub_cat_id = mIntent.getStringExtra("sub_cat_id_leader");
        total_point= mIntent.getStringExtra("total_point");
        mBinding.txtTopplayers.setTypeface(FontCustom.setFontBold(this));
        mBinding.txtYourscore.setTypeface(FontCustom.setFontBold(this));
        mBinding.tvtotalPointSystem.setTypeface(FontCustom.setFont(this));
        mBinding.txtNoresultfound.setTypeface(FontCustom.setFontBold(this));
        mBinding.txtNoresultfound.setVisibility(View.GONE);
        mBinding.tvtotalPointSystem.setText(total_point);
        mBinding.imgleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (cd.isConnectingToInternet()) {
            new ExecuteTask_leader_borad_user_list().execute(Pref.getValue(SubCategoryLeaderboardUserListActivity.this,"token",""),cat_id, sub_cat_id);
        } else {
            cd.showToast(context, R.string.NO_INTERNET_CONNECTION);
        }
    }


    public void StatusBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

    }

    class ExecuteTask_leader_borad_user_list extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(context);
        }

        @Override
        protected String doInBackground(String... params) {

            String res = WebService.PostData(params, Constants.LEADER_BOARD_MONTHLY_POINT_SYSTEM, WebService.LEADER_POINT_MONTHLY_SYSTEM_USER);
            Log.e("res....", "" + res);
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                WebService.dismissProgress();
                Log.e(TAG, "result " + result);
                final JSONObject jsonObject;
                jsonObject = new JSONObject(result);
                if (jsonObject.getString("code").equals("200")) {

                    JSONArray dataJsonArray = jsonObject.optJSONArray("data");

                    for (int i = 0; i < dataJsonArray.length(); i++) {
                        JSONObject dataObject = dataJsonArray.getJSONObject(i);

                        Gson gson = new Gson();

                        SubcategoryLeaderBoardUserListModel leaderBoardUserListModel = gson.fromJson(dataObject.toString(), SubcategoryLeaderBoardUserListModel.class);
                        leaderBoardUserListModelArrayList.add(leaderBoardUserListModel);

                    }
                    leaderboardUserlistAdapter = new SubcategoryLeaderboardUserlistAdapter(SubCategoryLeaderboardUserListActivity.this, leaderBoardUserListModelArrayList, jsonObject.optString("max_point"));
                    mBinding.gridLeaderboradUserList.setAdapter(leaderboardUserlistAdapter);

                } else if(jsonObject.getString("code").equals("100")){
                        mBinding.txtNoresultfound.setVisibility(View.VISIBLE);
                    mBinding.txtNoresultfound.setText(jsonObject.optString("msg"));
                }
                else {
                    mBinding.txtNoresultfound.setVisibility(View.GONE);
                    Toast.makeText(context, ""+jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
