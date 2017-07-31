package com.zeeba.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zeeba.Activity.Dashboard.DashBoardMainActivity;
import com.zeeba.Activity.Dashboard.DashboardFragment_new;
import com.zeeba.Adapter.ChallengerFBFriendListAdapter;
import com.zeeba.Adapter.NotificationofChallengesListAdapter;
import com.zeeba.Model.ChallengerFBuserListModel;
import com.zeeba.Model.NotificationFrndChallengesListModel;
import com.zeeba.R;
import com.zeeba.Webservice.WebService;
import com.zeeba.databinding.FragmentChallengerfriendlistBinding;
import com.zeeba.databinding.FragmentNotificationofchallengeslistBinding;
import com.zeeba.utils.ConnectionDetector;
import com.zeeba.utils.Constants;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;
import com.zeeba.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class NotificationofChallengesListFragment extends Fragment {

    FragmentNotificationofchallengeslistBinding mBinding;
    ArrayList<NotificationFrndChallengesListModel> notificationFrndChallengesListModelArrayList = new ArrayList<>();
    NotificationofChallengesListAdapter notificationofChallengesListAdapter;
    View rootView;
    Context context;
    ConnectionDetector cd;
    private String TAG = "ChallengerFriendsListFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_notificationofchallengeslist, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();
        cd = new ConnectionDetector(getActivity());
        mBinding.tvNotificationBlankView.setTypeface(FontCustom.setFontcontent(getActivity()));
        ((DashBoardMainActivity) getActivity()).mBinding.imgRefreshData.setVisibility(View.VISIBLE);

        ((DashBoardMainActivity) getActivity()).mBinding.imgRefreshData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cd.isConnectingToInternet()) {
                    new ExecuteTask_Notification_Frnd_challenges_list().execute(Pref.getValue(getActivity(), "token", ""), Pref.getValue(getActivity(), Constants.PREF_USER_FB_ID, ""));
                } else {
                    cd.showToast(context, R.string.NO_INTERNET_CONNECTION);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();


        if (cd.isConnectingToInternet()) {
            new ExecuteTask_Notification_Frnd_challenges_list().execute(Pref.getValue(getActivity(), "token", ""), Pref.getValue(getActivity(), Constants.PREF_USER_FB_ID, ""));
        } else {
            cd.showToast(context, R.string.NO_INTERNET_CONNECTION);
        }

        pulltoRefreshFuntion();
    }

    private void pulltoRefreshFuntion() {
        mBinding.swipeContainerForNotification.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (cd.isConnectingToInternet()) {
                    new ExecuteTask_Notification_Frnd_challenges_list().execute(Pref.getValue(getActivity(), "token", ""), Pref.getValue(getActivity(), Constants.PREF_USER_FB_ID, ""));
                } else {
                    cd.showToast(context, R.string.NO_INTERNET_CONNECTION);
                }
            }
        });
        // Configure the refreshing colors
        mBinding.swipeContainerForNotification.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    class ExecuteTask_Notification_Frnd_challenges_list extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(context);
        }

        @Override
        protected String doInBackground(String... params) {

            String res = WebService.PostData(params, Constants.NOTIFICATION_FRND_CHALLENGES_LIST, WebService.NOTIFICATION_FRND_CHALLENGES_LIST);
            Log.e("res....", "" + res);
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                WebService.dismissProgress();
                notificationFrndChallengesListModelArrayList.clear();
                mBinding.swipeContainerForNotification.setRefreshing(false);
                Log.e(TAG, "result " + result);
                final JSONObject jsonObject;
                jsonObject = new JSONObject(result);
                if (jsonObject.getString("code").equals("200")) {
                    JSONArray dataJsonArray = jsonObject.optJSONArray("data");
                    if (dataJsonArray.length() == 0) {
                        mBinding.tvNotificationBlankView.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.tvNotificationBlankView.setVisibility(View.GONE);
                    }
                    for (int i = 0; i < dataJsonArray.length(); i++) {
                        JSONObject dataObject = dataJsonArray.getJSONObject(i);

                        Gson gson = new Gson();

                        NotificationFrndChallengesListModel notificationFrndChallengesListModel = gson.fromJson(dataObject.toString(), NotificationFrndChallengesListModel.class);
                        notificationFrndChallengesListModelArrayList.add(notificationFrndChallengesListModel);

                    }
                    notificationofChallengesListAdapter = new NotificationofChallengesListAdapter(getActivity(), notificationFrndChallengesListModelArrayList);
                    mBinding.lvNotificationfacebookfrndList.setAdapter(notificationofChallengesListAdapter);
                } else if (jsonObject.optString("code").equalsIgnoreCase("100")) {
                    mBinding.tvNotificationBlankView.setVisibility(View.VISIBLE);
                } else if (jsonObject.optString("code").equals("1000")) {
                    Utils.exitApplication(getActivity());
                } else {
                    mBinding.tvNotificationBlankView.setVisibility(View.GONE);
                    Toast.makeText(context, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();

                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                            fm.popBackStack();

                        }
                        DashboardFragment_new fragment = new DashboardFragment_new();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).commit();
                        Pref.setValue(getActivity(), "facebook_request", "0");
                        Pref.setValue(getActivity(), "reload_data", "1");
                        Pref.setValue(getActivity(), "drawer_value", "8");
                        return true;
                    }
                }
                return false;
            }
        });
    }


}
