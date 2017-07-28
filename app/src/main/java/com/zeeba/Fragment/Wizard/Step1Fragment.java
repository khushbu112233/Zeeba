package com.zeeba.Fragment.Wizard;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zeeba.R;
import com.zeeba.databinding.FragmentStep1Binding;
import com.zeeba.utils.FontCustom;

import static com.zeeba.Activity.Wizard.WizardLoadFragment._btn2;
import static com.zeeba.Activity.Wizard.WizardLoadFragment._mViewPager;
import static com.zeeba.Activity.Wizard.WizardLoadFragment.boy_selected;
import static com.zeeba.Activity.Wizard.WizardLoadFragment.girl_selected;
import static com.zeeba.Activity.Wizard.WizardLoadFragment.other_type_selected;

/**
 * Created by aipxperts on 22/3/17.
 */

public class Step1Fragment extends Fragment{

    /**
     *  Binding all views
     */
    FragmentStep1Binding mBinding;
    /**
     * DEclare Objects
     */
    View rootView;
    Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_step1, container, false);
        rootView = mBinding.getRoot();
        context=getActivity();
        /**
         *  Set typeface of text
         */
        mBinding.txtSelect.setTypeface(FontCustom.setFontBold(context));
        mBinding.txtGirl.setTypeface(FontCustom.setFontcontent(context));
        mBinding.txtBoy.setTypeface(FontCustom.setFontcontent(context));
        mBinding.txtOtherGender.setTypeface(FontCustom.setFontcontent(context));
        mBinding.btnnext.setTypeface(FontCustom.setFontcontent(context));
        /**
         * Select Gender
         */
        mBinding.imgBoy.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(boy_selected==0)
                {
                    boy_selected=1;
                    girl_selected=0;
                    other_type_selected=0;
                    mBinding.imgBoy.setImageResource(R.mipmap.selected_boy);
                    mBinding.imgGirl.setImageResource(R.mipmap.girl);
                    mBinding.otherGenderMainUnselctionFrame.setVisibility(View.VISIBLE);
                    mBinding.otherGenderSelectionFrame.setVisibility(View.GONE);
                }
                else {
                    boy_selected=0;
                    mBinding.imgBoy.setImageResource(R.mipmap.boy);
                }

                return false;
            }
        });
        mBinding.imgGirl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(girl_selected==0)
                {
                    girl_selected=1;
                    boy_selected=0;
                    other_type_selected=0;
                    mBinding.imgBoy.setImageResource(R.mipmap.boy);
                    mBinding.imgGirl.setImageResource(R.mipmap.selected_girl);
                    mBinding.otherGenderMainUnselctionFrame.setVisibility(View.VISIBLE);
                    mBinding.otherGenderSelectionFrame.setVisibility(View.GONE);

                }
                else {
                    girl_selected=0;
                    mBinding.imgGirl.setImageResource(R.mipmap.girl);
                }
                return false;
            }
        });

        mBinding.lnOtherGenderTypeSelection.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(other_type_selected==0)
                {
                    other_type_selected=1;
                    boy_selected=0;
                    girl_selected=0;
                    mBinding.otherGenderSelectionFrame.setVisibility(View.VISIBLE);
                    mBinding.otherGenderMainUnselctionFrame.setVisibility(View.GONE);
                    mBinding.imgBoy.setImageResource(R.mipmap.boy);
                    mBinding.imgGirl.setImageResource(R.mipmap.girl);

                }
                else {
                    other_type_selected=0;
                    mBinding.otherGenderMainUnselctionFrame.setVisibility(View.VISIBLE);
                    mBinding.otherGenderSelectionFrame.setVisibility(View.GONE);
                }
                return false;
            }
        });

        mBinding.btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(girl_selected==1 || boy_selected==1 || other_type_selected==1) {
                    _btn2.setImageResource(R.mipmap.fill_circle);
                    _mViewPager.setCurrentItem(1);
                }
                else {
                    Toast.makeText(context,"Please select your gender!",Toast.LENGTH_LONG).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        /**
         * Set previous selected value
         */
        if(boy_selected==1)
        {
            mBinding.imgBoy.setImageResource(R.mipmap.selected_boy);
            mBinding.imgGirl.setImageResource(R.mipmap.girl);
            mBinding.otherGenderMainUnselctionFrame.setVisibility(View.VISIBLE);
        }
        else if(girl_selected==1)
        {
            mBinding.imgBoy.setImageResource(R.mipmap.boy);
            mBinding.imgGirl.setImageResource(R.mipmap.selected_girl);
            mBinding.otherGenderMainUnselctionFrame.setVisibility(View.VISIBLE);
        }
        else if(other_type_selected==1)
        {
            mBinding.otherGenderSelectionFrame.setVisibility(View.VISIBLE);
            mBinding.otherGenderMainUnselctionFrame.setVisibility(View.GONE);
            mBinding.imgBoy.setImageResource(R.mipmap.boy);
            mBinding.imgGirl.setImageResource(R.mipmap.girl);
        }
    }
}
