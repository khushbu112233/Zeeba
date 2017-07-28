package com.zeeba.Fragment.Wizard;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.zeeba.Activity.Dashboard.DashBoardMainActivity;
import com.zeeba.Model.FacebookuserzeebaListModel;
import com.zeeba.R;
import com.zeeba.Services.GpsTracker;
import com.zeeba.Webservice.WebService;
import com.zeeba.databinding.FragmentStep3Binding;
import com.zeeba.utils.ConnectionDetector;
import com.zeeba.utils.Constant;
import com.zeeba.utils.Constants;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.zeeba.Activity.Wizard.WizardLoadFragment._btn2;
import static com.zeeba.Activity.Wizard.WizardLoadFragment._mViewPager;
import static com.zeeba.Activity.Wizard.WizardLoadFragment.boy_selected;
import static com.zeeba.Activity.Wizard.WizardLoadFragment.girl_selected;
import static com.zeeba.Activity.Wizard.WizardLoadFragment.selected_age;
import static com.zeeba.Activity.Wizard.WizardLoadFragment.selected_age_pos;
import static com.zeeba.Activity.Wizard.WizardLoadFragment.selected_race;

/**
 * Created by aipxperts on 22/3/17.
 */

public class Step3Fragment extends Fragment {

    /**
     * Binding all xml views
     */
    FragmentStep3Binding mBinding;
    /**
     * DEclare rootview & context
     */
    View rootView;
    Context context;
    /**
     * Generate textview list runtime
     */
    TextView[] txt_age = new TextView[8];
    /**
     * Declare Arraylist
     */
    ArrayList<String> arrayList_age = new ArrayList<>();
    /**
     * DEclare objects
     */
    ConnectionDetector cd;
    Animation anim;
    /**
     * Declare variables
     */
    String gender;
    String latitude;
    String longitude;
    String fbName="";
    String fb_id="";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /**
         * Binding all xml views
         */

        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_step3, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();


        /**
         * Set typeface
         */
        mBinding.txtSelect.setTypeface(FontCustom.setFontBold(context));
        /**
         * Intialize connection
         */
        cd=new ConnectionDetector(context);
        /**
         * Set age in list
         */
        setagedata();
        /**
         * call back button
         */
        mBinding.btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _btn2.setImageResource(R.mipmap.fill_circle);
                _mViewPager.setCurrentItem(1);
            }
        });

        if(!TextUtils.isEmpty(Pref.getValue(getActivity(),Constants.PREF_USER_FB_NAME,"")) || !TextUtils.isEmpty(Pref.getValue(getActivity(),Constants.PREF_USER_FB_ID,""))){
            fbName=Pref.getValue(getActivity(),Constants.PREF_USER_FB_NAME,"");
            fb_id=Pref.getValue(getActivity(),Constants.PREF_USER_FB_ID,"");
        }


        Pref.setValue(context,"profile_set_up","no");
        /**
         * call api
         */
        mBinding.btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected_age_pos==-1) {

                    Toast.makeText(context,"Please select your age!",Toast.LENGTH_LONG).show();
                }
                else {

                    if(boy_selected==1)
                    {
                        gender="male";
                    }
                    else if (girl_selected==1){
                        gender="female";
                    }
                    else{
                        gender="Prefer not to say";
                    }
                    /**
                     * call user api
                     */

                    if(cd.isConnectingToInternet()) {

                        new ExecuteTask().execute(gender,selected_race,selected_age,latitude,longitude,fbName,fb_id,"android",Pref.getValue(getActivity(),"refreshedToken",""));
                    }else
                    {
                        cd.showToast(context, R.string.NO_INTERNET_CONNECTION);
                    }


                }
            }
        });




        return rootView;
    }


    public void setagedata()
    {
        arrayList_age.clear();
        arrayList_age.add("Under 18");
        arrayList_age.add("18-24");
        arrayList_age.add("25-34");
        arrayList_age.add("35-44");
        arrayList_age.add("45-54");
        arrayList_age.add("55-64");
        arrayList_age.add("65 or Above");
        arrayList_age.add("Prefer not to say");

        /**
         * Generate view dynamically
         */

        for(int i=0;i<arrayList_age.size();i++)
        {
            txt_age[i] = new TextView(context);
            txt_age[i].setText(arrayList_age.get(i));
            txt_age[i].setPadding(10,20,10,20);
            txt_age[i].setClickable(true);
            txt_age[i].setVisibility(View.VISIBLE);
            txt_age[i].setTextColor(getResources().getColor(R.color.white));
            txt_age[i].setGravity(Gravity.CENTER);
            txt_age[i].setTextSize(13);
            txt_age[i].setTypeface(FontCustom.setFontcontent(context));
           // txt_age[i].setAnimateType(HTextViewType.ANVIL);
            txt_age[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.  MATCH_PARENT));
          //  txt_age[i].setLayoutParams(new LinearLayout.LayoutParams(500,LinearLayout.LayoutParams.WRAP_CONTENT));
            mBinding.listLayout.addView(txt_age[i]);
        }
        for(int i=0;i<arrayList_age.size();i++)
        {
            final int finalI = i;
            txt_age[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.v("data",txt_age[finalI].getText().toString()+"--");

                    for(int j=0;j<arrayList_age.size();j++)
                    {
                        if(j==finalI)
                        {


                            selected_age_pos=finalI;
                            selected_age=txt_age[finalI].getText().toString();
                            txt_age[finalI].setTextSize(23);
                            txt_age[finalI].setBackgroundResource(R.drawable.button_bg);



                        }
                        else {
                            txt_age[j].setBackgroundColor(Color.parseColor("#00ffffff"));
                            txt_age[j].setTextSize(13);

                        }
                    }

                }
            });
        }


    }



    class ExecuteTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(context);
        }

        @Override
        protected String doInBackground(String... params) {

            String res = WebService.PostData(params, Constants.User_param, WebService.USER);
            Log.e("res....", "" + res);

            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                WebService.dismissProgress();
                Log.v("resultlogin",result+"--");
                JSONObject json;
                json= new JSONObject(result);
                String token=json.getString("token");
                Log.e("token ",token+"--");

                if(!token.equals(""))
                {
                    /**
                     * fabric analytics
                     */
                    Answers.getInstance().logCustom(new CustomEvent("Zeeba Profile setup")
                            .putCustomAttribute("Profile setup", "Yes"));
                }

                Pref.setValue(context,"token",token);
                Intent intent=new Intent(context, DashBoardMainActivity.class);
                startActivity(intent);
                ((FragmentActivity)context).finish();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
       // AppEventsLogger.deactivateApp(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();

        GpsTracker gpsTracker=new GpsTracker(getActivity());
        latitude=gpsTracker.getLatitude()+"";
        longitude=gpsTracker.getLongitude()+"";
        Log.v("my_rinal_loc",gpsTracker.getLatitude()+" "+gpsTracker.getLongitude());

        /**
         * Set previous selected value
         */
        if(arrayList_age.size()>0)
        {
            for(int j=0;j<arrayList_age.size();j++)
            {
                if(j==selected_age_pos)
                {
                    txt_age[selected_age_pos].setBackgroundResource(R.drawable.button_bg);
                    txt_age[selected_age_pos].setTextSize(23);


                }
                else {
                    txt_age[j].setBackgroundColor(Color.parseColor("#00ffffff"));
                    txt_age[j].setTextSize(13);

                }
            }
        }

    }

}
