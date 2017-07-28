package com.zeeba.Fragment.Wizard;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zeeba.R;
import com.zeeba.databinding.FragmentStep2Binding;
import com.zeeba.utils.FontCustom;

import java.util.ArrayList;

import static com.zeeba.Activity.Wizard.WizardLoadFragment._btn1;
import static com.zeeba.Activity.Wizard.WizardLoadFragment._btn3;
import static com.zeeba.Activity.Wizard.WizardLoadFragment._mViewPager;
import static com.zeeba.Activity.Wizard.WizardLoadFragment.selected_race;
import static com.zeeba.Activity.Wizard.WizardLoadFragment.selected_race_pos;

/**
 * Created by aipxperts on 22/3/17.
 */

public class Step2Fragment extends Fragment{

    /**
     * Binding all views of xml
     */
    FragmentStep2Binding mBinding;
    /**
     * DEclare objects
     */
    View rootView;
    Context context;
    /**
     *  DEclare objects to dynamic generate views
     */
    TextView[] txt_race=new TextView[9];
    View[] view=new View[1];
    /**
     * Declare arraylist
     */
    ArrayList<String> arrayList_race=new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /**
         * Binding all xml views
         */
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_step2, container, false);
        rootView = mBinding.getRoot();
        context=getActivity();
        /**
         * Set typeface
         */
        mBinding.txtSelect.setTypeface(FontCustom.setFontBold(context));
        /**
         *  Create list & display
         */
        setracedata();
        mBinding.btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _btn1.setImageResource(R.mipmap.fill_circle);
                _mViewPager.setCurrentItem(0);
                }
        });

        /**
         * SElect any one race from list
         */
        mBinding.btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected_race_pos==-1) {

                    Toast.makeText(context,"Please select your race!",Toast.LENGTH_LONG).show();
                }
                else {
                    _btn3.setImageResource(R.mipmap.fill_circle);
                    _mViewPager.setCurrentItem(2);
                }
            }
        });

        return rootView;
    }

    public void setracedata()
    {
        arrayList_race.clear();
        arrayList_race.add("American indian or Alaska Native");
        arrayList_race.add("Asian");
        arrayList_race.add("Black or African American");
        arrayList_race.add("Hispanic/Latino");
        arrayList_race.add("Multiracial");
        arrayList_race.add("Native Hawaiian or other Pacific Islander");
        arrayList_race.add("White");
        arrayList_race.add("Other");
        arrayList_race.add("Prefer not to say");
        for(int i=0;i<arrayList_race.size();i++)
        {
            txt_race[i] = new TextView(context);
            txt_race[i].setText(arrayList_race.get(i));
            txt_race[i].setPadding(10,20,5,20);
            txt_race[i].setClickable(true);
            txt_race[i].setVisibility(View.VISIBLE);
            txt_race[i].setTextColor(getResources().getColor(R.color.white));
            txt_race[i].setGravity(Gravity.LEFT);
            txt_race[i].setTextSize(13);
            txt_race[i].setTypeface(FontCustom.setFontcontent(context));
            mBinding.listLayout.addView(txt_race[i]);


        }

       /* edt_other[0] = new EditText(context);
        edt_other[0].setHint("Other");
        edt_other[0].setPadding(5,20,5,10);

        edt_other[0].setClickable(true);
        edt_other[0].setVisibility(View.VISIBLE);
        edt_other[0].setHintTextColor(getResources().getColor(R.color.white));
        edt_other[0].setTextColor(getResources().getColor(R.color.white));
        edt_other[0].setGravity(Gravity.LEFT);
        edt_other[0].setTextSize(13);
        edt_other[0].setBackgroundColor(Color.parseColor("#00ffffff"));
        edt_other[0].setTypeface(FontCustom.setFontcontent(context));
        mBinding.listLayout.addView(edt_other[0]);*/

        view[0]=new View(context);
        view[0].setBackgroundColor(Color.parseColor("#ffffff"));
        view[0].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2, 1f));
        mBinding.listLayout.addView(view[0]);

        for(int i=0;i<arrayList_race.size();i++)
        {
            final int finalI = i;
            txt_race[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.v("data",txt_race[finalI].getText().toString()+"--");

                    for(int j=0;j<arrayList_race.size();j++)
                    {
                        if(j==finalI)
                        {
                            txt_race[finalI].setBackgroundResource(R.drawable.button_bg);
                            txt_race[finalI].setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.select_race, 0);
                            selected_race=txt_race[finalI].getText().toString();
                            selected_race_pos=finalI;
                            //  edt_other[0].setText("");
                        }
                        else {
                            txt_race[j].setBackgroundColor(Color.parseColor("#00ffffff"));
                            txt_race[j].setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        }
                    }

                }
            });
        }

        /*edt_other[0].addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                if(s.length()>0)
                {
                    for(int j=0;j<arrayList_race.size();j++)
                    {

                            txt_race[j].setBackgroundColor(Color.parseColor("#00ffffff"));
                            txt_race[j].setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });*/

    }

}
