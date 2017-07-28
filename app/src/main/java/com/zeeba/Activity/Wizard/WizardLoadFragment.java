package com.zeeba.Activity.Wizard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.zeeba.Adapter.FRagmentPagerAdapter;
import com.zeeba.Model.FacebookuserzeebaListModel;
import com.zeeba.R;
import com.zeeba.utils.LockableViewPager;

import java.util.ArrayList;

/**
 * Created by aipxperts on 22/3/17.
 */

public class WizardLoadFragment extends Fragment {
    public static LockableViewPager _mViewPager;
    private FRagmentPagerAdapter _adapter;
    public static ImageView _btn1, _btn2, _btn3;
    public static int boy_selected=0;
    public static int girl_selected=0;
    public static int other_type_selected=0;
    public static String selected_race;
    public static int selected_race_pos=-1;
    public static String selected_age;
    public static int selected_age_pos=-1;
    public WizardLoadFragment() {
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpView();
        setTab();
        onCircleButtonClick();


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_main, container, false);
    }
    private void onCircleButtonClick() {
        _btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // _btn1.setImageResource(R.mipmap.fill_circle);
             //   _mViewPager.setCurrentItem(0);
            }
        });
        _btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  _btn2.setImageResource(R.mipmap.fill_circle);
                //_mViewPager.setCurrentItem(1);
            }
        });
        _btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  _btn3.setImageResource(R.mipmap.fill_circle);
               // _mViewPager.setCurrentItem(2);
            }
        });
    }
    private void setUpView() {
        _mViewPager = (LockableViewPager) getView().findViewById(R.id.imageviewPager);
        _adapter = new FRagmentPagerAdapter(getActivity(), getFragmentManager());
        _mViewPager.setAdapter(_adapter);
        _mViewPager.setCurrentItem(0);
        _mViewPager.setSwipeable(false);

        initButton();
    }
    private void setTab() {
        _mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int position) {
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                _btn1.setImageResource(R.mipmap.holo_circle);
                _btn2.setImageResource(R.mipmap.holo_circle);
                _btn3.setImageResource(R.mipmap.holo_circle);
                btnAction(position);
            }
        });
    }
    private void btnAction(int action) {
        switch (action) {
            case 0:
                _btn1.setImageResource(R.mipmap.fill_circle);
                break;
            case 1:
                _btn2.setImageResource(R.mipmap.fill_circle);
                break;
            case 2:
                _btn3.setImageResource(R.mipmap.fill_circle);
                break;
        }
    }
    private void initButton() {
        _btn1 = (ImageView) getView().findViewById(R.id.btn1);
        _btn1.setImageResource(R.mipmap.fill_circle);
        _btn2 = (ImageView) getView().findViewById(R.id.btn2);
        _btn3 = (ImageView) getView().findViewById(R.id.btn3);
    }
    private void setButton(Button btn, String text, int h, int w) {
        btn.setWidth(w);
        btn.setHeight(h);
        btn.setText(text);
    }
}
