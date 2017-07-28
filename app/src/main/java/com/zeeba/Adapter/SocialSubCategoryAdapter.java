package com.zeeba.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.zeeba.Activity.Dashboard.CategoryActivity1;
import com.zeeba.Activity.Dashboard.SelectAnswerActivity;
import com.zeeba.Activity.Dashboard.SelectAnswerSocialActivity;
import com.zeeba.Activity.Dashboard.SelectSocialSubCategoryActivity;
import com.zeeba.Activity.Dashboard.ThankYouActivity;
import com.zeeba.Model.AnswerImage;
import com.zeeba.Model.Questions_Model;
import com.zeeba.Model.SubCategory;
import com.zeeba.R;
import com.zeeba.Webservice.WebService;
import com.zeeba.databinding.SubcategoryGridLayout1Binding;
import com.zeeba.databinding.SubcategoryGridLayoutBinding;
import com.zeeba.databinding.SubcategoryGridLayoutSocialBinding;
import com.zeeba.utils.ConnectionDetector;
import com.zeeba.utils.Constants;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.zeeba.Activity.MainActivity.realm;

/**
 * Created by aipxperts-ubuntu-01 on 25/3/17.
 */

public class SocialSubCategoryAdapter extends BaseAdapter {
    Context mContext;
    SubcategoryGridLayoutSocialBinding mBinding;
    ConnectionDetector cd;
    ArrayList<SubCategory> subCategories = new ArrayList<>();

    public SocialSubCategoryAdapter(Context context, ArrayList<SubCategory> subCategories) {
        this.mContext = context;
        this.subCategories=subCategories;
        cd=new ConnectionDetector(mContext);

    }

    @Override
    public int getCount() {
        return subCategories.size();
    }

    @Override
    public Object getItem(int pos) {
        return pos;
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(final int pos, View view, ViewGroup viewGroup) {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.subcategory_grid_layout_social, viewGroup, false);
        mBinding.txtTitle.setTypeface(FontCustom.setFontcontent(mContext));
        mBinding.txtTitle.setText(subCategories.get(pos).getName());
        Glide.with(mContext)
                .load(subCategories.get(pos).getForeground())
                .into(mBinding.imgSubCategory);
        mBinding.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(cd.isConnectingToInternet()) {
                    Log.v("set_click",Pref.getValue(mContext,"set_click","")+"---");

                    if(Pref.getValue(mContext,"set_click","").equals("0")) {

                        Pref.setValue(mContext, "cate_name", subCategories.get(pos).getName());
                        Pref.setValue(mContext, "cate_id",  subCategories.get(pos).getSub_id());
                        Pref.setValue(mContext,"is_FirstTime","yes");
                        Intent intent = new Intent(mContext, SelectAnswerSocialActivity.class);
                        mContext.startActivity(intent);
                        Pref.setValue(mContext,"set_click","1");
                    }
                }else
                {
                    cd.showToast(mContext, R.string.NO_INTERNET_CONNECTION);
                }
            }
        });
        

        return mBinding.getRoot();

    }


}
