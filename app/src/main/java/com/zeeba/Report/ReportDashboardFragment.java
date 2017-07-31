package com.zeeba.Report;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.ads.AdRequest;
import com.zeeba.Activity.Dashboard.CategoryActivity1;
import com.zeeba.Activity.Dashboard.DashBoardMainActivity;
import com.zeeba.Activity.Dashboard.DashboardFragment_new;
import com.zeeba.Activity.Dashboard.SelectSocialSubCategoryActivity;
import com.zeeba.Model.AnswerImage;
import com.zeeba.Model.Category;
import com.zeeba.Model.Questions;
import com.zeeba.Model.SubCategory;
import com.zeeba.R;
import com.zeeba.Webservice.WebService;
import com.zeeba.databinding.ActivityDashboardBinding;
import com.zeeba.utils.ConnectionDetector;
import com.zeeba.utils.Constants;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.zeeba.Activity.MainActivity.category_sub;
import static com.zeeba.Activity.MainActivity.realm;

/**
 * Created by aipxperts-ubuntu-01 on 12/5/17.
 */

public class ReportDashboardFragment extends Fragment {    /**
 * Binding all views of xml
 */
ActivityDashboardBinding mBinding;
    /**
     * Declare dynamic generate layout views
     */
    LinearLayout[][] layout;
    ImageView[][][] imag_topic;
    FrameLayout[][][] frame_topic;
    TextView[][][] text_topic;
    TextView[][][] text_cid;
    TextView[][][] text_page_flag;
    ImageView[][][] icon_topic;
    /**
     * Intialize outer count which define how many times 5 views repeted
     */
    int total_outer_count=0;
    /**
     *  total row count
     */
    int tot_row_count=0;
    /**
     * Intitialize variable to identify total no of category odd or even
     */
    int tot_cate_count_type=0;
    /**
     * Intialize total no of category
     */
    int tot_no_data=0;
    /**
     * Intialize count which defines category index
     */
    int count=0;
    /**
     *  Intialize stop to break from inner & outer loop
     */
    int stop=1;
    /**
     *  Declare objects
     */
    View rootView;
    Context context;
    ConnectionDetector cd;
    int is_click=0;

    ArrayList<Category> categoryArrayList=new ArrayList<>();
    ArrayList<String> cate_id_array_list=new ArrayList<>();
    ArrayList<String> cate_id_array_list_db=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.activity_dashboard, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();
        cd=new ConnectionDetector(context);
        ((DashBoardMainActivity)getActivity()).mBinding.imgRefreshData.setVisibility(View.GONE);
        if(Pref.getValue(context,"add_display","").equalsIgnoreCase("0"))
        {
            mBinding.adView.setVisibility(View.GONE);
           /* RelativeLayout.LayoutParams ps= (RelativeLayout.LayoutParams) mBinding.llMain.getLayoutParams();
            ps.setMargins(0,0,0,0);
            mBinding.llMain.setLayoutParams(ps);*/
        }else
        {
          /*  RelativeLayout.LayoutParams ps= (RelativeLayout.LayoutParams) mBinding.llMain.getLayoutParams();
            ps.setMargins(0,0,0,180);
            mBinding.llMain.setLayoutParams(ps);*/
            mBinding.adView.setVisibility(View.VISIBLE);
        }
        AdRequest adRequest = new AdRequest.Builder().build();
        mBinding.adView.loadAd(adRequest);
        mBinding.txtSelect.setTypeface(FontCustom.setFontBold(context));
        mBinding.txtSelect.setText("Results Report");

        if(cd.isConnectingToInternet()) {
            new ExecuteTask_category_list().execute(Pref.getValue(context,"token",""),"0");
        }else
        {
            cd.showToast(context, R.string.NO_INTERNET_CONNECTION);
        }
        // Pref.setValue(context,"cate_deleted","no");

       /* if(Pref.getValue(context,"profile_set_up","").equals("no"))
        {
            FacebookSdk.sdkInitialize(context);
            AppEventsLogger.activateApp(context);
            AppEventsLogger logger = AppEventsLogger.newLogger(context);
            logger.logEvent("Profile setup");
            AppEventsLogger.deactivateApp(context);
            Pref.setValue(context,"profile_set_up","yes");
        }*/
        /**
         * Call Api
         */
       /* if(Pref.getValue(context,"last_sync","").equals(""))
        {*/
     /*   if(cd.isConnectingToInternet()) {
            new ExecuteTask_category_list().execute(Pref.getValue(context,"token",""),"0");
        }else
        {
            cd.showToast(context, R.string.NO_INTERNET_CONNECTION);
        }
*/
        //getdata_deomdb();
       /* }
        else {
            if(cd.isConnectingToInternet()) {
                new ExecuteTask_category_list().execute(Pref.getValue(context,"token",""),Pref.getValue(context,"last_sync",""));
            }else
            {
                cd.showToast(context, R.string.NO_INTERNET_CONNECTION);
            }

        }*/



        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        is_click=0;
        Log.v("on_resume_call","call---------------");

    }

    class ExecuteTask_category_list extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(context);
        }

        @Override
        protected String doInBackground(String... params) {

            String res = WebService.PostData(params, Constants.Category_param, WebService.CATEGORY);
            Log.e("res....", "" + res);

            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                WebService.dismissProgress();
                Log.v("report_dash",result+"--");
                final JSONObject jsonObject;
                jsonObject= new JSONObject(result);
                if(jsonObject.getString("code").equals("200")) {
                    final JSONArray json = jsonObject.getJSONArray("data");
                    Long tsLong = System.currentTimeMillis();
                    String ts = tsLong.toString();
                    Pref.setValue(context, "last_sync", ts);
                    /**
                     * Store all category in realm database
                     */
                    category_sub.clear();
                    // cate_id_array_list_db.clear();
                    cate_id_array_list.clear();
                    categoryArrayList.clear();
                    Category[] categories = new Category[json.length()];
                    for(int i=0;i<json.length();i++)
                    {

                        if(!json.getJSONObject(i).getString("page_flag").equals("1")) {

                            categories[i] = new Category();
                            categories[i].setId(json.getJSONObject(i).getString("id"));
                            categories[i].setName(json.getJSONObject(i).getString("name"));
                            categories[i].setBackground(json.getJSONObject(i).getString("background"));
                            categories[i].setForeground(json.getJSONObject(i).getString("foreground"));
                            categories[i].setPage_flag(json.getJSONObject(i).getString("page_flag"));
                            categoryArrayList.add(categories[i]);
                            cate_id_array_list.add(json.getJSONObject(i).getString("id"));
                        }
                        if(!json.getJSONObject(i).getString("page_flag").equals("1"))
                        {
                            SubCategory[] subCategories=new SubCategory[json.getJSONObject(i).getJSONArray("subcategory").length()];

                            Log.v("length",subCategories.length+"");

                            for(int j=0;j<subCategories.length;j++)
                            {
                                subCategories[j]=new SubCategory();
                                subCategories[j].setId(json.getJSONObject(i).getString("id"));
                                subCategories[j].setSub_id(json.getJSONObject(i).getJSONArray("subcategory").getJSONObject(j).getString("id"));
                                subCategories[j].setName(json.getJSONObject(i).getJSONArray("subcategory").getJSONObject(j).getString("name"));
                                subCategories[j].setBackground(json.getJSONObject(i).getJSONArray("subcategory").getJSONObject(j).getString("background"));
                                subCategories[j].setForeground(json.getJSONObject(i).getJSONArray("subcategory").getJSONObject(j).getString("foreground"));
                                subCategories[j].setPage_flag(json.getJSONObject(i).getJSONArray("subcategory").getJSONObject(j).getString("page_flag"));
                                category_sub.add(subCategories[j]);
                            }
                        }
                    }
                    // load_data();
                    if(categoryArrayList.size()==0)
                    {
                        Toast.makeText(context,"No data found!",Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }
                }
                /**
                 * store category in db
                 */
                //store_category_in_db();
                /**
                 *  Get all category value from realm database
                 */
                if(categoryArrayList.size()%5==0)
                {
                    total_outer_count=(categoryArrayList.size()/5);
                }
                else {
                    total_outer_count=(categoryArrayList.size()/5)+1;
                }
                total_outer_count=1;

                if(categoryArrayList.size()%2==0)
                {
                    tot_row_count=(categoryArrayList.size()/2);
                    tot_cate_count_type=1;
                }
                else {
                    tot_row_count=(categoryArrayList.size()/2)+1;
                    tot_cate_count_type=0;
                }

                  /*
                   Intialize size of layout array
                   */
                tot_no_data=categoryArrayList.size();
                layout=new LinearLayout[tot_no_data][tot_no_data];
                imag_topic=new ImageView[tot_no_data][tot_no_data][tot_no_data];
                frame_topic=new FrameLayout[tot_no_data][tot_no_data][tot_no_data];
                text_topic=new TextView[tot_no_data][tot_no_data][tot_no_data];
                text_cid=new TextView[tot_no_data][tot_no_data][tot_no_data];
                text_page_flag=new TextView[tot_no_data][tot_no_data][tot_no_data];
                icon_topic=new ImageView[tot_no_data][tot_no_data][tot_no_data];

     /* create view dynamically
     */
                load_data_new();

                //getdata_deomdb();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public void load_data_new()
    {
        final float inPixels = getActivity().getResources().getDimension(R.dimen.category_size);
        for (int outer_count = 0; outer_count < total_outer_count; outer_count++) {
            if (stop == 0) {
                break;
            }
            for (int row_count = 0; row_count < tot_row_count; row_count++) {
                layout[outer_count][row_count] = new LinearLayout(context);
                layout[outer_count][row_count].setOrientation(LinearLayout.HORIZONTAL);
                layout[outer_count][row_count].setWeightSum(1);

                LinearLayout.LayoutParams mainParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) inPixels, 1f);
                mainParam.gravity = Gravity.CENTER;
                layout[outer_count][row_count].setLayoutParams(mainParam);
                if (stop == 0) {
                    break;
                }

                //if(row_count < tot_row_count) {
                for (int col_count = 0; col_count < 2; col_count++) {
                    Log.v("counttwo", count + " ");
                    LinearLayout.LayoutParams childParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) inPixels, 0.5f);
                    frame_topic[outer_count][row_count][col_count] = new FrameLayout(context);
                    imag_topic[outer_count][row_count][col_count] = new ImageView(context);
                    text_topic[outer_count][row_count][col_count] = new TextView(context);
                    text_cid[outer_count][row_count][col_count] = new TextView(context);
                    icon_topic[outer_count][row_count][col_count] = new ImageView(context);
                    text_page_flag[outer_count][row_count][col_count]=new TextView(context);
                    text_cid[outer_count][row_count][col_count].setText(categoryArrayList.get(count).getId());
                    text_page_flag[outer_count][row_count][col_count].setText(categoryArrayList.get(count).getPage_flag());
                    text_topic[outer_count][row_count][col_count].setText(categoryArrayList.get(count).getName());
                    text_topic[outer_count][row_count][col_count].setTextColor(Color.parseColor("#ffffff"));
                    Glide.with(context)
                            .load(categoryArrayList.get(count).getForeground())
                            .into(icon_topic[outer_count][row_count][col_count]);
                    FrameLayout.LayoutParams icon_params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 70);
                    icon_params.gravity = Gravity.CENTER;
                    icon_topic[outer_count][row_count][col_count].setLayoutParams(icon_params);
                    final int finalOuter_count1 = outer_count;
                    final int finalRow_count1 = row_count;
                    final int finalCol_count1 = col_count;
                    Glide.with(context)
                            .load(categoryArrayList.get(count).getBackground())
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onLoadStarted(Drawable placeholder) {
                                    Log.v("glide","onLoadStarted");
                                }

                                @Override
                                public void onLoadFailed(Exception e, Drawable errorDrawable) {

                                    Log.v("glide","onLoadFailed");
                                }

                                @Override
                                public void onResourceReady(final Bitmap icon1, GlideAnimation<? super Bitmap> glideAnimation) {
                                    Log.v("glide","onResourceReady");
                                    //  Bitmap blur_bitmap = BlurBuilder.blur(getActivity(), icon1);
                                    imag_topic[finalOuter_count1][finalRow_count1][finalCol_count1].setImageBitmap(icon1);
                                }

                                @Override
                                public void onLoadCleared(Drawable placeholder) {
                                    Log.v("glide","onLoadCleared");
                                }
                                @Override
                                public void setRequest(Request request) {
                                    Log.v("glide","setRequest");
                                }

                                @Override
                                public Request getRequest() {
                                    Log.v("glide","getRequest");
                                    return null;
                                }

                                @Override
                                public void onStart() {
                                    Log.v("glide","onStart");
                                }

                                @Override
                                public void onStop() {
                                    Log.v("glide","onStop");
                                }

                                @Override
                                public void onDestroy() {
                                    Log.v("glide","onDestroy");
                                }
                            });
                    imag_topic[outer_count][row_count][col_count].setScaleType(ImageView.ScaleType.FIT_XY);
                    childParam.setMargins(10, 10, 10, 10);
                    frame_topic[outer_count][row_count][col_count].setLayoutParams(childParam);
                    text_topic[outer_count][row_count][col_count].setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                    text_topic[outer_count][row_count][col_count].setTextSize(20);
                    text_topic[outer_count][row_count][col_count].setTypeface(FontCustom.setFontcontent(context));
                    text_topic[outer_count][row_count][col_count].setPadding(15, 15, 15, 15);
                    imag_topic[outer_count][row_count][col_count].setPadding(2,2,2,2);
                    imag_topic[outer_count][row_count][col_count].setBackgroundResource(R.drawable.img_bg);

                    // icon_topic[outer_count][row_count][col_count].setForegroundGravity(Gravity.CENTER);
                    frame_topic[outer_count][row_count][col_count].addView(imag_topic[outer_count][row_count][col_count]);
                    frame_topic[outer_count][row_count][col_count].addView(text_topic[outer_count][row_count][col_count]);
                    frame_topic[outer_count][row_count][col_count].addView(icon_topic[outer_count][row_count][col_count]);
                    // frame_topic[outer_count][row_count][col_count].setForegroundGravity(Gravity.CENTER);


                    //kh add
                    layout[outer_count][row_count].addView(frame_topic[outer_count][row_count][col_count]);

                    final int finalOuter_count = outer_count;
                    final int finalRow_count = row_count;
                    final int finalCol_count = col_count;
                    frame_topic[outer_count][row_count][col_count].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(is_click==0) {
                                /**
                                 *  if page flag ==1 -> Display 4 option
                                 *  else Display 2 option
                                 */
                                Pref.setValue(context,"report_cat_image",categoryArrayList.get(finalCol_count).getBackground());

                                for(int image_get = 0;image_get<categoryArrayList.size();image_get++)
                                {
                                    if(categoryArrayList.get(image_get).getId().equalsIgnoreCase(text_cid[finalOuter_count][finalRow_count][finalCol_count].getText().toString()))
                                    {
                                        Pref.setValue(context, "report_cate_image", categoryArrayList.get(image_get).getBackground());
                                        Pref.setValue(context,"report_cate_fore_image",categoryArrayList.get(image_get).getForeground());
                                    }
                                }
                                Pref.setValue(context, "report_cate_name", text_topic[finalOuter_count][finalRow_count][finalCol_count].getText().toString());
                                Pref.setValue(context, "report_cate_id", text_cid[finalOuter_count][finalRow_count][finalCol_count].getText().toString());
                                Intent intent = new Intent(context, ReportCategoryActivity1.class);
                                startActivity(intent);

                                is_click=1;
                            }
                        }
                    });
                    if (count == tot_no_data-1) {
                        stop = 0;
                        break;
                    }
                    count++;
                    //  }
                }
                LinearLayout.LayoutParams childParam1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                childParam1.setMargins(10, 4, 10, 0);
                layout[outer_count][row_count].setLayoutParams(childParam1);
                layout[outer_count][row_count].setGravity(Gravity.CENTER);

                mBinding.mainLayout.addView(layout[outer_count][row_count]);
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
