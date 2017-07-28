package com.zeeba.Activity.Dashboard;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.zeeba.Adapter.NavDrawerListAdapter;
import com.zeeba.Model.FacebookuserzeebaListModel;
import com.zeeba.R;
import com.zeeba.Report.ReportDashboardFragment;
import com.zeeba.databinding.ActivityDashboardNewBinding;
import com.zeeba.fragment.ChallengerFriendsListFragment;
import com.zeeba.fragment.FacebookFrndRequestFragment;
import com.zeeba.fragment.LeaderboardUserListFragment;
import com.zeeba.fragment.MyChallengersAcceptedFacebookListFragment;
import com.zeeba.fragment.NotificationofChallengesListFragment;
import com.zeeba.utils.Constant;
import com.zeeba.utils.Constants;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;
import com.zeeba.utils.Utils;

import java.util.ArrayList;

public class DashBoardMainActivity extends FragmentActivity {

   public static ActivityDashboardNewBinding mBinding;
    ArrayList<String> navDrawerItems;
    ArrayList<Integer> navDrawerItems_icons;
    ArrayList<FacebookuserzeebaListModel> facebookuserzeebaListModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard_new);
        //  mBinding.txtSelect.setTypeface(FontCustom.setFontcontent(DashBoardMainActivity.this));
        /**
         * set status bar
         */
        StatusBar();
        /**
         * set drawer list
         */
        setdrawer();
        mBinding.listSlidermenu.setOnItemClickListener(new SlideMenuClickListener());
        String menuFragment = getIntent().getStringExtra("menuFragment");

        // If menuFragment is defined, then this activity was launched with a fragment selection
        if (menuFragment != null) {

            // Here we can decide what do to -- perhaps load other parameters from the intent extras such as IDs, etc
            if (menuFragment.equals("challengerscreenFragment")) {
                NotificationofChallengesListFragment fragment_remove = new NotificationofChallengesListFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment_remove).commit();
                Pref.setValue(DashBoardMainActivity.this, "drawer_value", "2");

                // ChallengerFriendsListFragment fragment = new ChallengerFriendsListFragment();
                // getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).commit();
                // Pref.setValue(DashBoardMainActivity.this, "drawer_value", "6");
            }
        } else {
            /**
             * load category list
             */

            DashboardFragment_new fragment = new DashboardFragment_new();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).commit();
            Pref.setValue(DashBoardMainActivity.this, "drawer_value", "0");
        }

        mBinding.drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                    if (!Pref.getValue(DashBoardMainActivity.this, Constants.PREF_USER_FB_ID, "").equalsIgnoreCase("")) {
                        Log.e("DashMain","Resume11");
                        mBinding.lnFbProfile.setVisibility(View.VISIBLE);
                        mBinding.tvFBUsername.setText(Pref.getValue(DashBoardMainActivity.this, Constants.PREF_USER_FB_NAME, ""));
                        Glide.with(DashBoardMainActivity.this).load(Pref.getValue(DashBoardMainActivity.this, Constants.PREF_USER_FB_IMAGE, "")).transform(new Utils.CircleTransform(DashBoardMainActivity.this)).placeholder(R.mipmap.boy).into(mBinding.imgFBUserimg);
                    } else {
                        Log.e("DashMain","else222");
                        mBinding.lnFbProfile.setVisibility(View.GONE);
                    }
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("DashMain","%%%");
        if (!Pref.getValue(DashBoardMainActivity.this, Constants.PREF_USER_FB_ID, "").equalsIgnoreCase("")) {
            Log.e("DashMain","Resume");
            mBinding.lnFbProfile.setVisibility(View.VISIBLE);
            mBinding.tvFBUsername.setText(Pref.getValue(DashBoardMainActivity.this, Constants.PREF_USER_FB_NAME, ""));
            Glide.with(DashBoardMainActivity.this).load(Pref.getValue(DashBoardMainActivity.this, Constants.PREF_USER_FB_IMAGE, "")).transform(new Utils.CircleTransform(DashBoardMainActivity.this)).placeholder(R.mipmap.boy).into(mBinding.imgFBUserimg);
        } else {
            Log.e("DashMain","else");
            mBinding.lnFbProfile.setVisibility(View.GONE);
        }

    }




    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    //Home, Leaderboard, Challenges, Reports, feedback, remove ads, information, contact us.
    public void setdrawer() {
        navDrawerItems = new ArrayList<String>();
        navDrawerItems.add("Home");
        //navDrawerItems.add("Leaderboard");
        navDrawerItems.add("Challenges");
        navDrawerItems.add("Notifications");
        //  navDrawerItems.add("Facebook Friend");
        navDrawerItems.add("Reports");
        navDrawerItems.add("Feedback");
        navDrawerItems.add("Remove Ads");
        navDrawerItems.add("Information");
        navDrawerItems.add("Contact us");
        //  navDrawerItems.add("About us");
        // navDrawerItems.add("Privacy Policy");


        navDrawerItems_icons = new ArrayList<Integer>();
        navDrawerItems_icons.add(R.mipmap.home);
        //navDrawerItems_icons.add(R.mipmap.leaderboard_drawer_img);
        navDrawerItems_icons.add(R.mipmap.challengers_img);
        navDrawerItems_icons.add(R.mipmap.notification_icon_img);
        navDrawerItems_icons.add(R.mipmap.report);
        navDrawerItems_icons.add(R.mipmap.feedback);
        navDrawerItems_icons.add(R.mipmap.remove_add);
        navDrawerItems_icons.add(R.mipmap.information);
        navDrawerItems_icons.add(R.mipmap.contact_us);
        //  navDrawerItems_icons.add(R.mipmap.about_us);
        //  navDrawerItems_icons.add(R.mipmap.privacy);


        mBinding.drawerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mBinding.drawerLayout.openDrawer(Gravity.LEFT);



            }
        });

        final NavDrawerListAdapter adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems, navDrawerItems_icons);
        mBinding.listSlidermenu.setAdapter(adapter);

    }

    public void StatusBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

    }


    /**
     * Diplaying fragment view for selected nav drawer list item
     */
    private void displayView(int position) {
        // update the main content by replacing fragments

        switch (position) {

            case 0:
                if (!Pref.getValue(DashBoardMainActivity.this, "drawer_value", "").equals("0")) {
                    DashboardFragment_new fragment = new DashboardFragment_new();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).commit();
                    Pref.setValue(DashBoardMainActivity.this, "drawer_value", "0");
                    //Pref.setValue(DashBoardMainActivity.this, "facebook_request", "0");
                    Log.e("Dashboard", "111");
                } else {
                    DashboardFragment_new fragment = new DashboardFragment_new();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).commit();
                    Pref.setValue(DashBoardMainActivity.this, "facebook_request", "0");
                    Pref.setValue(DashBoardMainActivity.this, "reload_data", "1");
                    Log.e("Dashboard", "%%%");
                }
                mBinding.drawerLayout.closeDrawer(Gravity.LEFT);
                break;

            /*case 1:
                if (!Pref.getValue(DashBoardMainActivity.this, "drawer_value", "").equals("1")) {

                    LeaderboardUserListFragment fragment_remove = new LeaderboardUserListFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment_remove).commit();
                    Pref.setValue(DashBoardMainActivity.this, "drawer_value", "1");


                }
                mBinding.drawerLayout.closeDrawer(Gravity.LEFT);
                break;*/
            case 1:

                if (!Pref.getValue(DashBoardMainActivity.this, "drawer_value", "").equals("1")) {

                    // ChallengerFriendsListFragment fragment_remove = new ChallengerFriendsListFragment();
                    MyChallengersAcceptedFacebookListFragment fragment_remove = new MyChallengersAcceptedFacebookListFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment_remove).addToBackStack(null).commit();
                    Pref.setValue(DashBoardMainActivity.this, "drawer_value", "1");
                }
                mBinding.drawerLayout.closeDrawer(Gravity.LEFT);


                break;

            case 2:

                if (!Pref.getValue(DashBoardMainActivity.this, "drawer_value", "").equals("2")) {

                    // FacebookFrndRequestFragment fragment_remove = new FacebookFrndRequestFragment();
                    NotificationofChallengesListFragment fragment_remove = new NotificationofChallengesListFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment_remove).addToBackStack(null).commit();
                    Pref.setValue(DashBoardMainActivity.this, "drawer_value", "2");
                }
                mBinding.drawerLayout.closeDrawer(Gravity.LEFT);


                break;
            case 3:
                mBinding.drawerLayout.closeDrawer(Gravity.LEFT);
               /* Intent intent1 = new Intent(Intent.ACTION_SEND);
                intent1.setType("message/rfc822");
                intent1.putExtra(Intent.EXTRA_EMAIL  , new String[]{"berry1804@gmail.com"});
                intent1.putExtra(Intent.EXTRA_SUBJECT, "Report");
               // i.putExtra(Intent.EXTRA_TEXT   , "body of email");
                try {
                    startActivity(Intent.createChooser(intent1, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {

                }*/
                //  if(Pref.getValue(DashBoardMainActivity.this,"add_display","").equalsIgnoreCase("0")) {

                if (!Pref.getValue(DashBoardMainActivity.this, "drawer_value", "").equals("3")) {
                    mBinding.drawerLayout.closeDrawer(Gravity.LEFT);
                    ReportDashboardFragment fragment_report = new ReportDashboardFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment_report).addToBackStack(null).commit();
                    Pref.setValue(DashBoardMainActivity.this, "drawer_value", "3");
                }
              /*  }else
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(DashBoardMainActivity.this).create();
                    alertDialog.setMessage("This feature is available for paid user only. Do you want proceed to puchase the feature?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    mBinding.drawerLayout.closeDrawer(Gravity.LEFT);
                                    RemoveAdsFragment fragment_remove = new RemoveAdsFragment();
                                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment_remove).commit();
                                    Pref.setValue(DashBoardMainActivity.this, "drawer_value", "3");

                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    DashboardFragment_new fragment = new DashboardFragment_new();
                                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).commit();
                                    Pref.setValue(DashBoardMainActivity.this, "drawer_value", "0");

                                }
                            });

                    alertDialog.show();

                }*/

                break;
            case 4:

                mBinding.drawerLayout.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"berry1804@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                // i.putExtra(Intent.EXTRA_TEXT   , "body of email");
                try {
                    startActivity(Intent.createChooser(intent, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {

                }


                break;

            case 5:

                if (!Pref.getValue(DashBoardMainActivity.this, "drawer_value", "").equals("5")) {
                    mBinding.drawerLayout.closeDrawer(Gravity.LEFT);
                    RemoveAdsFragment fragment_remove = new RemoveAdsFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment_remove).addToBackStack(null).commit();
                    Pref.setValue(DashBoardMainActivity.this, "drawer_value", "5");
                }


                mBinding.drawerLayout.closeDrawer(Gravity.LEFT);
                // /  opendialog((getResources().getString(R.string.app_name)), "All Ads removed!", true, false);

                break;

            case 6:
                if (!Pref.getValue(DashBoardMainActivity.this, "drawer_value", "").equals("6")) {
                    InformationFragment fragment_info = new InformationFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment_info).addToBackStack(null).commit();
                    Pref.setValue(DashBoardMainActivity.this, "drawer_value", "6");
                }
                mBinding.drawerLayout.closeDrawer(Gravity.LEFT);


                break;

            case 7:

                mBinding.drawerLayout.closeDrawer(Gravity.LEFT);
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"berry1804@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Contact Us");
                // i.putExtra(Intent.EXTRA_TEXT   , "body of email");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {

                }


                break;
            default:
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(getLocalClassName(), "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        super.onActivityResult(requestCode, resultCode, data);
       /* try {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                fragment.onActivityResult(requestCode, resultCode, data);
                Log.d("Activity", "ON RESULT CALLED");
            }
        } catch (Exception e) {
            Log.d("ERROR", e.toString());
        }*/

        // Pass on the activity result to the helper for handling
        if (data != null && data.getData() != null) {
            if (!RemoveAdsFragment.bp.handleActivityResult(requestCode, resultCode, data)) {
                // not handled, so handle it ourselves (here's where you'd
                // perform any handling of activity results not related to in-app
                // billing...
                super.onActivityResult(requestCode, resultCode, data);
            } else {
                Log.d(getLocalClassName(), "onActivityResult handled by IABUtil.");
            }
        } else {
            Log.e("Dash", "Else");
        }


    }


    public Fragment getCurrentFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_main_container);
        return currentFragment;

    }
}
