package com.zeeba.Activity.Dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zeeba.Activity.FacebookLoginActivity;
import com.zeeba.Adapter.FacebookFriendListAdapter;
import com.zeeba.Model.FacebookuserzeebaListModel;
import com.zeeba.R;
import com.zeeba.Webservice.WebService;
import com.zeeba.databinding.ActivityChallengesListFacebookFrndBinding;
import com.zeeba.fragment.NotificationofChallengesListFragment;
import com.zeeba.utils.Constants;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChallengesListFacebookFrndActivity extends AppCompatActivity {

    ActivityChallengesListFacebookFrndBinding mBinding;
    Context mContext;
    ArrayList<FacebookuserzeebaListModel> facebookuserzeebaListModelArrayList = new ArrayList<FacebookuserzeebaListModel>();
    FacebookFriendListAdapter facebookFriendListAdapter;
    private String TAG = "ChallengesListFacebook";
    ArrayList<String> permission = new ArrayList<>();
    String appLinkUrl, previewImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_challenges_list_facebook_frnd);
        mContext = ChallengesListFacebookFrndActivity.this;
        StatusBar();
        mBinding.tvChallengeFriend.setTypeface(FontCustom.setFontBold(mContext));
        mBinding.btnInvitaionFacebookFrndList.setTypeface(FontCustom.setFontcontent(mContext));
        if (!Pref.getValue(ChallengesListFacebookFrndActivity.this, Constants.PREF_USER_FB_ID, "").equals("")) {
            mBinding.lvFacebookFrnd.setVisibility(View.VISIBLE);
            permission.add("id,first_name,last_name,email,name");
            getFriendsList();
            WebService.showProgress(ChallengesListFacebookFrndActivity.this);
            /*SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
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

        mBinding.imgRefreshData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permission.add("id,first_name,last_name,email,name");
                getFriendsList();
                WebService.showProgress(ChallengesListFacebookFrndActivity.this);
            }
        });

        mBinding.imgleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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
                    AppInviteDialog.show(ChallengesListFacebookFrndActivity.this, content);
                }
            }
        });


    }

    private void pulltoRefreshFuntion() {
        mBinding.swipeContainerForFrnd.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                permission.add("id,first_name,last_name,email,name");
                getFriendsList();
                WebService.showProgress(ChallengesListFacebookFrndActivity.this);
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
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

    }

    private List<String> getFriendsList() {
        final List<String> friendslist = new ArrayList<String>();

        Date formate = new Date(Pref.getValue(ChallengesListFacebookFrndActivity.this, "FB_EXPIRE", ""));

        AccessToken PageAT = new AccessToken(Pref.getValue(ChallengesListFacebookFrndActivity.this, Constants.PREF_USER_FB_TOKEN, ""), Pref.getValue(ChallengesListFacebookFrndActivity.this, "FB_APPLICATION_ID", ""), Pref.getValue(ChallengesListFacebookFrndActivity.this, "FB_USER_ID", ""), permission, null, AccessTokenSource.FACEBOOK_APPLICATION_NATIVE, formate, null);
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

                        facebookFriendListAdapter = new FacebookFriendListAdapter(ChallengesListFacebookFrndActivity.this, facebookuserzeebaListModelArrayList);
                        mBinding.lvFacebookFrnd.setAdapter(facebookFriendListAdapter);

                        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ChallengesListFacebookFrndActivity.this);
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
