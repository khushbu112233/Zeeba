package com.zeeba.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.zeeba.Activity.Dashboard.DashBoardMainActivity;
import com.zeeba.Model.ChallengerFBuserListModel;
import com.zeeba.Model.FacebookuserzeebaListModel;
import com.zeeba.R;
import com.zeeba.databinding.RowChallengerFbFrndListBinding;
import com.zeeba.databinding.RowFacebookFrndListBinding;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;

import java.util.ArrayList;

/**
 * Created by aipxperts on 17/3/17.
 */

public class ChallengerFBFriendListAdapter extends BaseAdapter {

    Context context;
    Activity ac;
    private ArrayList<ChallengerFBuserListModel> payLoadList = new ArrayList<>();


    public ChallengerFBFriendListAdapter(Activity activity, ArrayList<ChallengerFBuserListModel> payLoadList) {
        context = activity;
        this.payLoadList = payLoadList;


    }

    @Override
    public int getCount() {
        return payLoadList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        RowChallengerFbFrndListBinding mBinding;
        final ChallengerFBuserListModel challengerFBuserListModel = payLoadList.get(position);

        if (convertView == null) {

            mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_challenger_fb_frnd_list, parent, false);
            convertView = mBinding.getRoot();
            convertView.setTag(mBinding);
        } else {
            mBinding = (RowChallengerFbFrndListBinding) convertView.getTag();
        }
        mBinding.tvCategoryName.setTypeface(FontCustom.setFont(context));
        mBinding.tvInviteFromFriendName.setTypeface(FontCustom.setFont(context));
        mBinding.tvInviteToFriendName.setTypeface(FontCustom.setFont(context));
        mBinding.tvVersus.setTypeface(FontCustom.setFont(context));
        mBinding.tvInviteFromFriendScore.setTypeface(FontCustom.setFont(context));
        mBinding.tvInviteToFriendScore.setTypeface(FontCustom.setFont(context));

        mBinding.tvCategoryName.setText(position+1 + ") "+challengerFBuserListModel.cate);
        mBinding.tvInviteFromFriendName.setText(challengerFBuserListModel.challage_from_name);
        mBinding.tvInviteToFriendName.setText(challengerFBuserListModel.challage_to_name);
        mBinding.tvInviteFromFriendScore.setText(challengerFBuserListModel.challage_from_result);
        mBinding.tvInviteToFriendScore.setText(challengerFBuserListModel.challage_to_result);

        return convertView;
    }

}