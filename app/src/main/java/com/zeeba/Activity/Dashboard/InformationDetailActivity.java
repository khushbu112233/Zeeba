package com.zeeba.Activity.Dashboard;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.zeeba.Activity.BaseActivity;
import com.zeeba.Adapter.CustomPagerAdapter;
import com.zeeba.R;
import com.zeeba.databinding.FragmentInformation1Binding;
import com.zeeba.utils.ConnectionDetector;
import com.zeeba.utils.Pref;

public class InformationDetailActivity extends BaseActivity {


    int[] mResources = {
            R.mipmap.dashbord_screen,
            R.mipmap.sub_category_screen,
            R.mipmap.info_15_sec_img,
            R.mipmap.question_screen_2,
            R.mipmap.thank_screen,


    };


    int[] mResources_social = {
            R.mipmap.dashboard_social,
            R.mipmap.subcategory_for_social,
            R.mipmap.info_social_15_sec_img,
            R.mipmap.que_for_social_2,
            R.mipmap.thank_you_for_social,


    };
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //  getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }
        pager=(ViewPager)findViewById(R.id.view_pager);
        if( Pref.getValue(InformationDetailActivity.this,"from_social","").equals("true")) {
            CustomPagerAdapter mCustomPagerAdapter = new CustomPagerAdapter(InformationDetailActivity.this, mResources_social);
            pager.setAdapter(mCustomPagerAdapter);
        }
        else {
            CustomPagerAdapter mCustomPagerAdapter = new CustomPagerAdapter(InformationDetailActivity.this, mResources);
            pager.setAdapter(mCustomPagerAdapter);
        }
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}
