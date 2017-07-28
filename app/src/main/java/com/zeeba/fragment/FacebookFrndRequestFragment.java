package com.zeeba.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.zeeba.Activity.Dashboard.DashBoardMainActivity;
import com.zeeba.Activity.FacebookLoginActivity;
import com.zeeba.Activity.Wizard.WizardActivity;
import com.zeeba.Adapter.FacebookFriendListAdapter;
import com.zeeba.Model.FacebookuserzeebaListModel;
import com.zeeba.R;
import com.zeeba.Webservice.WebService;
import com.zeeba.databinding.FragmentFacebookfrndlistBinding;
import com.zeeba.utils.ConnectionDetector;
import com.zeeba.utils.Constants;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FacebookFrndRequestFragment extends Fragment {

    FragmentFacebookfrndlistBinding mBinding;
    CallbackManager mCallbackManager;
    ConnectionDetector cd;
    Context mContext;
    //ArrayList<FacebookuserzeebaListModel> facebookuserzeebaListModelArrayList;
    ArrayList<FacebookuserzeebaListModel> facebookuserzeebaListModelArrayList = new ArrayList<FacebookuserzeebaListModel>();
    FacebookFriendListAdapter facebookFriendListAdapter;
    View rootView;

    //variable declaration
    String loginAccessToken;
    String userfbid;
    String fbusername;
    String userLname;
    String fbimage;
    String fullname;
    String TAG="FacebookFrndRequestFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getActivity());
        mCallbackManager = CallbackManager.Factory.create();
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_facebookfrndlist, container, false);
        rootView = mBinding.getRoot();

        cd=new ConnectionDetector(getActivity());
       // FacebookSdk.sdkInitialize(this.getActivity());
        mContext=getActivity();
       // LoginManager.getInstance().logOut();
        mBinding.loginButton.setReadPermissions("public_profile", "user_friends", "email");
        mBinding.loginButton.setFragment(this);



        mBinding.tvChallengeFriend.setTypeface(FontCustom.setFont(getActivity()));

        if(!Pref.getValue(getActivity(), Constants.PREF_USER_FB_ID,"").equals("")) {
            mBinding.lvFacebookFrnd.setVisibility(View.VISIBLE);
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            Gson gson = new Gson();
            if (!TextUtils.isEmpty(sharedPrefs.getString("facebookFriendList", null))) {
                String json = sharedPrefs.getString("facebookFriendList", null);
                Type type = new TypeToken<ArrayList<FacebookuserzeebaListModel>>() {
                }.getType();
                facebookuserzeebaListModelArrayList = gson.fromJson(json, type);

                facebookFriendListAdapter = new FacebookFriendListAdapter(getActivity(), facebookuserzeebaListModelArrayList);
                mBinding.lvFacebookFrnd.setAdapter(facebookFriendListAdapter);
            }
        }
        else{
            mBinding.activityFacebookLogin.setVisibility(View.VISIBLE);
            facebookLogin();
        }



        return rootView;
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
                    public void onSuccess(LoginResult loginResult) {
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
                        Log.e(TAG,"Cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.e(TAG,"error " + exception);
                    }
                });

       // LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
    }

    private void callFacebookApi(String fbId) {
        if(cd.isConnectingToInternet()) {

           // new FacebookLoginActivity.ExecuteTaskforFBLoginAsync().execute(fbId,"android",Pref.getValue(getActivity(),"refreshedToken",""));
        }else
        {
            cd.showToast(getActivity(), R.string.NO_INTERNET_CONNECTION);
        }

    }


    /**
     * for facebook call api..
     */
    /*class ExecuteTaskforFBLoginAsync extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(getActivity());
        }

        @Override
        protected String doInBackground(String... params) {

            String res = WebService.PostData(params, Constants.PARA_FACEBOOK_USER, WebService.FACEBOOK_LOGIN_REGISTER);
            Log.e("res....", "" + res);

            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                WebService.dismissProgress();
                Log.e(TAG,"result " + result);
                JSONObject json;
                json=new JSONObject(result.toString());

                if(json.optString("code").equals("200")){

                    if(json.optString("fb_flag").equals("0")){
                        Intent intent = new Intent(getActivity(),
                                WizardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        finish();
                    }else{
                        Pref.setValue(getActivity(),"token",json.optString("token"));
                        Intent intent = new Intent(getActivity(), DashBoardMainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }





            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }*/

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
                Log.e(TAG,"$$ "+ response.toString());
                try {
                    JSONObject responseObject = response.getJSONObject();
                    JSONArray dataArray = responseObject.getJSONArray("data");

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataObject = dataArray.getJSONObject(i);

                        Gson gson=new Gson();

                        FacebookuserzeebaListModel facebookuserzeebaListModel=gson.fromJson(dataObject.toString(),FacebookuserzeebaListModel.class);
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
    public void onResume() {
        super.onResume();
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
