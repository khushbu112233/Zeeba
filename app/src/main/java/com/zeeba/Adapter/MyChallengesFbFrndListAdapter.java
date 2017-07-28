package com.zeeba.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.zeeba.Activity.Dashboard.DashBoardMainActivity;
import com.zeeba.Activity.Dashboard.ThankYouFBFrndResultActivity;
import com.zeeba.Model.FacebookuserzeebaListModel;
import com.zeeba.Model.MyChallengesFBListModel;
import com.zeeba.R;
import com.zeeba.databinding.RowFacebookFrndListBinding;
import com.zeeba.databinding.RowMyAcceptedChallegesFrndListBinding;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;
import com.zeeba.utils.Utils;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by aipxperts on 17/3/17.
 */

public class MyChallengesFbFrndListAdapter extends BaseAdapter {

    Context context;
    Activity ac;
    private ArrayList<MyChallengesFBListModel> payLoadList = new ArrayList<>();


    public MyChallengesFbFrndListAdapter(Activity activity, ArrayList<MyChallengesFBListModel> payLoadList) {
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

        RowMyAcceptedChallegesFrndListBinding mBinding;
        final MyChallengesFBListModel myChallengesFBListModel = payLoadList.get(position);

        if (convertView == null) {

            mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_my_accepted_challeges_frnd_list, parent, false);
            convertView = mBinding.getRoot();
            convertView.setTag(mBinding);
        } else {
            mBinding = (RowMyAcceptedChallegesFrndListBinding) convertView.getTag();
        }
        mBinding.tvFriendName.setTypeface(FontCustom.setFontcontent(context));
        mBinding.tvCategoryName.setTypeface(FontCustom.setFontcontent(context));
        mBinding.tvSubCategoryName.setTypeface(FontCustom.setFontcontent(context));
        mBinding.tvStatusOfChallenges.setTypeface(FontCustom.setFontcontent(context));
        Glide.with(context).load(myChallengesFBListModel.profile).transform(new Utils.CircleTransform(context)).placeholder(R.mipmap.boy).into(mBinding.imgUserFbProfile);
        mBinding.tvFriendName.setText("Challenge To: " + myChallengesFBListModel.challage_to_name);
        mBinding.tvCategoryName.setText("Category: " + myChallengesFBListModel.cate_name);
        mBinding.tvSubCategoryName.setText("Sub-Category: " + myChallengesFBListModel.sub_cate_name);
        if(myChallengesFBListModel.status.equalsIgnoreCase("0")){
            mBinding.imgStatusOfChallenges.setImageDrawable(context.getDrawable(R.mipmap.pending_tick_blue));
            mBinding.tvStatusOfChallenges.setText("Pending");
        }else if(myChallengesFBListModel.status.equalsIgnoreCase("1")){
            mBinding.imgStatusOfChallenges.setImageDrawable(context.getDrawable(R.mipmap.accept_tick_green));
            mBinding.tvStatusOfChallenges.setText("Accepted");
        }
        else if(myChallengesFBListModel.status.equalsIgnoreCase("2")){
            mBinding.imgStatusOfChallenges.setImageDrawable(context.getDrawable(R.mipmap.completed_tick_yellow));
            mBinding.tvStatusOfChallenges.setText("Completed");
        }
        else{
            mBinding.imgStatusOfChallenges.setImageDrawable(context.getDrawable(R.mipmap.rejected_tick_red));
            mBinding.tvStatusOfChallenges.setText("Rejected");
        }

        mBinding.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pref.setValue(context, "invite_id", myChallengesFBListModel.challage_id);
                    context.startActivity(new Intent(context, ThankYouFBFrndResultActivity.class).putExtra("challage_id",myChallengesFBListModel.challage_id));
            }
        });
        return convertView;
    }

}