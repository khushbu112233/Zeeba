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
import com.zeeba.Activity.Dashboard.CategoryActivity1;
import com.zeeba.Activity.Dashboard.DashBoardMainActivity;
import com.zeeba.Activity.Dashboard.ThankYouFBFrndResultActivity;
import com.zeeba.Model.AcceptedChallengesFBListModel;
import com.zeeba.Model.FacebookuserzeebaListModel;
import com.zeeba.R;
import com.zeeba.databinding.RowAcceptedChallegesFrndListBinding;
import com.zeeba.databinding.RowFacebookFrndListBinding;
import com.zeeba.utils.ConnectionDetector;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;
import com.zeeba.utils.Utils;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by aipxperts on 17/3/17.
 */

public class AcceptedChallegesFbFrndListAdapter extends BaseAdapter {

    Context context;
    Activity ac;
    private ArrayList<AcceptedChallengesFBListModel> payLoadList = new ArrayList<>();
    ConnectionDetector cd;

    public AcceptedChallegesFbFrndListAdapter(Activity activity, ArrayList<AcceptedChallengesFBListModel> payLoadList) {
        context = activity;
        this.payLoadList = payLoadList;
        cd = new ConnectionDetector(context);

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

        RowAcceptedChallegesFrndListBinding mBinding;
        final AcceptedChallengesFBListModel acceptedChallengesFBListModel = payLoadList.get(position);

        if (convertView == null) {

            mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_accepted_challeges_frnd_list, parent, false);
            convertView = mBinding.getRoot();
            convertView.setTag(mBinding);
        } else {
            mBinding = (RowAcceptedChallegesFrndListBinding) convertView.getTag();
        }
        mBinding.tvchallengerName.setTypeface(FontCustom.setFontcontent(context));
        mBinding.tvCategoryName.setTypeface(FontCustom.setFontcontent(context));
        mBinding.tvSubCategoryName.setTypeface(FontCustom.setFontcontent(context));
        Glide.with(context).load(acceptedChallengesFBListModel.profile).transform(new Utils.CircleTransform(context)).placeholder(R.mipmap.boy).into(mBinding.imgUserFbProfile);
        mBinding.tvchallengerName.setText("Challenger:" + acceptedChallengesFBListModel.challage_from_name);
        mBinding.tvCategoryName.setText("Category: " + acceptedChallengesFBListModel.cate_name);
        mBinding.tvSubCategoryName.setText("Sub-Category: " + acceptedChallengesFBListModel.sub_cate_name);
        mBinding.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (acceptedChallengesFBListModel.is_game_over.equals("0")) {

                    if (cd.isConnectingToInternet()) {
                        Pref.setValue(context, "cate_id", acceptedChallengesFBListModel.cate);
                        Pref.setValue(context, "selected_qid", acceptedChallengesFBListModel.sub_cate);
                        Pref.setValue(getApplicationContext(), "invite_facebook", "1");
                        Pref.setValue(getApplicationContext(), "start_over", "0");
                        Pref.setValue(getApplicationContext(), "invite_id", acceptedChallengesFBListModel.challage_id);
                        Pref.setValue(context, "view_result", "1");
                        Pref.setValue(context, "facebook_request", "1");
                        Intent intent = new Intent(context, CategoryActivity1.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else {
                        cd.showToast(context, R.string.NO_INTERNET_CONNECTION);
                    }

                } else {
                    Pref.setValue(getApplicationContext(), "invite_id", acceptedChallengesFBListModel.challage_id);
                    Pref.setValue(getApplicationContext(), "view_result", "0");
                    context.startActivity(new Intent(context, ThankYouFBFrndResultActivity.class).putExtra("challage_id", acceptedChallengesFBListModel.challage_id));
                }
            }
        });

        return convertView;
    }

}