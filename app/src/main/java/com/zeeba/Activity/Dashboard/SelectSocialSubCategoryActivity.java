package com.zeeba.Activity.Dashboard;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.zeeba.Activity.BaseActivity;
import com.zeeba.Activity.MainActivity;
import com.zeeba.Adapter.SocialSubCategoryAdapter;
import com.zeeba.Model.Category;
import com.zeeba.Model.Questions;
import com.zeeba.Model.Questions_Model;
import com.zeeba.Model.SubCategory;
import com.zeeba.R;
import com.zeeba.Webservice.WebService;
import com.zeeba.utils.Constants;
import com.zeeba.utils.HeaderGridView;
import com.zeeba.utils.Pref;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import io.realm.Realm;
import io.realm.RealmResults;
import static com.zeeba.Activity.MainActivity.realm;

public class SelectSocialSubCategoryActivity extends BaseActivity {

    /**
     * DEclare all views
     */
    private TextView headerText;
    private HeaderGridView listView;
    private View headerView;
    private View headerSpace;
    private ImageView header_image_view;
    private ImageView img_left;
    RelativeLayout rl_challnges_fb_btm;
    /**
     * Declare arraylist
     */
    ArrayList<SubCategory> categoryArrayList = new ArrayList<>();

    /**
     *  Declare variable to check remaining days,hours
     */
    long elapsedDays;
    long elapsedHours;
    /**
     * Intialize flag to set expiration
     */
    int check_expiration=0;
    /**
     *  Intialize variable to set database size
     */
    int db_size=0;
    AdView adView;
    RelativeLayout rel_1;
    FrameLayout fm_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category1);
        /**
         * Intialize all views
         */
        listView = (HeaderGridView) findViewById(R.id.list_view);
        headerView = findViewById(R.id.header_image_view);
        headerText = (TextView) findViewById(R.id.txt_heading);
        header_image_view = (ImageView) findViewById(R.id.header_image_view);
        rel_1 = (RelativeLayout)findViewById(R.id.rel_1);
        img_left = (ImageView) findViewById(R.id.imgleft);
        adView=(AdView)findViewById(R.id.adView);
        fm_1 = (FrameLayout)findViewById(R.id.fm_1);
        rl_challnges_fb_btm=(RelativeLayout)findViewById(R.id.rl_challnges_fb_btm);
        header_image_view.setClickable(false);
        rl_challnges_fb_btm.setVisibility(View.GONE);
        if(Pref.getValue(SelectSocialSubCategoryActivity.this,"add_display","").equalsIgnoreCase("0"))
        {
           /* RelativeLayout.LayoutParams ps= (RelativeLayout.LayoutParams)fm_1.getLayoutParams();
            ps.setMargins(0,0,0,0);
            fm_1.setLayoutParams(ps);*/
            adView.setVisibility(View.GONE);
        }else
        {
         /*   RelativeLayout.LayoutParams ps= (RelativeLayout.LayoutParams)fm_1.getLayoutParams();
            ps.setMargins(0,0,0,180);
            fm_1.setLayoutParams(ps);*/
            adView.setVisibility(View.VISIBLE);
        }
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        /**
         * Provide animation effect of header
         */
        setListViewHeader();
        /**
         * set header
         */
        getdata_deomdb();
        /**
         * set adapter of subcategory
         */
        setadapterdata();



        img_left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return false;
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //  getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }
        // Handle list View scroll events
        listView.setOnScrollListener(onScrollListener());
    }

    public void getdata_deomdb() {
        if(realm!=null) {
            final RealmResults<Category> category = realm.where(Category.class).equalTo("id", Pref.getValue(SelectSocialSubCategoryActivity.this, "cate_id", "")).findAll();

            Log.v("category_size", category.size() + "");
            Pref.setValue(SelectSocialSubCategoryActivity.this, "ques_name", category.get(0).getName());
            headerText.setText(category.get(0).getName());
            Glide.with(SelectSocialSubCategoryActivity.this)
                    .load(category.get(0).getBackground())
                    .into(header_image_view);
            Pref.setValue(SelectSocialSubCategoryActivity.this, "foreground_img", category.get(0).getForeground());
        }

    }
    public void setadapterdata()
    {
        headerText.setText(Pref.getValue(SelectSocialSubCategoryActivity.this, "cate_name",""));

        for(int i=0;i<MainActivity.category_sub.size();i++)
        {
            if(MainActivity.category_sub.get(i).getId().equals(Pref.getValue(SelectSocialSubCategoryActivity.this, "cate_id","")))
            {
                categoryArrayList.add(MainActivity.category_sub.get(i));
            }

        }

        SocialSubCategoryAdapter adapter=new SocialSubCategoryAdapter(SelectSocialSubCategoryActivity.this,categoryArrayList);
        listView.setAdapter(adapter);
    }


    private void setListViewHeader() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listHeader = inflater.inflate(R.layout.header_layout1, null, false);
        headerSpace = listHeader.findViewById(R.id.header_space);

        listView.addHeaderView(listHeader);
    }



    private AbsListView.OnScrollListener onScrollListener () {
        return new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                // Check if the first item is already reached to top
                if (listView.getFirstVisiblePosition() == 0) {
                    View firstChild = listView.getChildAt(0);
                    int topY = 0;
                    if (firstChild != null) {
                        topY = firstChild.getTop();

                    }

                    int headerTopY = headerSpace.getTop();

                    headerText.setY(Math.max(0, headerTopY + topY));
                    headerText.setBackgroundColor(Color.parseColor("#00000000"));

                    // Set the image to scroll half of the amount that of HeaderGridView
                    headerView.setY(topY * 0.7f);
                }
                else {
                    headerText.setBackgroundColor(Color.parseColor("#ec1078"));
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        Pref.setValue(SelectSocialSubCategoryActivity.this,"set_click","0");

        if(Pref.getValue(SelectSocialSubCategoryActivity.this,"start_over","").equals("1"))
        {
            Pref.setValue(SelectSocialSubCategoryActivity.this,"start_over","1");
            finish();
        }
    }
}


