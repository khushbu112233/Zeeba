package com.zeeba.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zeeba.Activity.Dashboard.ChallengesListFacebookFrndActivity;
import com.zeeba.Activity.Dashboard.DashBoardMainActivity;
import com.zeeba.Activity.Dashboard.DashboardFragment_new;
import com.zeeba.Activity.FacebookLoginActivity;
import com.zeeba.Adapter.AcceptedChallegesFbFrndListAdapter;
import com.zeeba.Adapter.FacebookFriendListAdapter;
import com.zeeba.Adapter.MyChallengesFbFrndListAdapter;
import com.zeeba.Adapter.NotificationofChallengesListAdapter;
import com.zeeba.Model.AcceptedChallengesFBListModel;
import com.zeeba.Model.FacebookuserzeebaListModel;
import com.zeeba.Model.MyChallengesFBListModel;
import com.zeeba.Model.NotificationFrndChallengesListModel;
import com.zeeba.R;
import com.zeeba.Webservice.WebService;
import com.zeeba.databinding.FragmentMychallengersacceptfblistBinding;
import com.zeeba.utils.ConnectionDetector;
import com.zeeba.utils.Constants;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;
import com.zeeba.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MyChallengersAcceptedFacebookListFragment extends Fragment {

    FragmentMychallengersacceptfblistBinding mBinding;
    CallbackManager mCallbackManager;
    ConnectionDetector cd;
    Context mContext;
    //ArrayList<FacebookuserzeebaListModel> facebookuserzeebaListModelArrayList;
    ArrayList<FacebookuserzeebaListModel> facebookuserzeebaListModelArrayList = new ArrayList<FacebookuserzeebaListModel>();
    ArrayList<MyChallengesFBListModel> myChallengesFBListModelArrayList = new ArrayList<>();
    ArrayList<AcceptedChallengesFBListModel> acceptedChallengesFBListModelArrayList = new ArrayList<>();
    FacebookFriendListAdapter facebookFriendListAdapter;
    MyChallengesFbFrndListAdapter myChallengesFbFrndListAdapter;
    AcceptedChallegesFbFrndListAdapter acceptedChallegesFbFrndListAdapter;
    View rootView;

    //variable declaration
    String loginAccessToken;
    String userfbid;
    String fbusername;
    String userLname;
    String fbimage;
    String fullname;
    String TAG = "MyChallengersAcceptedFacebookListFragment";
    int clickchallenge = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getActivity());
        mCallbackManager = CallbackManager.Factory.create();
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_mychallengersacceptfblist, container, false);
        rootView = mBinding.getRoot();

        ((DashBoardMainActivity)getActivity()).mBinding.imgRefreshData.setVisibility(View.VISIBLE);



        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        cd = new ConnectionDetector(getActivity());
        // FacebookSdk.sdkInitialize(this.getActivity());
        mContext = getActivity();
        prepareView();
        setOnActionListener();
        // LoginManager.getInstance().logOut();
        mBinding.loginButton.setReadPermissions("public_profile", "user_friends", "email");
        mBinding.loginButton.setFragment(this);


        mBinding.tvChallengeFriend.setTypeface(FontCustom.setFont(getActivity()));
        mBinding.tvChallengesBlankView.setTypeface(FontCustom.setFontcontent(getActivity()));

        if (!Pref.getValue(getActivity(), Constants.PREF_USER_FB_ID, "").equals("")) {

            mBinding.lnMyChallengesList.setVisibility(View.VISIBLE);
            pulltoRefreshFuntion(0);
        } else {
            mBinding.activityFacebookLogin.setVisibility(View.VISIBLE);
            facebookLogin();
        }
        Log.e("DashMain", "Challenge");
        Pref.setValue(getActivity(), Constants.PREF_USER_FB_ID, Pref.getValue(getActivity(), Constants.PREF_USER_FB_ID, ""));

    }


    private void prepareView() {
        mBinding.btnMyChallengesList.setTypeface(FontCustom.setFontcontent(getActivity()));
        mBinding.btnAcceptedChallengesList.setTypeface(FontCustom.setFontcontent(getActivity()));
        mBinding.btnChallegesFacebookFrndList.setTypeface(FontCustom.setFontcontent(getActivity()));
        setColorbackroundView(0);
        if (!Pref.getValue(getActivity(), Constants.PREF_USER_FB_ID, "").equals("")) {
            mBinding.rlChallngesFbBtm.setVisibility(View.GONE);
            if (cd.isConnectingToInternet()) {

                new ExecuteTaskforMyChallengesAccpted().execute(Pref.getValue(getActivity(), "token", ""), Pref.getValue(getActivity(), Constants.PREF_USER_FB_ID, ""));
            } else {
                cd.showToast(getActivity(), R.string.NO_INTERNET_CONNECTION);
            }

        } else {
            Log.e(TAG, "app id null");
        }
    }

    private void pulltoRefreshFuntion(final int clickchallenge) {

        ((DashBoardMainActivity)getActivity()).mBinding.imgRefreshData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickchallenge == 0) {
                    if (cd.isConnectingToInternet()) {

                        new ExecuteTaskforMyChallengesAccpted().execute(Pref.getValue(getActivity(), "token", ""), Pref.getValue(getActivity(), Constants.PREF_USER_FB_ID, ""));
                    } else {
                        cd.showToast(getActivity(), R.string.NO_INTERNET_CONNECTION);
                    }
                } else {
                    if (cd.isConnectingToInternet()) {

                        new ExecuteTaskforAcceptedChallengesAccpted().execute(Pref.getValue(getActivity(), "token", ""), Pref.getValue(getActivity(), Constants.PREF_USER_FB_ID, ""));
                    } else {
                        cd.showToast(getActivity(), R.string.NO_INTERNET_CONNECTION);
                    }
                }
            }
        });


        mBinding.swipeContainerForChallenges.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (clickchallenge == 0) {
                    if (cd.isConnectingToInternet()) {

                        new ExecuteTaskforMyChallengesAccpted().execute(Pref.getValue(getActivity(), "token", ""), Pref.getValue(getActivity(), Constants.PREF_USER_FB_ID, ""));
                    } else {
                        cd.showToast(getActivity(), R.string.NO_INTERNET_CONNECTION);
                    }
                } else {
                    if (cd.isConnectingToInternet()) {

                        new ExecuteTaskforAcceptedChallengesAccpted().execute(Pref.getValue(getActivity(), "token", ""), Pref.getValue(getActivity(), Constants.PREF_USER_FB_ID, ""));
                    } else {
                        cd.showToast(getActivity(), R.string.NO_INTERNET_CONNECTION);
                    }
                }
            }
        });
        // Configure the refreshing colors
        mBinding.swipeContainerForChallenges.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void setOnActionListener() {
        mBinding.btnMyChallengesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setColorbackroundView(0);
                pulltoRefreshFuntion(0);
                // myChallengesFBListModelArrayList.clear();
                if (cd.isConnectingToInternet()) {

                    new ExecuteTaskforMyChallengesAccpted().execute(Pref.getValue(getActivity(), "token", ""), Pref.getValue(getActivity(), Constants.PREF_USER_FB_ID, ""));
                } else {
                    cd.showToast(getActivity(), R.string.NO_INTERNET_CONNECTION);
                }

            }
        });


        mBinding.btnAcceptedChallengesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setColorbackroundView(1);
                pulltoRefreshFuntion(1);
                //  acceptedChallengesFBListModelArrayList.clear();
                if (cd.isConnectingToInternet()) {

                    new ExecuteTaskforAcceptedChallengesAccpted().execute(Pref.getValue(getActivity(), "token", ""), Pref.getValue(getActivity(), Constants.PREF_USER_FB_ID, ""));
                } else {
                    cd.showToast(getActivity(), R.string.NO_INTERNET_CONNECTION);
                }

            }
        });

        mBinding.rlChallngesFbBtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ChallengesListFacebookFrndActivity.class));
                //getActivity().finish();
            }
        });
    }

    private void setColorbackroundView(int clickchallenge) {
        if (clickchallenge == 0) {
            mBinding.btnMyChallengesList.setTextColor(getResources().getColor(R.color.pink_latest_trasperent));
            mBinding.btnMyChallengesList.setBackground(getResources().getDrawable(R.drawable.round_background_mychallenges_white));
            mBinding.btnAcceptedChallengesList.setTextColor(getResources().getColor(R.color.white));
            mBinding.btnAcceptedChallengesList.setBackground(getResources().getDrawable(R.drawable.round_background_mychallenges_pink));
        } else {
            mBinding.btnMyChallengesList.setTextColor(getResources().getColor(R.color.white));
            mBinding.btnMyChallengesList.setBackground(getResources().getDrawable(R.drawable.round_background_mychallenges_pink));
            mBinding.btnAcceptedChallengesList.setTextColor(getResources().getColor(R.color.pink_latest_trasperent));
            mBinding.btnAcceptedChallengesList.setBackground(getResources().getDrawable(R.drawable.round_background_mychallenges_white));
        }
    }

    private void facebookLogin() {

        mBinding.btnFb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.loginButton.performClick();
            }
        });

        mBinding.loginButton.registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        // App code
                        Log.e(TAG, "result is" + loginResult);
                        loginAccessToken = loginResult.getAccessToken().getToken();
                        Log.e(TAG, "token is" + loginAccessToken);
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject jsonResult, GraphResponse response) {
                                userfbid = jsonResult.optString("id");
                                fbusername = jsonResult.optString("first_name");
                                userLname = jsonResult.optString("last_name");
                                fullname = fbusername + " " + userLname;
                                Log.e(TAG, "userfbid is" + userfbid);
                                Log.e(TAG, "fbusername is" + fbusername);
                                Log.e(TAG, "username is" + userLname);
                                fbimage = Constants.FB_IMAGE_PATH + userfbid + Constants.FB_IMAGE_SIZE;
                                Log.e(TAG, "image is" + fbimage);
                                Pref.setValue(getActivity(), Constants.PREF_USER_FB_ID, userfbid);
                                Pref.setValue(getActivity(), Constants.PREF_USER_FB_IMAGE, fbimage);
                                Pref.setValue(getActivity(), Constants.PREF_USER_FB_NAME, fbusername + " " + userLname);
                                Pref.setValue(getActivity(), Constants.PREF_USER_FB_TOKEN, loginAccessToken);
                                Pref.setValue(getActivity(), "FB_APPLICATION_ID", loginResult.getAccessToken().getApplicationId());
                                Pref.setValue(getActivity(), "FB_USER_ID", loginResult.getAccessToken().getUserId());
                                Pref.setValue(getActivity(), "FB_EXPIRE", "" + loginResult.getAccessToken().getExpires());
                                getFriendsList();
                                LoginManager.getInstance().logOut();
                                //facebookCallback.onSuccess();//interface for passing user fb details on success

                            }

                        });
                        //adding parameter and executing graph api to get user fb profile details.
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,first_name,last_name,email,name");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.e(TAG, "Cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.e(TAG, "error " + exception);
                    }
                });

        // LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
    }


    private void callFacebookApi(String fbId) {
        if (cd.isConnectingToInternet()) {

            new ExecuteTaskforExistingUserFBLoginAsync().execute(Pref.getValue(getActivity(), "token", ""), fbId, Pref.getValue(getActivity(), Constants.PREF_USER_FB_NAME, ""), "android", Pref.getValue(getActivity(), "refreshedToken", ""));
        } else {
            cd.showToast(getActivity(), R.string.NO_INTERNET_CONNECTION);
        }

    }


    /**
     * for facebook call api..
     */
    class ExecuteTaskforExistingUserFBLoginAsync extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(getActivity());
        }

        @Override
        protected String doInBackground(String... params) {

            String res = WebService.PostData(params, Constants.PARA_EXISTING_USER_LOGIN_FACEBOOK_USER, WebService.EXISTING_USER_LOGIN_WITH_FB);
            Log.e("res....", "" + res);

            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                WebService.dismissProgress();
                Log.e(TAG, "result " + result);
                JSONObject json;
                json = new JSONObject(result.toString());

                if (json.optString("code").equals("200")) {

                    Pref.setValue(getActivity(), "token", json.optString("token"));
                    Toast.makeText(mContext, "" + json.optString("msg"), Toast.LENGTH_SHORT).show();

                    if (cd.isConnectingToInternet()) {

                        new ExecuteTaskforMyChallengesAccpted().execute(Pref.getValue(getActivity(), "token", ""), Pref.getValue(getActivity(), Constants.PREF_USER_FB_ID, ""));
                    } else {
                        cd.showToast(getActivity(), R.string.NO_INTERNET_CONNECTION);
                    }


                } else if (json.optString("code").equals("100")) {
                    mBinding.activityFacebookLogin.setVisibility(View.VISIBLE);
                    mBinding.lnMyChallengesList.setVisibility(View.GONE);
                    Pref.setValue(getActivity(), Constants.PREF_USER_FB_ID, "");
                    Pref.setValue(getActivity(), Constants.PREF_USER_FB_IMAGE, "");
                    Pref.setValue(getActivity(), Constants.PREF_USER_FB_NAME, "");
                    Pref.setValue(getActivity(), Constants.PREF_USER_FB_TOKEN, "");
                    Toast.makeText(mContext, "" + json.optString("msg"), Toast.LENGTH_SHORT).show();
                } else {
                    mBinding.activityFacebookLogin.setVisibility(View.VISIBLE);
                    mBinding.lnMyChallengesList.setVisibility(View.GONE);
                    Toast.makeText(mContext, "" + json.optString("msg"), Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * for my challenges of fb frnd api..
     */
    class ExecuteTaskforMyChallengesAccpted extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(getActivity());
        }

        @Override
        protected String doInBackground(String... params) {

            String res = WebService.PostData(params, Constants.PARA_MY_CHALLENGES_FACEBOOK_USER, WebService.MY_CHALLENGES_FB_USER);
            Log.e("res....", "" + res);

            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                WebService.dismissProgress();
                myChallengesFBListModelArrayList.clear();
                mBinding.swipeContainerForChallenges.setRefreshing(false);
                Log.e(TAG, "result " + result);
                JSONObject json;
                json = new JSONObject(result.toString());

                if (json.optString("code").equals("200")) {
                    mBinding.rlChallngesFbBtm.setVisibility(View.VISIBLE);
                    JSONArray dataJsonArray = json.optJSONArray("data");
                    if (dataJsonArray.length() == 0) {
                        mBinding.swipeContainerForChallenges.setVisibility(View.GONE);
                        mBinding.tvChallengesBlankView.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.swipeContainerForChallenges.setVisibility(View.VISIBLE);
                        mBinding.tvChallengesBlankView.setVisibility(View.GONE);
                    }
                    for (int i = 0; i < dataJsonArray.length(); i++) {
                        JSONObject dataObject = dataJsonArray.getJSONObject(i);

                        Gson gson = new Gson();

                        MyChallengesFBListModel myChallengesFBListModel = gson.fromJson(dataObject.toString(), MyChallengesFBListModel.class);
                        myChallengesFBListModelArrayList.add(myChallengesFBListModel);

                    }

                    myChallengesFbFrndListAdapter = new MyChallengesFbFrndListAdapter(getActivity(), myChallengesFBListModelArrayList);
                    mBinding.lvMyChallengesAcceptedList.setAdapter(myChallengesFbFrndListAdapter);


                    mBinding.activityFacebookLogin.setVisibility(View.GONE);
                    mBinding.lnMyChallengesList.setVisibility(View.VISIBLE);


                }
                else if (json.optString("code").equals("1000")) {
                    Utils.exitApplication(getActivity());
                }

                else {
                    mBinding.activityFacebookLogin.setVisibility(View.VISIBLE);
                    mBinding.lnMyChallengesList.setVisibility(View.GONE);
                    Toast.makeText(mContext, "" + json.optString("msg"), Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * for my challenges of fb frnd api..
     */
    class ExecuteTaskforAcceptedChallengesAccpted extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(getActivity());
        }

        @Override
        protected String doInBackground(String... params) {

            String res = WebService.PostData(params, Constants.PARA_ACCEPTED_CHALLENGES_FACEBOOK_USER, WebService.ACCEPTED_CHALLENGES_FB_USER);
            Log.e("res....", "" + res);

            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                WebService.dismissProgress();
                acceptedChallengesFBListModelArrayList.clear();
                mBinding.swipeContainerForChallenges.setRefreshing(false);
                Log.e(TAG, "result " + result);
                JSONObject json;
                json = new JSONObject(result.toString());

                if (json.optString("code").equals("200")) {
                    mBinding.rlChallngesFbBtm.setVisibility(View.VISIBLE);
                    JSONArray dataJsonArray = json.optJSONArray("data");

                    if (dataJsonArray.length() == 0) {
                        mBinding.swipeContainerForChallenges.setVisibility(View.GONE);
                        mBinding.tvChallengesBlankView.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.swipeContainerForChallenges.setVisibility(View.VISIBLE);
                        mBinding.tvChallengesBlankView.setVisibility(View.GONE);
                    }
                    for (int i = 0; i < dataJsonArray.length(); i++) {
                        JSONObject dataObject = dataJsonArray.getJSONObject(i);

                        Gson gson = new Gson();

                        AcceptedChallengesFBListModel acceptedChallengesFBListModel = gson.fromJson(dataObject.toString(), AcceptedChallengesFBListModel.class);
                        acceptedChallengesFBListModelArrayList.add(acceptedChallengesFBListModel);

                    }

                    acceptedChallegesFbFrndListAdapter = new AcceptedChallegesFbFrndListAdapter(getActivity(), acceptedChallengesFBListModelArrayList);
                    mBinding.lvMyChallengesAcceptedList.setAdapter(acceptedChallegesFbFrndListAdapter);


                    mBinding.activityFacebookLogin.setVisibility(View.GONE);
                    mBinding.lnMyChallengesList.setVisibility(View.VISIBLE);


                } else if (json.optString("code").equals("1000")) {
                    Utils.exitApplication(getActivity());
                } else {
                    mBinding.activityFacebookLogin.setVisibility(View.VISIBLE);
                    mBinding.lnMyChallengesList.setVisibility(View.GONE);
                    Toast.makeText(mContext, "" + json.optString("msg"), Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private List<String> getFriendsList() {
        final List<String> friendslist = new ArrayList<String>();
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/friends", null, HttpMethod.GET, new GraphRequest.Callback() {
            public void onCompleted(GraphResponse response) {
/* handle the result */
                Log.e(TAG, "$$ " + response.toString());
                try {
                    JSONObject responseObject = response.getJSONObject();
                    JSONArray dataArray = responseObject.getJSONArray("data");

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataObject = dataArray.getJSONObject(i);

                        Gson gson = new Gson();

                        FacebookuserzeebaListModel facebookuserzeebaListModel = gson.fromJson(dataObject.toString(), FacebookuserzeebaListModel.class);
                        facebookuserzeebaListModelArrayList.add(facebookuserzeebaListModel);


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
                    callFacebookApi(userfbid);

                }
            }
        }).executeAsync();
        return friendslist;
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
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        AppEventsLogger.deactivateApp(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //accessTokenTracker.stopTracking();
        //profileTracker.stopTracking();
    }

}
