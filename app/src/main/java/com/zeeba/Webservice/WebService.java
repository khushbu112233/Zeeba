package com.zeeba.Webservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.HttpGet;
import com.loopj.android.http.RequestParams;
import com.zeeba.R;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class WebService {

    public static ProgressDialog mProgressDialog;
    static String json_login = "";


    //live
   public static String BASE_URL ="http://zeebapro.us/api/v2/";

    public static String USER=BASE_URL+"user";
    public static String CATEGORY=BASE_URL+"category";
    public static String SUB_CATEGORY=BASE_URL+"subcategory";
    public static String RESULT=BASE_URL+"result";
    public static String RESULT_Social=BASE_URL+"socialresult";
    public static String RESULT_OF_CHALLENGES_FB_FRND=BASE_URL+"challengeresult";
    public static String REPORT_SUB_CATEGORY=BASE_URL+"report/subcategory";
    public static String REPORT_LIST_SUB_CATEGORY=BASE_URL+"report/list";
    public static String FACEBOOK_LOGIN_REGISTER=BASE_URL+"fb/user";
    public static String INVITE_FACEBOOK_FRND_PLAYING_GAME=BASE_URL+"invite";
    public static String INVITE_RESPONSE_BY_USER_ACCEPT_DENY=BASE_URL+"invite_response";
    public static String LEADER_POINT_SYSTEM_USER=BASE_URL+"report/daily/points";
    public static String LEADER_POINT_MONTHLY_SYSTEM_USER=BASE_URL+"report/mothly/points";
    public static String TOP_TEN_USERNAME=BASE_URL+"user/name";
    public static String CHALLENGER_FRIEND_FB_LIST=BASE_URL+"challanges";
    public static String NOTIFICATION_FRND_CHALLENGES_LIST=BASE_URL+"notification";
    public static String EXISTING_USER_LOGIN_WITH_FB=BASE_URL+"fb/user/create";
    public static String MY_CHALLENGES_FB_USER=BASE_URL+"my_challenges";
    public static String ACCEPTED_CHALLENGES_FB_USER=BASE_URL+"my_accepted";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.v("URL", url);
        Log.v("RequestParams", params.toString());
        client.get(url, params, responseHandler);

    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.v("URL", url);
        Log.v("RequestParams_post", params.toString());
        client.post(url, params, responseHandler);
    }

    public static void showProgress(Context context) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            dismissProgress();
        try {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mProgressDialog.show();
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setContentView(R.layout.progressdialog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismissProgress() {
        if (mProgressDialog != null) {
            try {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static String GetData(String url, String value) {
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            //   Log.e("value",""+value);
            HttpGet get = new HttpGet(url+"/"+value);
           // get.setHeader("token",value);
            HttpResponse httpResponse = httpclient.execute(get);


            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;


    }


    /**
     * for only url not pass with header with url
     * @param url
     * @param value
     * @return
     */
    public static String GetDataOnly(String url, String value) {
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            //   Log.e("value",""+value);
            HttpGet get = new HttpGet(url);
            // get.setHeader("token",value);
            HttpResponse httpResponse = httpclient.execute(get);


            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;


    }


    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public static String PostData(String[] valuse, String[] values, String url) {
        String s="";
        try
        {
            HttpClient httpClient=new DefaultHttpClient();
            cz.msebera.android.httpclient.client.methods.HttpPost httpPost=new cz.msebera.android.httpclient.client.methods.HttpPost(url);

            List<cz.msebera.android.httpclient.NameValuePair> list=new ArrayList<cz.msebera.android.httpclient.NameValuePair>();
            for (int i =0;i<valuse.length;i++) {
                list.add(new cz.msebera.android.httpclient.message.BasicNameValuePair(values[i], valuse[i]));
                Log.v("values",values[i]+"--"+valuse[i]);
            }

            httpPost.addHeader("Content-Type","application/x-www-form-urlencoded");
            httpPost.setEntity(new cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity(list));
            HttpResponse httpResponse=  httpClient.execute(httpPost);

            cz.msebera.android.httpclient.HttpEntity httpEntity=httpResponse.getEntity();
            s= readResponse(httpResponse);
            Log.e("s",""+s);
        }
        catch(Exception exception)  {}
        return s;


    }

    public static String readResponse(HttpResponse res) {
        InputStream is=null;
        String return_text="";
        try {
            is=res.getEntity().getContent();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(is));
            String line="";
            StringBuffer sb=new StringBuffer();
            while ((line=bufferedReader.readLine())!=null)
            {
                sb.append(line);
            }
            return_text=sb.toString();
        } catch (Exception e)
        {

        }
        return return_text;

    }

}
