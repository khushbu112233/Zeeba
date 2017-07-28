package com.zeeba.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.zeeba.Model.SubcategoryLeaderBoardUserListModel;
import com.zeeba.R;
import com.zeeba.databinding.RowLeaderBoardUserListBinding;
import com.zeeba.databinding.RowSubCategoryLeaderBoardUserListBinding;
import com.zeeba.utils.FontCustom;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by aipxperts on 17/3/17.
 */

public class SubcategoryLeaderboardUserlistAdapter extends BaseAdapter {

    Context context;
    private ArrayList<SubcategoryLeaderBoardUserListModel> payLoadList = new ArrayList<>();
    int count=0;
    String maxPoint;

    public SubcategoryLeaderboardUserlistAdapter(Activity activity, ArrayList<SubcategoryLeaderBoardUserListModel> payLoadList,String max_point) {
        context = activity;
        this.payLoadList = payLoadList;
        this.maxPoint=max_point;

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

        final RowSubCategoryLeaderBoardUserListBinding mBinding;
        final SubcategoryLeaderBoardUserListModel leaderBoardUserListModel = payLoadList.get(position);

        if (convertView == null) {

            mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_sub_category_leader_board_user_list, parent, false);
            convertView = mBinding.getRoot();
            convertView.setTag(mBinding);
        } else {
            mBinding = (RowSubCategoryLeaderBoardUserListBinding) convertView.getTag();
        }
        mBinding.tvUserNameLeaderboard.setTypeface(FontCustom.setFontcontent(context));
        mBinding.myTextProgress.setTypeface(FontCustom.setFontcontent(context));
        mBinding.tvNumberofLeader.setTypeface(FontCustom.setFontcontent(context));
        if(payLoadList.get(position).point.equals("0")) {
            mBinding.progressBar1.setMax(100);
            mBinding.progressBar1.setProgress(0);
            mBinding.myTextProgress.setText(leaderBoardUserListModel.point);

        }
        else{
            int multiplied = Integer.parseInt(String.valueOf(100*Integer.parseInt(leaderBoardUserListModel.point))) / Integer.parseInt(maxPoint);

            mBinding.progressBar1.setMax(100);
            mBinding.progressBar1.setProgress(multiplied);
            mBinding.myTextProgress.setText(leaderBoardUserListModel.point);

        }
        count=position+1;
        mBinding.tvNumberofLeader.setText(""+count);

       mBinding.tvUserNameLeaderboard.setText(leaderBoardUserListModel.name);

        if(leaderBoardUserListModel.gender.equalsIgnoreCase("male")){
            mBinding.imgUseravatarLeaderboard.setImageResource(R.mipmap.boy);
        }
        else if(leaderBoardUserListModel.gender.equalsIgnoreCase("female")){
            mBinding.imgUseravatarLeaderboard.setImageResource(R.mipmap.girl);
        }
        else{
            mBinding.imgUseravatarLeaderboard.setImageResource(R.mipmap.other_leader_img);
        }


        return convertView;
    }

}