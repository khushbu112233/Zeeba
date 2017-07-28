package com.zeeba.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zeeba.Adapter.ChallengerFBFriendListAdapter;
import com.zeeba.Adapter.LeaderboardUserlistAdapter;
import com.zeeba.Model.ChallengerFBuserListModel;
import com.zeeba.Model.LeaderBoardUserListModel;
import com.zeeba.R;
import com.zeeba.Webservice.WebService;
import com.zeeba.databinding.FragmentChallengerfriendlistBinding;
import com.zeeba.databinding.FragmentLeaderboradusrelistBinding;
import com.zeeba.utils.ConnectionDetector;
import com.zeeba.utils.Constants;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class ChallengerFriendsListFragment extends Fragment {

    FragmentChallengerfriendlistBinding mBinding;
    ArrayList<ChallengerFBuserListModel> challengerFBuserListModelArrayList=new ArrayList<>();
    ChallengerFBFriendListAdapter challengerFBFriendListAdapter;
    View rootView;
    Context context;
    ConnectionDetector cd;
    private String TAG="ChallengerFriendsListFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_challengerfriendlist, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();
        cd=new ConnectionDetector(context);

        mBinding.txtYourScore.setTypeface(FontCustom.setFont(getActivity()));
        if(cd.isConnectingToInternet()) {
            new ExecuteTask_Challenger_Friend_list().execute(Pref.getValue(getActivity(),"token",""));
        }else
        {
            cd.showToast(context, R.string.NO_INTERNET_CONNECTION);
        }



        return rootView;
    }


    class ExecuteTask_Challenger_Friend_list extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
             WebService.showProgress(context);
        }
        @Override
        protected String doInBackground(String... params) {

            String res = WebService.PostData(params, Constants.CHALLENGER_FRIEND_USER_LIST, WebService.CHALLENGER_FRIEND_FB_LIST);
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

                        ChallengerFBuserListModel challengerFBuserListModel=gson.fromJson(dataObject.toString(),ChallengerFBuserListModel.class);
                        challengerFBuserListModelArrayList.add(challengerFBuserListModel);

                    }
                    challengerFBFriendListAdapter=new ChallengerFBFriendListAdapter(getActivity(),challengerFBuserListModelArrayList);
                    mBinding.lvChallengerFriendList.setAdapter(challengerFBFriendListAdapter);

                }
                else
                {
                    Toast.makeText(context, ""+jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();

                }



            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
