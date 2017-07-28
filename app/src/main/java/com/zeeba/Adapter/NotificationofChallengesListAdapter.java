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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zeeba.Activity.Dashboard.CategoryActivity1;
import com.zeeba.Activity.Dashboard.SelectAnswerActivity_new;
import com.zeeba.Activity.Dashboard.ThankYouActivity;
import com.zeeba.Activity.Dashboard.ThankYouFBFrndResultActivity;
import com.zeeba.Activity.MainActivity;
import com.zeeba.Model.FacebookuserzeebaListModel;
import com.zeeba.Model.NotificationFrndChallengesListModel;
import com.zeeba.R;
import com.zeeba.Services.NotificationButtonListener;
import com.zeeba.Webservice.WebService;
import com.zeeba.databinding.RowMyAcceptedChallegesFrndListBinding;
import com.zeeba.databinding.RowNotificationChallegesFrndListBinding;
import com.zeeba.utils.ConnectionDetector;
import com.zeeba.utils.Constants;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;
import com.zeeba.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by aipxperts on 17/3/17.
 */

public class NotificationofChallengesListAdapter extends BaseAdapter {

    Context context;
    Activity ac;
    private ArrayList<NotificationFrndChallengesListModel> payLoadList = new ArrayList<>();
    ConnectionDetector cd;
    private String TAG = "Notification";
    int positionofCategory = 0;

    public NotificationofChallengesListAdapter(Context activity, ArrayList<NotificationFrndChallengesListModel> payLoadList) {
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

        final RowNotificationChallegesFrndListBinding mBinding;
        final NotificationFrndChallengesListModel notificationFrndChallengesListModel = payLoadList.get(position);

        if (convertView == null) {

            mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_notification_challeges_frnd_list, parent, false);
            convertView = mBinding.getRoot();
            convertView.setTag(mBinding);
        } else {
            mBinding = (RowNotificationChallegesFrndListBinding) convertView.getTag();
        }
        mBinding.tvNameChallengesCategory.setTypeface(FontCustom.setFontcontent(context));
        mBinding.tvAcceptView.setTypeface(FontCustom.setFontcontent(context));
        mBinding.tvRejectView.setTypeface(FontCustom.setFontcontent(context));
        Glide.with(context).load(notificationFrndChallengesListModel.profile).transform(new Utils.CircleTransform(context)).placeholder(R.mipmap.boy).into(mBinding.imgUserFbProfile);
        mBinding.tvNameChallengesCategory.setText(notificationFrndChallengesListModel.message);


        if (notificationFrndChallengesListModel.accept_btn.equals("1") && notificationFrndChallengesListModel.reject_btn.equals("1")) {
            mBinding.lnAcceptReject.setVisibility(View.VISIBLE);
            mBinding.tvViewResultGame.setVisibility(View.GONE);
            mBinding.lnAcceptMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cd.isConnectingToInternet()) {
                        Pref.setValue(context, "cate_id", notificationFrndChallengesListModel.cate);
                        Pref.setValue(context, "selected_qid", notificationFrndChallengesListModel.sub_cate);
                        Pref.setValue(context, "invite_id", notificationFrndChallengesListModel.challage_id);

                        // Pref.setValue(getApplicationContext(), "invite_facebook", "1");
                        new ExecuteTaskForAcceptorDeny(context, position).execute(notificationFrndChallengesListModel.challage_id, "accept");
                    } else {
                        cd.showToast(context, R.string.NO_INTERNET_CONNECTION);
                    }
                }
            });

            mBinding.lnRejectMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cd.isConnectingToInternet()) {
                        Pref.setValue(getApplicationContext(), "invite_facebook", "1");
                        new ExecuteTaskForAcceptorDeny(context, position).execute(notificationFrndChallengesListModel.challage_id, "reject");
                    } else {
                        cd.showToast(context, R.string.NO_INTERNET_CONNECTION);
                    }
                }
            });

        } else if (notificationFrndChallengesListModel.view_btn.equalsIgnoreCase("1")) {
            mBinding.tvViewResultGame.setVisibility(View.VISIBLE);
            mBinding.lnAcceptReject.setVisibility(View.GONE);

            mBinding.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Pref.setValue(getApplicationContext(), "invite_id", notificationFrndChallengesListModel.challage_id);
                    Pref.setValue(getApplicationContext(), "view_result", "0");
                    context.startActivity(new Intent(context, ThankYouFBFrndResultActivity.class).putExtra("challage_id", notificationFrndChallengesListModel.challage_id));
                }
            });

        } else {
            mBinding.tvViewResultGame.setVisibility(View.GONE);
            mBinding.lnAcceptReject.setVisibility(View.GONE);
            if (notificationFrndChallengesListModel.view_btn.equalsIgnoreCase("0") && notificationFrndChallengesListModel.accept_btn.equalsIgnoreCase("0")) {
                mBinding.llMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (notificationFrndChallengesListModel.status.equals("4")) {
                            Log.e("Noti", "Rejected");
                        } else {
                            if (notificationFrndChallengesListModel.is_game_over.equalsIgnoreCase("0")) {

                                if (notificationFrndChallengesListModel.request_from_fbid.equals(Pref.getValue(context, Constants.PREF_USER_FB_ID, ""))) {
                                    Pref.setValue(getApplicationContext(), "invite_id", notificationFrndChallengesListModel.challage_id);
                                    Pref.setValue(getApplicationContext(), "view_result", "0");
                                    context.startActivity(new Intent(context, ThankYouFBFrndResultActivity.class).putExtra("challage_id", notificationFrndChallengesListModel.challage_id));

                                } else {
                                    if (cd.isConnectingToInternet()) {
                                        Pref.setValue(context, "cate_id", notificationFrndChallengesListModel.cate);
                                        Pref.setValue(context, "selected_qid", notificationFrndChallengesListModel.sub_cate);
                                        Pref.setValue(getApplicationContext(), "invite_facebook", "1");
                                        Pref.setValue(getApplicationContext(), "start_over", "0");
                                        Pref.setValue(getApplicationContext(), "invite_id", notificationFrndChallengesListModel.challage_id);
                                        Pref.setValue(context, "view_result", "1");
                                        Pref.setValue(context, "facebook_request", "1");
                                        Intent intent = new Intent(context, CategoryActivity1.class);
                                        //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        //  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                    } else {
                                        cd.showToast(context, R.string.NO_INTERNET_CONNECTION);
                                    }

                                }
                            } else {
                                Pref.setValue(getApplicationContext(), "invite_id", notificationFrndChallengesListModel.challage_id);
                                Pref.setValue(getApplicationContext(), "view_result", "0");
                                context.startActivity(new Intent(context, ThankYouFBFrndResultActivity.class).putExtra("challage_id", notificationFrndChallengesListModel.challage_id));
                            }
                        }
                    }
                });
            } else {
                Log.e("Adapter", "%%%%");
            }

        }

        /*mBinding.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if (notificationFrndChallengesListModel.is_game_over.equalsIgnoreCase("0")) {
                Pref.setValue(context, "cate_id", notificationFrndChallengesListModel.cate);
                Pref.setValue(context, "selected_qid", notificationFrndChallengesListModel.sub_cate);
                Pref.setValue(getApplicationContext(), "invite_facebook", "1");
                Intent intent = new Intent(context, CategoryActivity1.class);
                //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                       *//* } else {
                            context.startActivity(new Intent(context, ThankYouFBFrndResultActivity.class).putExtra("challage_id", notificationFrndChallengesListModel.challage_id));
                        }*//*

            }
        });*/


        //for information
        /**
         * jain flag=0 ==> accept and reject button show.pending
         * jain flag=1 ==> Join start game
         * jain flag=2 ==> Completed ( first - user)
         * jain flag=3 ==>Completed ( Game Over)
         *
         */
       /* if(notificationFrndChallengesListModel.join_flag.equalsIgnoreCase("0")){
            mBinding.lnAcceptReject.setVisibility(View.VISIBLE);
            mBinding.tvViewResultGame.setVisibility(View.GONE);
            mBinding.tvAcceptView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cd.isConnectingToInternet()) {
                        Pref.setValue(context, "cate_id", notificationFrndChallengesListModel.cate);
                        Pref.setValue(context, "selected_qid", notificationFrndChallengesListModel.sub_cate);
                        // Pref.setValue(getApplicationContext(), "invite_facebook", "1");
                        new ExecuteTaskForAcceptorDeny(context,position).execute(notificationFrndChallengesListModel.challage_id, "accept");
                    } else {
                        cd.showToast(context, R.string.NO_INTERNET_CONNECTION);
                    }
                }
            });

            mBinding.tvRejectView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cd.isConnectingToInternet()) {
                        Pref.setValue(getApplicationContext(), "invite_facebook", "1");
                        new ExecuteTaskForAcceptorDeny(context,position).execute(notificationFrndChallengesListModel.challage_id, "reject");
                    } else {
                        cd.showToast(context, R.string.NO_INTERNET_CONNECTION);
                    }
                }
            });

        }else if(notificationFrndChallengesListModel.join_flag.equalsIgnoreCase("1")){
            mBinding.lnAcceptReject.setVisibility(View.GONE);
            mBinding.tvViewResultGame.setVisibility(View.GONE);
            mBinding.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                        //if (notificationFrndChallengesListModel.is_game_over.equalsIgnoreCase("0")) {
                            Pref.setValue(context, "cate_id", notificationFrndChallengesListModel.cate);
                            Pref.setValue(context, "selected_qid", notificationFrndChallengesListModel.sub_cate);
                            Pref.setValue(getApplicationContext(), "invite_facebook", "1");
                            Intent intent = new Intent(context, CategoryActivity1.class);
                          //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                          //  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                       *//* } else {
                            context.startActivity(new Intent(context, ThankYouFBFrndResultActivity.class).putExtra("challage_id", notificationFrndChallengesListModel.challage_id));
                        }*//*

                }
            });
        }
        else if(notificationFrndChallengesListModel.join_flag.equalsIgnoreCase("2")){
            mBinding.lnAcceptReject.setVisibility(View.GONE);
            mBinding.tvViewResultGame.setVisibility(View.GONE);
            mBinding.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, ThankYouFBFrndResultActivity.class).putExtra("challage_id",notificationFrndChallengesListModel.challage_id));
                }
            });
        }
        else if(notificationFrndChallengesListModel.join_flag.equalsIgnoreCase("3")){
            mBinding.lnAcceptReject.setVisibility(View.GONE);
            mBinding.tvViewResultGame.setVisibility(View.VISIBLE);
            mBinding.tvViewResultGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, ThankYouFBFrndResultActivity.class).putExtra("challage_id",notificationFrndChallengesListModel.challage_id));
                }
            });
        }
        else{

        }*/



      /*  if (notificationFrndChallengesListModel.accept_btn.equals("0") && notificationFrndChallengesListModel.reject_btn.equals("0")) {
            mBinding.lnAcceptReject.setVisibility(View.GONE);
            mBinding.lnAcceptMain.setVisibility(View.GONE);
            mBinding.lnRejectMain.setVisibility(View.GONE);
        } else {
            mBinding.lnAcceptReject.setVisibility(View.VISIBLE);
            mBinding.lnAcceptMain.setVisibility(View.VISIBLE);
            mBinding.lnRejectMain.setVisibility(View.VISIBLE);
        }*/

        /*if (notificationFrndChallengesListModel.reject_btn.equals("0")) {
            mBinding.lnAcceptReject.setVisibility(View.GONE);
            mBinding.lnRejectMain.setVisibility(View.GONE);
        } else {
            mBinding.lnAcceptReject.setVisibility(View.VISIBLE);
            mBinding.lnRejectMain.setVisibility(View.VISIBLE);
        }*/

        /*if (notificationFrndChallengesListModel.view_btn.equals("0")) {
            mBinding.tvViewResultGame.setVisibility(View.GONE);
        } else {
            mBinding.tvViewResultGame.setVisibility(View.VISIBLE);
        }*/


        return convertView;
    }

    public class ExecuteTaskForAcceptorDeny extends AsyncTask<String, Integer, String> {

        public ExecuteTaskForAcceptorDeny(Context mContext, int position) {
            //   this.trad_no = trad_no;
            //   this.device_token_id = device_token_id;
            context = mContext;
            positionofCategory = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(context);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = "";
            res = WebService.PostData(params, Constants.InviteResponseByUserAcceptorDeny, WebService.INVITE_RESPONSE_BY_USER_ACCEPT_DENY);
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            WebService.dismissProgress();
            try {
                Log.e(TAG, "result " + result.toString());
                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.optString("code").equals("200")) {

                    if (jsonObject.optString("msg").equalsIgnoreCase("Request accepted Successful")) {
                        Log.e(TAG, "%%%% " + jsonObject.optString("msg"));
                        Pref.setValue(getApplicationContext(), "invite_facebook", "1");
                        Pref.setValue(getApplicationContext(), "start_over", "0");
                        Pref.setValue(context, "view_result", "1");
                        Pref.setValue(context, "facebook_request", "1");
                        Intent intent = new Intent(context, CategoryActivity1.class);
                        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    } else {
                        payLoadList.remove(positionofCategory);
                        notifyDataSetChanged();
                        Log.e("Broadcase", "rejected notificaiton");
                    }

                }else if (jsonObject.optString("code").equals("1000")) {
                    Utils.exitApplication(context);
                }
                else {
                    Toast.makeText(context, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}