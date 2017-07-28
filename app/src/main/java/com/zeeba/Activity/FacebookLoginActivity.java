package com.zeeba.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
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
import com.zeeba.Activity.Dashboard.DashBoardMainActivity;
import com.zeeba.Activity.Wizard.WizardActivity;
import com.zeeba.Fragment.Wizard.Step3Fragment;
import com.zeeba.Model.FacebookuserzeebaListModel;
import com.zeeba.R;
import com.zeeba.Webservice.WebService;
import com.zeeba.databinding.ActivityFacebookLoginBinding;
import com.zeeba.utils.ConnectionDetector;
import com.zeeba.utils.Constants;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FacebookLoginActivity extends AppCompatActivity implements View.OnClickListener {

    //class object declaration..
    ActivityFacebookLoginBinding mBinding;
    CallbackManager mCallbackManager;
    ConnectionDetector cd;
    Context mContext;
    //variable declaration
    String loginAccessToken;
    String userfbid;
    String fbusername;
    String userLname;
    String fbimage;
    String fullname;
    public String TAG = "FacebookLoginActivity";
    ArrayList<FacebookuserzeebaListModel> facebookuserzeebaListModelArrayList = new ArrayList<FacebookuserzeebaListModel>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        mContext=getApplicationContext();
        LoginManager.getInstance().logOut();
        mCallbackManager = CallbackManager.Factory.create();
        cd=new ConnectionDetector(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_facebook_login);

        StatusBar();

        prepareView();
        setOnActionListener();

    }

    private void prepareView() {
        mBinding.tvTitleWithFacebook.setTypeface(FontCustom.setFont(FacebookLoginActivity.this));
        mBinding.tvSkip.setTypeface(FontCustom.setFont(FacebookLoginActivity.this));

    }

    private void setOnActionListener() {
        mBinding.btnFb.setOnClickListener(this);
        mBinding.tvSkip.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFb:
                facebookLogin();
                break;

            case R.id.tvSkip:
                Intent intent = new Intent(FacebookLoginActivity.this,
                        WizardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                break;

            default:
                break;
        }
    }


    public void StatusBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

    }

    private void facebookLogin() {
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        // App code
                        Log.e(TAG, "result is" + loginResult);
                        loginAccessToken = loginResult.getAccessToken().getToken();
                        Log.e(TAG, "token is" + loginAccessToken + " expire " +loginResult.getAccessToken().getExpires());
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
                                Pref.setValue(FacebookLoginActivity.this, Constants.PREF_USER_FB_ID, userfbid);
                                Pref.setValue(FacebookLoginActivity.this, Constants.PREF_USER_FB_IMAGE, fbimage);
                                Pref.setValue(FacebookLoginActivity.this, Constants.PREF_USER_FB_NAME, fbusername + " " + userLname);
                                Pref.setValue(FacebookLoginActivity.this, Constants.PREF_USER_FB_TOKEN, loginAccessToken);
                                Pref.setValue(FacebookLoginActivity.this,"FB_APPLICATION_ID", loginResult.getAccessToken().getApplicationId());
                                Pref.setValue(FacebookLoginActivity.this, "FB_USER_ID", loginResult.getAccessToken().getUserId());
                                Pref.setValue(FacebookLoginActivity.this, "FB_EXPIRE", ""+loginResult.getAccessToken().getExpires());
                                getAllFriendListofFB();
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

                        /*Intent mIntent = new Intent(MainActivity.this, DashboardActivity.class);
                        startActivity(mIntent);*/
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.e(TAG, "onCancel is" );
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.e(TAG, "exception is "  + exception);
                    }
                });

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
    }


    private List<String> getAllFriendListofFB() {
        final List<String> friendslist = new ArrayList<String>();

        GraphRequest request = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
            @Override
            public void onCompleted(JSONArray jsonArray, GraphResponse graphResponse) {
                Log.e("AllFriend","%%%% " + jsonArray.length() + " respo " + graphResponse.toString());
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email,name");
        request.setParameters(parameters);
        request.executeAsync();

        return friendslist;
    }


    private void callFacebookApi(String fbId) {
        if(cd.isConnectingToInternet()) {

            new ExecuteTaskforFBLoginAsync().execute(fbId,"android",Pref.getValue(FacebookLoginActivity.this,"refreshedToken",""));
        }else
        {
            cd.showToast(this, R.string.NO_INTERNET_CONNECTION);
        }

    }


    /**
     * for facebook call api..
     */
    class ExecuteTaskforFBLoginAsync extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(FacebookLoginActivity.this);
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

                Log.e(TAG,"result " + result);
                JSONObject json;
               json=new JSONObject(result.toString());

                if(json.optString("code").equals("200")){
                    Log.e(TAG,"$$$$ " + json.toString());
                    WebService.dismissProgress();
                    if(json.optString("fb_flag").equals("0")){
                        Intent intent = new Intent(FacebookLoginActivity.this,
                                WizardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        finish();
                    }else{
                        Pref.setValue(FacebookLoginActivity.this,"token",json.optString("token"));
                        Intent intent = new Intent(FacebookLoginActivity.this, DashBoardMainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                else{
                    Toast.makeText(mContext, ""+json.optString("msg"), Toast.LENGTH_SHORT).show();
                    WebService.dismissProgress();
                }





            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                        String ptofile="http://graph.facebook.com/" + dataObject.optString("id") + "/picture?type=large";
                        Log.e(TAG,"Photos " + ptofile);
                        Gson gson=new Gson();

                        FacebookuserzeebaListModel facebookuserzeebaListModel=gson.fromJson(dataObject.toString(),FacebookuserzeebaListModel.class);
                        facebookuserzeebaListModelArrayList.add(facebookuserzeebaListModel);


                        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(FacebookLoginActivity.this);
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //accessTokenTracker.stopTracking();
        //profileTracker.stopTracking();
    }
}
