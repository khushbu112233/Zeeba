package com.zeeba.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zeeba.R;
import com.zeeba.utils.FontCustom;

import java.util.ArrayList;

/**
 * Created by aipxperts-ubuntu-01 on 13/5/17.
 */

public class ReportCustomAdapter extends BaseAdapter {


    ArrayList<String> prgmNameList;
    Context context;
    ArrayList<String> imageId;
    private static LayoutInflater inflater=null;
    public ReportCustomAdapter(Context context, ArrayList<String> prgmNameList, ArrayList<String> prgmImages) {
        // TODO Auto-generated constructor stub
        this.prgmNameList = prgmNameList;
        this.context=context;
        this.imageId=prgmImages;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return prgmNameList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv;
        ImageView img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder=new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.report_list, null);
        holder.tv=(TextView) rowView.findViewById(R.id.textView1);
        holder.img=(ImageView) rowView.findViewById(R.id.imageView1);
        holder.tv.setTypeface(FontCustom.setFontcontent(context));
        holder.tv.setText(prgmNameList.get(position));
        // Log.e("name_list",""+prgmNameList.get(position));
        // holder.img.setImageResource(imageId[position]);
        Glide.with(context)
                .load(imageId.get(position))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        Log.v("glide","onLoadStarted");
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {

                        Log.v("glide","onLoadFailed");
                    }

                    @Override
                    public void onResourceReady(final Bitmap icon1, GlideAnimation<? super Bitmap> glideAnimation) {
                        Log.v("glide","onResourceReady");
                        //  Bitmap blur_bitmap = BlurBuilder.blur(getActivity(), icon1);
                        holder.img.setImageBitmap(icon1);
                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        Log.v("glide","onLoadCleared");
                    }
                    @Override
                    public void setRequest(Request request) {
                        Log.v("glide","setRequest");
                    }

                    @Override
                    public Request getRequest() {
                        Log.v("glide","getRequest");
                        return null;
                    }

                    @Override
                    public void onStart() {
                        Log.v("glide","onStart");
                    }

                    @Override
                    public void onStop() {
                        Log.v("glide","onStop");
                    }

                    @Override
                    public void onDestroy() {
                        Log.v("glide","onDestroy");
                    }
                });

        /*rowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "You Clicked "+prgmNameList.get(position), Toast.LENGTH_LONG).show();
            }
        });
*/
        return rowView;
    }

}
