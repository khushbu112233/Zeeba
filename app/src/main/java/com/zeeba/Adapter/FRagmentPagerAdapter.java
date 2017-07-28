package com.zeeba.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.zeeba.Fragment.Wizard.Step1Fragment;
import com.zeeba.Fragment.Wizard.Step2Fragment;
import com.zeeba.Fragment.Wizard.Step3Fragment;
import com.zeeba.Model.FacebookuserzeebaListModel;

import java.util.ArrayList;

/**
 * Created by aipxperts on 22/3/17.
 */

public class FRagmentPagerAdapter extends FragmentPagerAdapter {
    private Context _context;
    public static int totalPage = 3;

    public FRagmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        _context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = new Fragment();
        switch (position) {
            case 0:
                f = new Step1Fragment();
                break;
            case 1:
                f = new Step2Fragment();
                break;
            case 2:
                f = new Step3Fragment();
                break;
        }
        return f;
    }

    @Override
    public int getCount() {
        return totalPage;
    }
}
