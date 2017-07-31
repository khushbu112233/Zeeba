package com.zeeba.Activity.Dashboard;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
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
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.AdRequest;
import com.zeeba.Model.AnswerImage;
import com.zeeba.Model.Category;
import com.zeeba.Model.Questions;
import com.zeeba.Model.SubCategory;
import com.zeeba.R;
import com.zeeba.Webservice.WebService;
import com.zeeba.databinding.ActivityDashboardBinding;
import com.zeeba.fragment.MyChallengersAcceptedFacebookListFragment;
import com.zeeba.utils.ConnectionDetector;
import com.zeeba.utils.Constants;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;
import com.zeeba.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.zeeba.Activity.MainActivity.category_sub;
import static com.zeeba.Activity.MainActivity.realm;


public class DashboardFragment_new extends Fragment {
    /**
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
    int total_outer_count = 0;
    /**
     * total row count
     */
    int tot_row_count = 0;
    /**
     * Intitialize variable to identify total no of category odd or even
     */
    int tot_cate_count_type = 0;
    /**
     * Intialize total no of category
     */
    int tot_no_data = 0;
    /**
     * Intialize count which defines category index
     */
    int count = 0;
    /**
     * Intialize stop to break from inner & outer loop
     */
    int stop = 1;
    /**
     * Declare objects
     */
    View rootView;
    Context context;
    ConnectionDetector cd;
    int is_click = 0;
    ArrayList<Category> categoryArrayList = new ArrayList<>();
    ArrayList<String> cate_id_array_list = new ArrayList<>();
    ArrayList<String> cate_id_array_list_db = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.activity_dashboard, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();
        cd = new ConnectionDetector(context);
        if (Pref.getValue(context, "add_display", "").equalsIgnoreCase("0")) {
            mBinding.adView.setVisibility(View.GONE);
            /*RelativeLayout.LayoutParams ps= (RelativeLayout.LayoutParams) mBinding.llMain.getLayoutParams();
            ps.setMargins(0,0,0,0);
            mBinding.llMain.setLayoutParams(ps);*/
        } else {
            /*RelativeLayout.LayoutParams ps= (RelativeLayout.LayoutParams) mBinding.llMain.getLayoutParams();
            ps.setMargins(0,0,0,60);
            mBinding.llMain.setLayoutParams(ps);*/
            mBinding.adView.setVisibility(View.VISIBLE);
        }
        AdRequest adRequest = new AdRequest.Builder().build();
        mBinding.adView.loadAd(adRequest);
        mBinding.txtSelect.setTypeface(FontCustom.setFontBold(context));
        if (Pref.getValue(getActivity(), "facebook_request", "").equals("1")) {
            mBinding.txtSelect.setText("Select A Category To Challenge" + "\n" + Pref.getValue(getActivity(), "send_request_user_facebook_name", ""));
            ((DashBoardMainActivity) getActivity()).mBinding.imgLeftIcon.setVisibility(View.VISIBLE);
            ((DashBoardMainActivity) getActivity()).mBinding.drawerIcon.setVisibility(View.GONE);
            ((DashBoardMainActivity) getActivity()).mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            ((DashBoardMainActivity) getActivity()).mBinding.imgLeftIcon.setVisibility(View.GONE);
            ((DashBoardMainActivity) getActivity()).mBinding.drawerIcon.setVisibility(View.VISIBLE);
            mBinding.txtSelect.setText("Select A Category");
            ((DashBoardMainActivity) getActivity()).mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }

        ((DashBoardMainActivity) getActivity()).mBinding.imgLeftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        Pref.setValue(context, "cate_deleted", "no");

        if (Pref.getValue(context, "profile_set_up", "").equals("no")) {
            FacebookSdk.sdkInitialize(context);
            AppEventsLogger.activateApp(context);
            AppEventsLogger logger = AppEventsLogger.newLogger(context);
            logger.logEvent("Profile setup");
            AppEventsLogger.deactivateApp(context);
            Pref.setValue(context, "profile_set_up", "yes");
        }
        /**
         * Call Api
         */
       /* if(Pref.getValue(context,"last_sync","").equals(""))
        {*/
        if (cd.isConnectingToInternet()) {
            new ExecuteTask_category_list().execute(Pref.getValue(context, "token", ""), "0");
        } else {
            cd.showToast(context, R.string.NO_INTERNET_CONNECTION);
        }

       /* }
        else {
            if(cd.isConnectingToInternet()) {
                new ExecuteTask_category_list().execute(Pref.getValue(context,"token",""),Pref.getValue(context,"last_sync",""));
            }else
            {
                cd.showToast(context, R.string.NO_INTERNET_CONNECTION);
            }

        }*/

        Log.v("on_resume_call", "oncreate---------------");
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        is_click = 0;

       // ((DashBoardMainActivity) getActivity()).setdrawer();

        Log.v("on_resume_call", "call---------------");
        ((DashBoardMainActivity) getActivity()).mBinding.imgRefreshData.setVisibility(View.GONE);
        if (Pref.getValue(context, "facebook_request", "").equals("0") && Pref.getValue(context, "reload_data", "").equals("0")) {

            MyChallengersAcceptedFacebookListFragment fragment_remove = new MyChallengersAcceptedFacebookListFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment_remove).commit();
        }

        if (Pref.getValue(context, "cate_deleted", "").equals("yes")) {
            Log.v("on_resume_call", "222---------------");
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    final RealmResults<Category> cate_del = realm.where(Category.class)
                            .equalTo("id", Pref.getValue(context, "cate_id", ""))
                            .findAll();
                    final RealmResults<Questions> questions_del = realm.where(Questions.class)
                            .equalTo("id", Pref.getValue(context, "cate_id", ""))
                            .findAll();

                    final RealmResults<AnswerImage> ans_img = realm.where(AnswerImage.class)
                            .equalTo("category_id", Pref.getValue(context, "cate_id", ""))

                            .findAll();
                    if (ans_img.size() > 0) {
                        ans_img.get(0).deleteFromRealm();
                    }
                    if (questions_del.size() > 0) {
                        questions_del.get(0).deleteFromRealm();
                    }
                    if (cate_del.size() > 0) {
                        cate_del.get(0).deleteFromRealm();
                    }
                }
            });
            /**
             * Intialize outer count which define how many times 5 views repeted
             */
            total_outer_count = 0;
            /**
             * Intialize total no of category
             */
            tot_no_data = 0;
            /**
             * Intialize count which defines category index
             */
            count = 0;
            /**
             *  Intialize stop to break from inner & outer loop
             */
            stop = 1;
            mBinding.mainLayout.removeAllViews();
            getdata_deomdb();
        }

        /*if (Pref.getValue(context, "cate_deleted", "").equals("yes")) {

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    final RealmResults<Category> cate_del = realm.where(Category.class)
                            .equalTo("id", Pref.getValue(context, "cate_id", ""))
                            .findAll();
                    final RealmResults<Questions> questions_del = realm.where(Questions.class)
                            .equalTo("id", Pref.getValue(context, "cate_id", ""))
                            .findAll();

                    final RealmResults<AnswerImage> ans_img = realm.where(AnswerImage.class)
                            .equalTo("category_id", Pref.getValue(context, "cate_id", ""))

                            .findAll();
                    if (ans_img.size() > 0) {
                        ans_img.get(0).deleteFromRealm();
                    }
                    if (questions_del.size() > 0) {
                        questions_del.get(0).deleteFromRealm();
                    }
                    if (cate_del.size() > 0) {
                        cate_del.get(0).deleteFromRealm();
                    }
                }
            });
            if (Pref.getValue(context, "facebook_request", "").equalsIgnoreCase("0")) {

                total_outer_count = 0;

                tot_no_data = 0;

                count = 0;

                stop = 1;
                if (cd.isConnectingToInternet()) {
                    new ExecuteTask_category_list().execute(Pref.getValue(context, "token", ""), "0");
                } else {
                    cd.showToast(context, R.string.NO_INTERNET_CONNECTION);
                }

                *//*mBinding.mainLayout.removeAllViews();
                getdata_deomdb();*//*
            }
        }*/
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
                Log.v("resultlogin", result + "--");
                final JSONObject jsonObject;
                jsonObject = new JSONObject(result);
                if (jsonObject.getString("code").equals("200")) {
                    final JSONArray json = jsonObject.getJSONArray("data");
                    Long tsLong = System.currentTimeMillis();
                    String ts = tsLong.toString();
                    Pref.setValue(context, "last_sync", ts);
                    /**
                     * Store all category in realm database
                     */
                    category_sub.clear();
                    cate_id_array_list_db.clear();
                    cate_id_array_list.clear();
                    categoryArrayList.clear();
                    Category[] categories = new Category[json.length()];
                    for (int i = 0; i < json.length(); i++) {
                        categories[i] = new Category();
                        categories[i].setId(json.getJSONObject(i).getString("id"));
                        categories[i].setName(json.getJSONObject(i).getString("name"));
                        categories[i].setBackground(json.getJSONObject(i).getString("background"));
                        categories[i].setForeground(json.getJSONObject(i).getString("foreground"));
                        categories[i].setPage_flag(json.getJSONObject(i).getString("page_flag"));
                        categoryArrayList.add(categories[i]);
                        cate_id_array_list.add(json.getJSONObject(i).getString("id"));
                        if (json.getJSONObject(i).getString("page_flag").equals("1")) {
                            SubCategory[] subCategories = new SubCategory[json.getJSONObject(i).getJSONArray("subcategory").length()];

                            Log.v("length", subCategories.length + "");

                            for (int j = 0; j < subCategories.length; j++) {
                                subCategories[j] = new SubCategory();
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
                } else if (jsonObject.getString("code").equals("1000")) {
                    Utils.exitApplication(getActivity());
                }
                /**
                 * store category in db
                 */
                if (realm != null) {
                    store_category_in_db();

                    /**
                     *  Get all category value from realm database
                     */
                    getdata_deomdb();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void store_category_in_db() {
        final RealmResults<Category> categories = realm.where(Category.class)
                .findAll();
        /**
         *  when no data in database at that time if condition execute and store all data to database from arraylist
         */
        if (categories.size() == 0) {

            if (categoryArrayList.size() > 0) {
                for (int i = 0; i < categoryArrayList.size(); i++) {

                    final int finalI = i;
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            // Add a person
                            Category category = realm.createObject(Category.class);

                            category.setId(categoryArrayList.get(finalI).getId());
                            category.setName(categoryArrayList.get(finalI).getName());
                            category.setBackground(categoryArrayList.get(finalI).getBackground());
                            category.setForeground(categoryArrayList.get(finalI).getForeground());
                            category.setPage_flag(categoryArrayList.get(finalI).getPage_flag());
                        }
                    });
                }
            }
        }
        /**
         * when data already in database at that time else call
         */
        else {


            /**
             *  By using this for loop remove all data from database which is not found in arraylist
             */

            for (int i = 0; i < categories.size(); i++) {

                if ((cate_id_array_list.contains(categories.get(i).getId()))) {

                } else {
                    final int finalI = i;
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            final RealmResults<Category> cate_del = realm.where(Category.class)
                                    .equalTo("id", categories.get(finalI).getId())
                                    .findAll();
                            final RealmResults<Questions> questions_del = realm.where(Questions.class)
                                    .equalTo("id", categories.get(finalI).getId())
                                    .findAll();

                            final RealmResults<AnswerImage> ans_img = realm.where(AnswerImage.class)
                                    .equalTo("category_id", categories.get(finalI).getId())

                                    .findAll();
                            if (ans_img.size() > 0) {
                                ans_img.get(0).deleteFromRealm();
                            }
                            if (questions_del.size() > 0) {
                                questions_del.get(0).deleteFromRealm();
                            }
                            if (cate_del.size() > 0) {
                                cate_del.get(0).deleteFromRealm();
                            }
                        }
                    });
                }
            }

            /**
             * Store all qid from db to question_id_list_db arraylist
             */

            for (int k = 0; k < categories.size(); k++) {
                cate_id_array_list_db.add(categories.get(k).getId());
            }
            /**
             *  By using this for loop store all new data from arraylist to database
             */

            for (int i = 0; i < categoryArrayList.size(); i++) {
                if ((cate_id_array_list_db.contains(categoryArrayList.get(i).getId()))) {

                } else {
                    final int finalI = i;
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Category category = realm.createObject(Category.class);
                            category.setId(categoryArrayList.get(finalI).getId());
                            category.setName(categoryArrayList.get(finalI).getName());
                            category.setBackground(categoryArrayList.get(finalI).getBackground());
                            category.setForeground(categoryArrayList.get(finalI).getForeground());
                            category.setPage_flag(categoryArrayList.get(finalI).getPage_flag());
                        }
                    });
                }
            }

            /**
             *  Update Category
             */
            final RealmResults<Category> categories_data = realm.where(Category.class)
                    .findAll();

            for (int i = 0; i < categoryArrayList.size(); i++) {
                for (int j = 0; j < categories_data.size(); j++) {
                    if (categoryArrayList.get(i).getId().equals(categories_data.get(j).getId())) {
                        final int finalI = i;
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                final RealmResults<Category> cate_update = realm.where(Category.class)
                                        .equalTo("id", categoryArrayList.get(finalI).getId())
                                        .findAll();
                                if (cate_update.size() > 0) {
                                    cate_update.get(0).setId(categoryArrayList.get(finalI).getId());
                                    cate_update.get(0).setName(categoryArrayList.get(finalI).getName());
                                    cate_update.get(0).setBackground(categoryArrayList.get(finalI).getBackground());
                                    cate_update.get(0).setForeground(categoryArrayList.get(finalI).getForeground());
                                    cate_update.get(0).setPage_flag(categoryArrayList.get(finalI).getPage_flag());
                                }
                            }
                        });
                    }
                }
            }


        }
    }

    public void getdata_deomdb() {
        final RealmResults<Category> category = realm.where(Category.class).findAll();
        /**
         *  store all category from db to arraylist
         */

        categoryArrayList.clear();
        Category[] categories = new Category[category.size()];
        for (int i = 0; i < category.size(); i++) {
            categories[i] = new Category();
            categories[i].setId(category.get(i).getId());
            categories[i].setName(category.get(i).getName());
            categories[i].setBackground(category.get(i).getBackground());
            categories[i].setForeground(category.get(i).getForeground());
            categories[i].setPage_flag(category.get(i).getPage_flag());
            if (Pref.getValue(getActivity(), "facebook_request", "").equals("1")) {
                if (i >= 1) {
                    Log.e("DashFragment", "iiii " + i + " name " + categories[i].getName());
                    categoryArrayList.add(categories[i]);
                    Log.e("DashFragment", "cat size " + categoryArrayList.size());
                }
                Log.e("DashFragment", "facebook_request");
            } else {
                Log.e("DashFragment", "else1111");
                categoryArrayList.add(categories[i]);
            }
        }

        tot_no_data = categoryArrayList.size();
        Log.v("tot_no_data", tot_no_data + "--");

        /**
         *  Get value of outer count
         */

        /*if(category.size()%5==0)
        {
            total_outer_count=(category.size()/5);
        }
        else {
            total_outer_count=(category.size()/5)+1;
        }*/
        total_outer_count = 1;

        if (categoryArrayList.size() % 2 == 0) {
            tot_row_count = (categoryArrayList.size() / 2);
            tot_cate_count_type = 1;
        } else {
            tot_row_count = (categoryArrayList.size() / 2) + 1;
            tot_cate_count_type = 0;
        }

        /**
         * Intialize size of layout array
         */
        layout = new LinearLayout[tot_no_data][tot_no_data];
        imag_topic = new ImageView[tot_no_data][tot_no_data][tot_no_data];
        frame_topic = new FrameLayout[tot_no_data][tot_no_data][tot_no_data];
        text_topic = new TextView[tot_no_data][tot_no_data][tot_no_data];
        text_cid = new TextView[tot_no_data][tot_no_data][tot_no_data];
        text_page_flag = new TextView[tot_no_data][tot_no_data][tot_no_data];
        icon_topic = new ImageView[tot_no_data][tot_no_data][tot_no_data];
        /**
         * create view dynamically
         */
        load_data_new();

    }

    @TargetApi(Build.VERSION_CODES.M)
    public void load_data() {
        final float inPixels = getActivity().getResources().getDimension(R.dimen.category_size);
        for (int outer_count = 0; outer_count < total_outer_count; outer_count++) {
            if (stop == 0) {
                break;
            }
            for (int row_count = 0; row_count < 3; row_count++) {
                layout[outer_count][row_count] = new LinearLayout(context);
                layout[outer_count][row_count].setOrientation(LinearLayout.HORIZONTAL);
                layout[outer_count][row_count].setWeightSum(1);

                LinearLayout.LayoutParams mainParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) inPixels, 1f);
                mainParam.gravity = Gravity.CENTER;
                layout[outer_count][row_count].setLayoutParams(mainParam);
                if (stop == 0) {
                    break;
                }

                if (row_count < 2) {
                    for (int col_count = 0; col_count < 2; col_count++) {
                        Log.v("counttwo", count + " ");
                        LinearLayout.LayoutParams childParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) inPixels, 0.5f);
                        frame_topic[outer_count][row_count][col_count] = new FrameLayout(context);
                        imag_topic[outer_count][row_count][col_count] = new ImageView(context);
                        text_topic[outer_count][row_count][col_count] = new TextView(context);
                        text_cid[outer_count][row_count][col_count] = new TextView(context);
                        icon_topic[outer_count][row_count][col_count] = new ImageView(context);
                        text_page_flag[outer_count][row_count][col_count] = new TextView(context);
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
                                        Log.v("glide", "onLoadStarted");
                                    }

                                    @Override
                                    public void onLoadFailed(Exception e, Drawable errorDrawable) {

                                        Log.v("glide", "onLoadFailed");
                                    }

                                    @Override
                                    public void onResourceReady(final Bitmap icon1, GlideAnimation<? super Bitmap> glideAnimation) {
                                        Log.v("glide", "onResourceReady");
                                        //  Bitmap blur_bitmap = BlurBuilder.blur(getActivity(), icon1);
                                        imag_topic[finalOuter_count1][finalRow_count1][finalCol_count1].setImageBitmap(icon1);
                                    }

                                    @Override
                                    public void onLoadCleared(Drawable placeholder) {
                                        Log.v("glide", "onLoadCleared");
                                    }

                                    @Override
                                    public void setRequest(Request request) {
                                        Log.v("glide", "setRequest");
                                    }

                                    @Override
                                    public Request getRequest() {
                                        Log.v("glide", "getRequest");
                                        return null;
                                    }

                                    @Override
                                    public void onStart() {
                                        Log.v("glide", "onStart");
                                    }

                                    @Override
                                    public void onStop() {
                                        Log.v("glide", "onStop");
                                    }

                                    @Override
                                    public void onDestroy() {
                                        Log.v("glide", "onDestroy");
                                    }
                                });
                        imag_topic[outer_count][row_count][col_count].setScaleType(ImageView.ScaleType.FIT_XY);
                        childParam.setMargins(10, 10, 10, 10);
                        frame_topic[outer_count][row_count][col_count].setLayoutParams(childParam);
                        text_topic[outer_count][row_count][col_count].setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                        text_topic[outer_count][row_count][col_count].setTextSize(20);
                        text_topic[outer_count][row_count][col_count].setTypeface(FontCustom.setFontcontent(context));
                        text_topic[outer_count][row_count][col_count].setPadding(15, 15, 15, 15);
                        imag_topic[outer_count][row_count][col_count].setPadding(2, 2, 2, 2);
                        imag_topic[outer_count][row_count][col_count].setBackgroundResource(R.drawable.img_bg);

                        // icon_topic[outer_count][row_count][col_count].setForegroundGravity(Gravity.CENTER);
                        frame_topic[outer_count][row_count][col_count].addView(imag_topic[outer_count][row_count][col_count]);
                        frame_topic[outer_count][row_count][col_count].addView(text_topic[outer_count][row_count][col_count]);
                        frame_topic[outer_count][row_count][col_count].addView(icon_topic[outer_count][row_count][col_count]);
                        // frame_topic[outer_count][row_count][col_count].setForegroundGravity(Gravity.CENTER);
                        layout[outer_count][row_count].addView(frame_topic[outer_count][row_count][col_count]);
                        final int finalOuter_count = outer_count;
                        final int finalRow_count = row_count;
                        final int finalCol_count = col_count;
                        frame_topic[outer_count][row_count][col_count].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (is_click == 0) {
                                    /**
                                     *  if page flag ==1 -> Display 4 option
                                     *  else Display 2 option
                                     */
                                    if (text_page_flag[finalOuter_count][finalRow_count][finalCol_count].getText().toString().equals("1")) {
                                        Pref.setValue(context, "start_over", "0");
                                        Pref.setValue(context, "cate_deleted", "no");
                                        Pref.setValue(context, "cate_name", text_topic[finalOuter_count][finalRow_count][finalCol_count].getText().toString());
                                        Pref.setValue(context, "cate_id", text_cid[finalOuter_count][finalRow_count][finalCol_count].getText().toString());
                                        Intent intent = new Intent(context, SelectSocialSubCategoryActivity.class);
                                        startActivity(intent);

                                    } else {
                                        Pref.setValue(context, "start_over", "0");
                                        Pref.setValue(context, "cate_deleted", "no");
                                        Pref.setValue(context, "cate_name", text_topic[finalOuter_count][finalRow_count][finalCol_count].getText().toString());
                                        Pref.setValue(context, "cate_id", text_cid[finalOuter_count][finalRow_count][finalCol_count].getText().toString());
                                        Intent intent = new Intent(context, CategoryActivity1.class);
                                        startActivity(intent);
                                    }
                                    is_click = 1;
                                }
                            }
                        });
                        if (count == tot_no_data - 1) {
                            stop = 0;
                            break;
                        }
                        count++;
                    }
                } else {
                    for (int col_count = 0; col_count < 1; col_count++) {
                        Log.v("countone", count + " ");
                        LinearLayout.LayoutParams childParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) inPixels, 0.5f);
                        frame_topic[outer_count][row_count][col_count] = new FrameLayout(context);
                        imag_topic[outer_count][row_count][col_count] = new ImageView(context);
                        text_topic[outer_count][row_count][col_count] = new TextView(context);
                        text_cid[outer_count][row_count][col_count] = new TextView(context);
                        icon_topic[outer_count][row_count][col_count] = new ImageView(context);
                        text_page_flag[outer_count][row_count][col_count] = new TextView(context);
                        text_page_flag[outer_count][row_count][col_count].setText(categoryArrayList.get(count).getPage_flag());
                        text_cid[outer_count][row_count][col_count].setText(categoryArrayList.get(count).getId());
                        text_topic[outer_count][row_count][col_count].setText(categoryArrayList.get(count).getName());
                        text_topic[outer_count][row_count][col_count].setTextColor(Color.parseColor("#ffffff"));
                        //   icon_topic[outer_count][row_count][col_count].setImageResource(R.mipmap.general_icon);
                        Glide.with(context)
                                .load(categoryArrayList.get(count).getForeground())
                                .into(icon_topic[outer_count][row_count][col_count]);
                        FrameLayout.LayoutParams icon_params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 70);
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
                                        Log.v("glide", "onLoadStarted");
                                    }

                                    @Override
                                    public void onLoadFailed(Exception e, Drawable errorDrawable) {

                                        Log.v("glide", "onLoadFailed");
                                    }

                                    @Override
                                    public void onResourceReady(final Bitmap icon1, GlideAnimation<? super Bitmap> glideAnimation) {
                                        Log.v("glide", "onResourceReady");
                                        //Bitmap blur_bitmap = BlurBuilder.blur(getActivity(), icon1);
                                        imag_topic[finalOuter_count1][finalRow_count1][finalCol_count1].setImageBitmap(icon1);
                                    }

                                    @Override
                                    public void onLoadCleared(Drawable placeholder) {
                                        Log.v("glide", "onLoadCleared");
                                    }

                                    @Override
                                    public void setRequest(Request request) {
                                        Log.v("glide", "setRequest");
                                    }

                                    @Override
                                    public Request getRequest() {
                                        Log.v("glide", "getRequest");
                                        return null;
                                    }

                                    @Override
                                    public void onStart() {
                                        Log.v("glide", "onStart");
                                    }

                                    @Override
                                    public void onStop() {
                                        Log.v("glide", "onStop");
                                    }

                                    @Override
                                    public void onDestroy() {
                                        Log.v("glide", "onDestroy");
                                    }
                                });

                        imag_topic[outer_count][row_count][col_count].setScaleType(ImageView.ScaleType.FIT_XY);
                        childParam.setMargins(10, 10, 10, 10);
                        frame_topic[outer_count][row_count][col_count].setLayoutParams(childParam);
                        text_topic[outer_count][row_count][col_count].setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                        text_topic[outer_count][row_count][col_count].setTextSize(20);
                        text_topic[outer_count][row_count][col_count].setTypeface(FontCustom.setFontcontent(context));
                        text_topic[outer_count][row_count][col_count].setPadding(15, 15, 15, 15);
                        // icon_topic[outer_count][row_count][col_count].setForegroundGravity(Gravity.CENTER);
                        imag_topic[outer_count][row_count][col_count].setPadding(2, 2, 2, 2);
                        imag_topic[outer_count][row_count][col_count].setBackgroundResource(R.drawable.img_bg);
                        frame_topic[outer_count][row_count][col_count].addView(imag_topic[outer_count][row_count][col_count]);
                        frame_topic[outer_count][row_count][col_count].addView(text_topic[outer_count][row_count][col_count]);
                        frame_topic[outer_count][row_count][col_count].addView(icon_topic[outer_count][row_count][col_count]);
                        // frame_topic[outer_count][row_count][col_count].setForegroundGravity(Gravity.CENTER);
                        layout[outer_count][row_count].addView(frame_topic[outer_count][row_count][col_count]);
                        final int finalOuter_count = outer_count;
                        final int finalRow_count = row_count;
                        final int finalCol_count = col_count;
                        frame_topic[outer_count][row_count][col_count].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (is_click == 0) {
                                    /**
                                     *  if page flag ==1 -> Display 4 option
                                     *  else Display 2 option
                                     */
                                    if (text_page_flag[finalOuter_count][finalRow_count][finalCol_count].getText().toString().equals("1")) {
                                        Pref.setValue(context, "start_over", "0");
                                        Pref.setValue(context, "cate_name", text_topic[finalOuter_count][finalRow_count][finalCol_count].getText().toString());
                                        Pref.setValue(context, "cate_id", text_cid[finalOuter_count][finalRow_count][finalCol_count].getText().toString());
                                        Intent intent = new Intent(context, SelectSocialSubCategoryActivity.class);
                                        startActivity(intent);

                                    } else {
                                        Pref.setValue(context, "start_over", "0");
                                        Pref.setValue(context, "cate_name", text_topic[finalOuter_count][finalRow_count][finalCol_count].getText().toString());
                                        Pref.setValue(context, "cate_id", text_cid[finalOuter_count][finalRow_count][finalCol_count].getText().toString());
                                        Intent intent = new Intent(context, CategoryActivity1.class);
                                        startActivity(intent);
                                    }
                                    is_click = 1;
                                }

                            }
                        });
                        if (count == tot_no_data - 1) {
                            stop = 0;
                            break;
                        }

                        count++;
                    }
                }
                LinearLayout.LayoutParams childParam1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                childParam1.setMargins(10, 4, 10, 0);
                layout[outer_count][row_count].setLayoutParams(childParam1);
                layout[outer_count][row_count].setGravity(Gravity.CENTER);
                mBinding.mainLayout.addView(layout[outer_count][row_count]);
            }
        }
    }

    public void load_data_new() {
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
                    text_page_flag[outer_count][row_count][col_count] = new TextView(context);
                    text_cid[outer_count][row_count][col_count].setText(categoryArrayList.get(count).getId());
                    text_page_flag[outer_count][row_count][col_count].setText(categoryArrayList.get(count).getPage_flag());
                    Log.e("SocialFlag", "%%%" + categoryArrayList.get(count).getPage_flag() + " ^^^  " + text_page_flag[outer_count][row_count][col_count].getText().toString());
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
                                    Log.v("glide", "onLoadStarted");
                                }

                                @Override
                                public void onLoadFailed(Exception e, Drawable errorDrawable) {

                                    Log.v("glide", "onLoadFailed");
                                }

                                @Override
                                public void onResourceReady(final Bitmap icon1, GlideAnimation<? super Bitmap> glideAnimation) {
                                    Log.v("glide", "onResourceReady");
                                    //  Bitmap blur_bitmap = BlurBuilder.blur(getActivity(), icon1);
                                    imag_topic[finalOuter_count1][finalRow_count1][finalCol_count1].setImageBitmap(icon1);
                                }

                                @Override
                                public void onLoadCleared(Drawable placeholder) {
                                    Log.v("glide", "onLoadCleared");
                                }

                                @Override
                                public void setRequest(Request request) {
                                    Log.v("glide", "setRequest");
                                }

                                @Override
                                public Request getRequest() {
                                    Log.v("glide", "getRequest");
                                    return null;
                                }

                                @Override
                                public void onStart() {
                                    Log.v("glide", "onStart");
                                }

                                @Override
                                public void onStop() {
                                    Log.v("glide", "onStop");
                                }

                                @Override
                                public void onDestroy() {
                                    Log.v("glide", "onDestroy");
                                }
                            });
                    imag_topic[outer_count][row_count][col_count].setScaleType(ImageView.ScaleType.FIT_XY);
                    childParam.setMargins(10, 10, 10, 10);
                    frame_topic[outer_count][row_count][col_count].setLayoutParams(childParam);
                    text_topic[outer_count][row_count][col_count].setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                    text_topic[outer_count][row_count][col_count].setTextSize(20);
                    text_topic[outer_count][row_count][col_count].setTypeface(FontCustom.setFontcontent(context));
                    text_topic[outer_count][row_count][col_count].setPadding(15, 15, 15, 15);
                    imag_topic[outer_count][row_count][col_count].setPadding(2, 2, 2, 2);
                    imag_topic[outer_count][row_count][col_count].setBackgroundResource(R.drawable.img_bg);

                    // icon_topic[outer_count][row_count][col_count].setForegroundGravity(Gravity.CENTER);
                    frame_topic[outer_count][row_count][col_count].addView(imag_topic[outer_count][row_count][col_count]);
                    frame_topic[outer_count][row_count][col_count].addView(text_topic[outer_count][row_count][col_count]);
                    frame_topic[outer_count][row_count][col_count].addView(icon_topic[outer_count][row_count][col_count]);
                    // frame_topic[outer_count][row_count][col_count].setForegroundGravity(Gravity.CENTER);
                    layout[outer_count][row_count].addView(frame_topic[outer_count][row_count][col_count]);
                    final int finalOuter_count = outer_count;
                    final int finalRow_count = row_count;
                    final int finalCol_count = col_count;
                    frame_topic[outer_count][row_count][col_count].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (is_click == 0) {
                                //Log.e("SocialFlag","%%%" +   text_page_flag[finalOuter_count][finalRow_count][finalCol_count].getText().toString());

                                /**
                                 *  if page flag ==1 -> Display 4 option
                                 *  else Display 2 option
                                 */
                                try {
                                    if (text_page_flag[finalOuter_count][finalRow_count][finalCol_count].getText().toString().equals("1")) {
                                        Pref.setValue(context, "start_over", "0");
                                        Pref.setValue(context, "cate_deleted", "no");
                                        Pref.setValue(context, "cate_name", text_topic[finalOuter_count][finalRow_count][finalCol_count].getText().toString());
                                        Pref.setValue(context, "cate_id", text_cid[finalOuter_count][finalRow_count][finalCol_count].getText().toString());
                                        Intent intent = new Intent(context, SelectSocialSubCategoryActivity.class);
                                        startActivity(intent);

                                    } else {
                                        Pref.setValue(context, "start_over", "0");
                                        Pref.setValue(context, "cate_deleted", "no");
                                        Pref.setValue(context, "cate_name", text_topic[finalOuter_count][finalRow_count][finalCol_count].getText().toString());
                                        Pref.setValue(context, "cate_id", text_cid[finalOuter_count][finalRow_count][finalCol_count].getText().toString());
                                        Intent intent = new Intent(context, CategoryActivity1.class);
                                        startActivity(intent);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                is_click = 1;
                            }
                        }
                    });
                    if (count == tot_no_data - 1) {
                        stop = 0;
                        break;
                    }
                    count++;
                    //  }
                } /*else {
                    for (int col_count = 0; col_count < 1; col_count++) {
                        Log.v("countone", count + " ");
                        LinearLayout.LayoutParams childParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) inPixels, 0.5f);
                        frame_topic[outer_count][row_count][col_count] = new FrameLayout(context);
                        imag_topic[outer_count][row_count][col_count] = new ImageView(context);
                        text_topic[outer_count][row_count][col_count] = new TextView(context);
                        text_cid[outer_count][row_count][col_count] = new TextView(context);
                        icon_topic[outer_count][row_count][col_count] = new ImageView(context);
                        text_page_flag[outer_count][row_count][col_count]=new TextView(context);
                        text_page_flag[outer_count][row_count][col_count].setText(categoryArrayList.get(count).getPage_flag());
                        text_cid[outer_count][row_count][col_count].setText(categoryArrayList.get(count).getId());
                        text_topic[outer_count][row_count][col_count].setText(categoryArrayList.get(count).getName());
                        text_topic[outer_count][row_count][col_count].setTextColor(Color.parseColor("#ffffff"));
                        //   icon_topic[outer_count][row_count][col_count].setImageResource(R.mipmap.general_icon);
                        Glide.with(context)
                                .load(categoryArrayList.get(count).getForeground())
                                .into(icon_topic[outer_count][row_count][col_count]);
                        FrameLayout.LayoutParams icon_params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 70);
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
                                        //Bitmap blur_bitmap = BlurBuilder.blur(getActivity(), icon1);
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
                        // icon_topic[outer_count][row_count][col_count].setForegroundGravity(Gravity.CENTER);
                        imag_topic[outer_count][row_count][col_count].setPadding(2,2,2,2);
                        imag_topic[outer_count][row_count][col_count].setBackgroundResource(R.drawable.img_bg);
                        frame_topic[outer_count][row_count][col_count].addView(imag_topic[outer_count][row_count][col_count]);
                        frame_topic[outer_count][row_count][col_count].addView(text_topic[outer_count][row_count][col_count]);
                        frame_topic[outer_count][row_count][col_count].addView(icon_topic[outer_count][row_count][col_count]);
                        // frame_topic[outer_count][row_count][col_count].setForegroundGravity(Gravity.CENTER);
                        layout[outer_count][row_count].addView(frame_topic[outer_count][row_count][col_count]);
                        final int finalOuter_count = outer_count;
                        final int finalRow_count = row_count;
                        final int finalCol_count = col_count;
                        frame_topic[outer_count][row_count][col_count].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(is_click==0) {
                                    *//**
                 *  if page flag ==1 -> Display 4 option
                 *  else Display 2 option
                 *//*
                                    if (text_page_flag[finalOuter_count][finalRow_count][finalCol_count].getText().toString().equals("1")) {
                                        Pref.setValue(context,"start_over","0");
                                        Pref.setValue(context, "cate_name", text_topic[finalOuter_count][finalRow_count][finalCol_count].getText().toString());
                                        Pref.setValue(context, "cate_id", text_cid[finalOuter_count][finalRow_count][finalCol_count].getText().toString());
                                        Intent intent = new Intent(context, SelectSocialSubCategoryActivity.class);
                                        startActivity(intent);

                                    } else {
                                        Pref.setValue(context,"start_over","0");
                                        Pref.setValue(context, "cate_name", text_topic[finalOuter_count][finalRow_count][finalCol_count].getText().toString());
                                        Pref.setValue(context, "cate_id", text_cid[finalOuter_count][finalRow_count][finalCol_count].getText().toString());
                                        Intent intent = new Intent(context, CategoryActivity1.class);
                                        startActivity(intent);
                                    }
                                    is_click=1;
                                }

                            }
                        });
                        if (count == tot_no_data-1) {
                            stop = 0;
                            break;
                        }

                        count++;
                    }
                }*/
                LinearLayout.LayoutParams childParam1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                childParam1.setMargins(10, 4, 10, 0);
                layout[outer_count][row_count].setLayoutParams(childParam1);
                layout[outer_count][row_count].setGravity(Gravity.CENTER);
                mBinding.mainLayout.addView(layout[outer_count][row_count]);
            }
        }
    }

}
