package com.zeeba.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zeeba.Activity.Dashboard.DashBoardMainActivity;
import com.zeeba.Activity.Dashboard.DashboardFragment_new;
import com.zeeba.Activity.Dashboard.SelectAnswerActivity_new;
import com.zeeba.Model.FacebookuserzeebaListModel;
import com.zeeba.R;
import com.zeeba.databinding.RowFacebookFrndListBinding;
import com.zeeba.fragment.ChallengerFacebookFriendsListFragment;
import com.zeeba.utils.Pref;
import com.zeeba.utils.Utils;

import java.util.ArrayList;

/**
 * Created by aipxperts on 17/3/17.
 */

public class FacebookFriendListAdapter extends BaseAdapter {

    Context context;
    Activity ac;
    private ArrayList<FacebookuserzeebaListModel> payLoadList = new ArrayList<>();


    public FacebookFriendListAdapter(Activity activity, ArrayList<FacebookuserzeebaListModel> payLoadList) {
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

        RowFacebookFrndListBinding mBinding;
        final FacebookuserzeebaListModel facebookuserzeebaListModel = payLoadList.get(position);

        if (convertView == null) {

            mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_facebook_frnd_list, parent, false);
            convertView = mBinding.getRoot();
            convertView.setTag(mBinding);
        } else {
            mBinding = (RowFacebookFrndListBinding) convertView.getTag();
        }

        Glide.with(context).load("http://graph.facebook.com/" + facebookuserzeebaListModel.id + "/picture?type=large").transform(new Utils.CircleTransform(context)).placeholder(R.mipmap.app_icon).into(mBinding.imgUserFbProfile);

        mBinding.tvFriendName.setText(facebookuserzeebaListModel.name);
        mBinding.btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pref.setValue(context,"facebook_request","1");
                Pref.setValue(context,"send_request_user_facebook_id",facebookuserzeebaListModel.id);
                Pref.setValue(context,"send_request_user_facebook_name",facebookuserzeebaListModel.name);
                DashboardFragment_new fragment = new DashboardFragment_new();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();
               // ((Activity)context).finish();
               // context.startActivity(new Intent(context, DashBoardMainActivity.class));
              //  Intent intent = new Intent(context, DashBoardMainActivity.class);
               // ((Activity)context).startActivityForResult(intent, 100);
                //Toast.makeText(context, "Testing", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

}