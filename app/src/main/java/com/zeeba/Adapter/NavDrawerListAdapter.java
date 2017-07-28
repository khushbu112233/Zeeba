package com.zeeba.Adapter;

/**
 * Created by aipxperts on 3/2/16.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.zeeba.R;
import com.zeeba.utils.Constants;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;
import com.zeeba.utils.Utils;

import java.util.ArrayList;

public class NavDrawerListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> navDrawerItems;
    private ArrayList<Integer> navDrawerItems_icons;

    public NavDrawerListAdapter(Context context, ArrayList<String> navDrawerItems ,ArrayList<Integer> navDrawerItems_icons){
        this.context = context;
        this.navDrawerItems = navDrawerItems;
        this.navDrawerItems_icons=navDrawerItems_icons;

    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list, null);

        }

        LinearLayout lnFBUserProfile=(LinearLayout)convertView.findViewById(R.id.ln_fb_profile);
        TextView tvFBUsername=(TextView)convertView.findViewById(R.id.tv_FB_username);
        ImageView imgFBUserProfile=(ImageView)convertView.findViewById(R.id.img_FB_userimg);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        ImageView img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
        txtTitle.setTypeface(FontCustom.setFontcontent(context));
        txtTitle.setText(navDrawerItems.get(position));
        img_icon.setImageResource(navDrawerItems_icons.get(position));

       /* if(position==0) {
            if (!Pref.getValue(context, Constants.PREF_USER_FB_ID, "").equalsIgnoreCase("")) {
                lnFBUserProfile.setVisibility(View.VISIBLE);
                tvFBUsername.setText(Pref.getValue(context, Constants.PREF_USER_FB_NAME, ""));
                Glide.with(context).load(Pref.getValue(context, Constants.PREF_USER_FB_IMAGE, "")).transform(new Utils.CircleTransform(context)).placeholder(R.mipmap.boy).into(imgFBUserProfile);
            } else {
                lnFBUserProfile.setVisibility(View.GONE);
            }
        }*/

        return convertView;
    }

}