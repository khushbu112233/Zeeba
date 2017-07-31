package com.zeeba.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.google.gson.Gson;
import com.zeeba.Activity.Dashboard.ChallengesListFacebookFrndActivity;
import com.zeeba.Activity.Dashboard.DashBoardMainActivity;
import com.zeeba.Adapter.ChallengerFBFriendListAdapter;
import com.zeeba.Adapter.FacebookFriendListAdapter;
import com.zeeba.Model.ChallengerFBuserListModel;
import com.zeeba.Model.FacebookuserzeebaListModel;
import com.zeeba.R;
import com.zeeba.Webservice.WebService;
import com.zeeba.databinding.ActivityChallengesListFacebookFrndBinding;
import com.zeeba.databinding.FragmentChallengerfriendlistBinding;
import com.zeeba.databinding.FragmentFacebookChallengesListFrndBinding;
import com.zeeba.utils.ConnectionDetector;
import com.zeeba.utils.Constants;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ChallengerFacebookFriendsListFragment extends Fragment {

    FragmentFacebookChallengesListFrndBinding mBinding;
    Context context;
    ArrayList<FacebookuserzeebaListModel> facebookuserzeebaListModelArrayList = new ArrayList<FacebookuserzeebaListModel>();
    FacebookFriendListAdapter facebookFriendListAdapter;
    private String TAG = "ChallengesListFacebook";
    ArrayList<String> permission = new ArrayList<>();
    String appLinkUrl, previewImageUrl;
    View rootView;
    ConnectionDetector cd;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_facebook_challenges_list_frnd, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();
        cd=new ConnectionDetector(context);

        StatusBar();
        mBinding.tvChallengeFriend.setTypeface(FontCustom.setFontBold(context));
        mBinding.btnInvitaionFacebookFrndList.setTypeface(FontCustom.setFontcontent(context));
        if (!Pref.getValue(getActivity(), Constants.PREF_USER_FB_ID, "").equals("")) {
            mBinding.lvFacebookFrnd.setVisibility(View.VISIBLE);
            permission.add("id,first_name,last_name,email,name");
            getFriendsList();
            WebService.showProgress(getActivity());
            /*SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            Gson gson = new Gson();
            if (!TextUtils.isEmpty(sharedPrefs.getString("facebookFriendList", null))) {
                String json = sharedPrefs.getString("facebookFriendList", null);
                Type type = new TypeToken<ArrayList<FacebookuserzeebaListModel>>() {
                }.getType();
                facebookuserzeebaListModelArrayList = gson.fromJson(json, type);

                facebookFriendListAdapter = new FacebookFriendListAdapter(ChallengesListFacebookFrndActivity.this, facebookuserzeebaListModelArrayList);
                mBinding.lvFacebookFrnd.setAdapter(facebookFriendListAdapter);
            }*/
        }
        pulltoRefreshFuntion();



        mBinding.rlInvitaionFbBtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                appLinkUrl = "https://fb.me/336546833425725";
                previewImageUrl = "http://zeebapro.us/public/admin/fb_banner.jpg";

                if (AppInviteDialog.canShow()) {
                    AppInviteContent content = new AppInviteContent.Builder()
                            .setApplinkUrl(appLinkUrl)
                            .setPreviewImageUrl(previewImageUrl)
                            .build();
                    AppInviteDialog.show(getActivity(), content);
                }
            }
        });



        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((DashBoardMainActivity) getActivity()).mBinding.drawerIcon.setVisibility(View.GONE);
        ((DashBoardMainActivity) getActivity()).mBinding.imgLeftIcon.setVisibility(View.VISIBLE);
        ((DashBoardMainActivity) getActivity()).mBinding.imgRefreshData.setVisibility(View.VISIBLE);
        ((DashBoardMainActivity) getActivity()).mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        ((DashBoardMainActivity) getActivity()).mBinding.imgLeftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        ((DashBoardMainActivity) getActivity()).mBinding.imgRefreshData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permission.add("id,first_name,last_name,email,name");
                getFriendsList();
                WebService.showProgress(getActivity());
            }
        });
    }

    private void pulltoRefreshFuntion() {
        mBinding.swipeContainerForFrnd.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                permission.add("id,first_name,last_name,email,name");
                getFriendsList();
                WebService.showProgress(getActivity());
            }
        });
        // Configure the refreshing colors
        mBinding.swipeContainerForFrnd.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    public void StatusBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

    }

    private List<String> getFriendsList() {
        final List<String> friendslist = new ArrayList<String>();

        Date formate = new Date(Pref.getValue(getActivity(), "FB_EXPIRE", ""));

        AccessToken PageAT = new AccessToken(Pref.getValue(getActivity(), Constants.PREF_USER_FB_TOKEN, ""), Pref.getValue(getActivity(), "FB_APPLICATION_ID", ""), Pref.getValue(getActivity(), "FB_USER_ID", ""), permission, null, AccessTokenSource.FACEBOOK_APPLICATION_NATIVE, formate, null);
        new GraphRequest(PageAT, "/me/friends", null, HttpMethod.GET, new GraphRequest.Callback() {
            public void onCompleted(GraphResponse response) {
/* handle the result */
                Log.e(TAG, "$$ " + response.toString());
                try {
                    facebookuserzeebaListModelArrayList.clear();
                    mBinding.swipeContainerForFrnd.setRefreshing(false);
                    WebService.dismissProgress();
                    JSONObject responseObject = response.getJSONObject();
                    JSONArray dataArray = responseObject.getJSONArray("data");

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataObject = dataArray.getJSONObject(i);
                        String ptofile = "http://graph.facebook.com/" + dataObject.optString("id") + "/picture?type=large";
                        Log.e(TAG, "Photos " + ptofile);
                        Gson gson = new Gson();

                        FacebookuserzeebaListModel facebookuserzeebaListModel = gson.fromJson(dataObject.toString(), FacebookuserzeebaListModel.class);
                        facebookuserzeebaListModelArrayList.add(facebookuserzeebaListModel);

                        facebookFriendListAdapter = new FacebookFriendListAdapter(getActivity(), facebookuserzeebaListModelArrayList);
                        mBinding.lvFacebookFrnd.setAdapter(facebookFriendListAdapter);

                        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = sharedPrefs.edit();

                        String json = gson.toJson(facebookuserzeebaListModelArrayList);

                        editor.putString("facebookFriendList", json);
                        editor.commit();
                    }
                  /*  Log.e(TAG, "size "+friendslist.toString());
                    List<String> list = friendslist;
                    String friends = "";
                    if (list != null && list.size() > 0) {
                        friends = list.toString();
                        if (friends.contains("[")) {
                            friends = (friends.substring(1, friends.length()-1));
                        }
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    // hideLoadingProgress();
                    //   callFacebookApi(userfbid);

                }
            }
        }).executeAsync();
        return friendslist;
    }

}
