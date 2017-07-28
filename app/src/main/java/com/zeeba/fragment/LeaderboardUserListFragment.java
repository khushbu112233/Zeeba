package com.zeeba.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zeeba.Activity.FacebookLoginActivity;
import com.zeeba.Adapter.FacebookFriendListAdapter;
import com.zeeba.Adapter.LeaderboardUserlistAdapter;
import com.zeeba.Adapter.ReportSubCategoryAdapter;
import com.zeeba.Model.FacebookuserzeebaListModel;
import com.zeeba.Model.LeaderBoardUserListModel;
import com.zeeba.Model.ReportSubCategory;
import com.zeeba.R;
import com.zeeba.Report.ReportCategoryActivity1;
import com.zeeba.Webservice.WebService;
import com.zeeba.databinding.FragmentFacebookfrndlistBinding;
import com.zeeba.databinding.FragmentLeaderboradusrelistBinding;
import com.zeeba.utils.ConnectionDetector;
import com.zeeba.utils.Pref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class LeaderboardUserListFragment extends Fragment {

    FragmentLeaderboradusrelistBinding mBinding;
    ArrayList<LeaderBoardUserListModel> leaderBoardUserListModelArrayList=new ArrayList<>();
    LeaderboardUserlistAdapter leaderboardUserlistAdapter;
    View rootView;
    Context context;
    ConnectionDetector cd;
    private String TAG="LeaderboardUserListFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_leaderboradusrelist, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();
        cd=new ConnectionDetector(context);


        if(cd.isConnectingToInternet()) {
            new ExecuteTask_leader_borad_user_list().execute();
        }else
        {
            cd.showToast(context, R.string.NO_INTERNET_CONNECTION);
        }



        return rootView;
    }


    class ExecuteTask_leader_borad_user_list extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
             WebService.showProgress(context);
        }
        @Override
        protected String doInBackground(String... params) {

            String res = WebService.GetDataOnly(WebService.LEADER_POINT_SYSTEM_USER,"");
            Log.e("res....", "" + res);
            return res;
        }
        @Override
        protected void onPostExecute(String result) {
            try {
                 WebService.dismissProgress();
                Log.e(TAG,"result " +result);
                final JSONObject jsonObject;
                jsonObject= new JSONObject(result);
                if(jsonObject.getString("code").equals("200")) {
                    JSONArray dataJsonArray=jsonObject.optJSONArray("data");

                    for (int i = 0; i < dataJsonArray.length(); i++) {
                        JSONObject dataObject = dataJsonArray.getJSONObject(i);

                        Gson gson=new Gson();

                        LeaderBoardUserListModel leaderBoardUserListModel=gson.fromJson(dataObject.toString(),LeaderBoardUserListModel.class);
                        leaderBoardUserListModelArrayList.add(leaderBoardUserListModel);

                    }
                    leaderboardUserlistAdapter=new LeaderboardUserlistAdapter(getActivity(),leaderBoardUserListModelArrayList);
                    mBinding.gridLeaderboradUserList.setAdapter(leaderboardUserlistAdapter);

                }
                else
                {


                }



            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
