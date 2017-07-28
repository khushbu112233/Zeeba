package com.zeeba.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.zeeba.Activity.Dashboard.DashBoardMainActivity;
import com.zeeba.Model.FacebookuserzeebaListModel;
import com.zeeba.Model.LeaderBoardUserListModel;
import com.zeeba.R;
import com.zeeba.databinding.RowFacebookFrndListBinding;
import com.zeeba.databinding.RowLeaderBoardUserListBinding;
import com.zeeba.utils.Pref;

import java.util.ArrayList;

/**
 * Created by aipxperts on 17/3/17.
 */

public class LeaderboardUserlistAdapter extends BaseAdapter {

    Context context;
    Activity ac;
    private ArrayList<LeaderBoardUserListModel> payLoadList = new ArrayList<>();


    public LeaderboardUserlistAdapter(Activity activity, ArrayList<LeaderBoardUserListModel> payLoadList) {
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

        RowLeaderBoardUserListBinding mBinding;
        final LeaderBoardUserListModel leaderBoardUserListModel = payLoadList.get(position);

        if (convertView == null) {

            mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_leader_board_user_list, parent, false);
            convertView = mBinding.getRoot();
            convertView.setTag(mBinding);
        } else {
            mBinding = (RowLeaderBoardUserListBinding) convertView.getTag();
        }

       mBinding.tvUserNameLeaderboard.setText(leaderBoardUserListModel.name);

        if(!TextUtils.isEmpty(leaderBoardUserListModel.point)){
            mBinding.tvPointresultwithcount.setText(leaderBoardUserListModel.point);
        }


        return convertView;
    }

}