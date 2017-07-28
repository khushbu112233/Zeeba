package com.zeeba.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;


/**
 * Created by dharmesh on 20/7/16.
 */
public class Constants {
    public static Context _context;

    public Constants(Context context) {
        this._context = context;
    }


    public static final String[] User_param =
            {
                    "gender",
                    "race",
                    "age",
                    "lat",
                    "long",
                    "name",
                    "fb_id",
                    "device_type",
                    "device_token"

            };


    public static final String[] Category_param =
            {
                    "token",
                    "sync"

            };

    public static final String[] SUBCategory_param =
            {
                    "token",
                    "id"
            };

    public static final String[] RESULT_param =
            {
                    "token",
                    "q_id",
                    "a_id",
                    "point",
                    "fb_id",
                    "invite_id"
            };

    public static final String[] RESULT_Social_param =
            {
                    "token",
                    "result"

            };

            //for fb challengers frnd result
    public static final String[] RESULT_OF_FB_FRND_param =
            {
                    "token",
                    "invite_id"
            };

    public static final String[] PARA_FACEBOOK_USER =
            {
                    "fb_id",
                    "device_type",
                    "device_token"

            };

    public static final String[] InviteFacebookFrndPlayingGame =
            {
                    "cate_id",
                    "subcate_id",
                    "invite_from",
                    "invite_to"
            };

    public static final String[] InviteResponseByUserAcceptorDeny =
            {
                    "invite_id",
                    "status"
            };

    //for monthly get leader board value from the subcategory

    public static final String[] LEADER_BOARD_MONTHLY_POINT_SYSTEM =
            {
                    "token",
                    "cat_id",
                    "subcat_id"

            };

    //for top ten winner name
    public static final String[] TOP_TEN_USERNAME =
            {
                    "token",
                    "name"

            };

    //for challenger list friend
    public static final String[] CHALLENGER_FRIEND_USER_LIST =
            {
                    "token"

            };

    //for notification challenges facebook list
    public static final String[] NOTIFICATION_FRND_CHALLENGES_LIST =
            {
                    "token",
                    "fb_id"

            };

    //for existing user login with facebook
    public static final String[] PARA_EXISTING_USER_LOGIN_FACEBOOK_USER =
            {
                    "token",
                    "fb_id",
                    "name",
                    "device_type",
                    "device_token"

            };

    //for my challenges list for facebook game user
    public static final String[] PARA_MY_CHALLENGES_FACEBOOK_USER =
            {
                    "token",
                    "fb_id"


            };

    //for accepted challenges list for facebook game user
    public static final String[] PARA_ACCEPTED_CHALLENGES_FACEBOOK_USER =
            {
                    "token",
                    "fb_id"


            };

    //for fb profile and image path
    public static final String FB_IMAGE_PATH = "https://graph.facebook.com/";
    public static final String FB_IMAGE_SIZE = "/picture?width=120&height=120";

    //constant value

    public static String PREF_USER_FB_ID = "PREF_USER_FB_ID";
    public static String PREF_USER_FB_IMAGE = "PREF_USER_FB_IMAGE";
    public static String PREF_USER_FB_NAME = "PREF_USER_FB_NAME";
    public static String PREF_USER_FB_TOKEN = "PREF_USER_FB_TOKEN";


    public interface ACTION {
        public static String MAIN_ACTION = "com.marothiatechs.customnotification.action.main";
        public static String INIT_ACTION = "com.marothiatechs.customnotification.action.init";
        public static String PREV_ACTION = "com.marothiatechs.customnotification.action.prev";
        public static String PLAY_ACTION = "com.marothiatechs.customnotification.action.play";
        public static String NEXT_ACTION = "com.marothiatechs.customnotification.action.next";
        public static String STARTFOREGROUND_ACTION = "com.marothiatechs.customnotification.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.marothiatechs.customnotification.action.stopforeground";

    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }


}
