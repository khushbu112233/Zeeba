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
import com.zeeba.Activity.Dashboard.SelectAnswerActivity_new;
import com.zeeba.Model.AnswerImage;
import com.zeeba.Model.Questions_Model;
import com.zeeba.Model.ReportList;
import com.zeeba.Model.ReportSubCategory;
import com.zeeba.R;
import com.zeeba.Report.ReportResultActivity;
import com.zeeba.Webservice.WebService;
import com.zeeba.databinding.SubcategoryGridLayoutBinding;
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

public class ReportSubCategoryAdapter extends BaseAdapter {
    Context mContext;
    SubcategoryGridLayoutBinding mBinding;
    ConnectionDetector cd;
    ArrayList<ReportSubCategory> question_detail = new ArrayList<>();
    ArrayList<ReportList> answerImageArrayList = new ArrayList<>();
    int set_click=0;
    long elapsedDays;
    long elapsedHours;

    public ReportSubCategoryAdapter(Context context, ArrayList<ReportSubCategory> question_detail) {
        this.mContext = context;
        this.question_detail=question_detail;
        cd=new ConnectionDetector(mContext);

    }

    @Override
    public int getCount() {
        return question_detail.size();
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


        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.subcategory_grid_layout, viewGroup, false);
        mBinding.txtTitle.setTypeface(FontCustom.setFontcontent(mContext));
        //  mBinding.txtTitle1.setTypeface(FontCustom.setFontcontent(mContext));
        mBinding.txtDaysremaining.setTypeface(FontCustom.setFontcontent(mContext));
        mBinding.txtDaysremaining.setVisibility(View.GONE);
        //  printDifference(System.currentTimeMillis(), Long.parseLong(question_detail.get(pos).getExpire_date()));
        mBinding.txtTitle.setText(question_detail.get(pos).getReport_sub_cat_name());

        Glide.with(mContext)
                .load(Pref.getValue(mContext, "report_cate_fore_image", ""))
                .into(mBinding.imgSubCategory);
        mBinding.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("set_click",Pref.getValue(mContext,"set_click","")+"---");
                Pref.setValue(mContext, "report_sub_cate_id",question_detail.get(pos).getRepost_sub_cat_id());
                Pref.setValue(mContext, "report_sub_cate_name",question_detail.get(pos).getReport_sub_cat_name());
                Intent intent=new Intent(mContext,ReportResultActivity.class);
                mContext.startActivity(intent);

            }
        });


        return mBinding.getRoot();

    }


}
