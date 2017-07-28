package com.zeeba.Activity.Wizard;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.WindowManager;

import com.zeeba.Model.FacebookuserzeebaListModel;
import com.zeeba.R;
import com.zeeba.databinding.ActivityWizardBinding;

import java.util.ArrayList;

public class WizardActivity extends FragmentActivity {

    ActivityWizardBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_wizard);
        StatusBar();


        WizardLoadFragment fragment = new WizardLoadFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment).commit();

    }

    public  void StatusBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

    }
}
