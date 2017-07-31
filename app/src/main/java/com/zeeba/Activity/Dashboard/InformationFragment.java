package com.zeeba.Activity.Dashboard;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zeeba.Adapter.CustomPagerAdapter;
import com.zeeba.R;
import com.zeeba.databinding.FragmentInformation1Binding;
import com.zeeba.utils.ConnectionDetector;
import com.zeeba.utils.Pref;


public class InformationFragment extends Fragment{

    FragmentInformation1Binding mBinding;
    View rootView;
    Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_information1, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();
        ((DashBoardMainActivity)getActivity()).mBinding.imgRefreshData.setVisibility(View.GONE);
        mBinding.btnsocial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pref.setValue(context,"from_social","true");
                Intent intent = new Intent(getActivity(), InformationDetailActivity.class);
                startActivity(intent);
            }
        });
        mBinding.btnnonsocial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pref.setValue(context,"from_social","false");
                Intent intent = new Intent(getActivity(), InformationDetailActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
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
