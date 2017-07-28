package com.zeeba.utils;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import static com.zeeba.Activity.Wizard.WizardLoadFragment.boy_selected;
import static com.zeeba.Activity.Wizard.WizardLoadFragment.girl_selected;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.zeeba.Activity.MainActivity;
import com.zeeba.Model.AnswerImage;
import com.zeeba.Model.Category;
import com.zeeba.Model.Questions;
import com.zeeba.Model.SocialAnswerCategory;
import com.zeeba.Model.SocialAnswerImage;
import com.zeeba.Model.SubCategory;
import com.zeeba.R;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static android.content.Context.ALARM_SERVICE;
import static com.zeeba.Activity.MainActivity.realm;
import static com.zeeba.Activity.Wizard.WizardLoadFragment.other_type_selected;
import static com.zeeba.Activity.Wizard.WizardLoadFragment.selected_age;
import static com.zeeba.Activity.Wizard.WizardLoadFragment.selected_age_pos;
import static com.zeeba.Activity.Wizard.WizardLoadFragment.selected_race;
import static com.zeeba.Activity.Wizard.WizardLoadFragment.selected_race_pos;

/**
 * Created by aipxperts on 10/2/17.
 */

public class Utils {

    public static int loginCount = 0;

    public static String PREF_LOGIN_TIME = "login_time";
    public static boolean isSignalProviderCalled = false;
    public static boolean isDisplayScanValue = true;

    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static class CircleTransform extends BitmapTransformation {
        public CircleTransform(Context context) {
            super(context);
        }

        @Override protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            // TODO this could be acquired from the pool too
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            return result;
        }

        @Override public String getId() {
            return getClass().getName();
        }
    }

    public static void exitApplication(final Context mContext) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle("Zeeba!");
        alertDialogBuilder.setMessage("You have been logged out of this device because you logged into another device with same credentials.");
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                final RealmResults<AnswerImage> answerImages = realm.where(AnswerImage.class).findAll();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        answerImages.deleteAllFromRealm();
                    }
                });
                final RealmResults<SocialAnswerImage> socialAnswerImages = realm.where(SocialAnswerImage.class).findAll();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        socialAnswerImages.deleteAllFromRealm();
                    }
                });
                final RealmResults<SocialAnswerCategory> socialAnswerCategories = realm.where(SocialAnswerCategory.class).findAll();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        socialAnswerCategories.deleteAllFromRealm();
                    }
                });

                final RealmResults<Questions> questionses = realm.where(Questions.class).findAll();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        questionses.deleteAllFromRealm();
                    }
                });

                final RealmResults<SubCategory> subCategories = realm.where(SubCategory.class).findAll();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        subCategories.deleteAllFromRealm();
                    }
                });

                final RealmResults<Category> categories = realm.where(Category.class).findAll();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        categories.deleteAllFromRealm();
                    }
                });

                boy_selected=0;
                girl_selected=0;
                other_type_selected=0;
                selected_race="";
                selected_race_pos=-1;
                selected_age="";
                selected_age_pos=-1;

                Pref.deleteAll(mContext);
              //  clearApplicationData(mContext);
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);


            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();

        //Toast.makeText(MainActivity.this,"logout",Toast.LENGTH_LONG).show();

    }
    public static void clearApplicationData(Context mContext) {
        File cacheDirectory = mContext.getCacheDir();
        File applicationDirectory = new File(cacheDirectory.getParent());
        if (applicationDirectory.exists()) {
            String[] fileNames = applicationDirectory.list();
            for (String fileName : fileNames) {
                if (!fileName.equals("lib")) {
                    deleteFile(new File(applicationDirectory, fileName));
                }
            }
        }
    }

    public static boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    deletedAll = deleteFile(new File(file, children[i])) && deletedAll;
                }
            } else {
                deletedAll = file.delete();
            }
        }

        return deletedAll;
    }

    public static void exitApplication1(final Context mContext, final RealmConfiguration configuration) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle("Zeeba!");
        alertDialogBuilder.setMessage("You have been logged out of this device because you logged into another device with same credentials.");
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


                Pref.deleteAll(mContext);
                Realm.deleteRealm(configuration);
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);


            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();

        //Toast.makeText(MainActivity.this,"logout",Toast.LENGTH_LONG).show();

    }

    public static String getDateCurrentTimeZone(long timestamp) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getTimeZone("UTC");
            Log.e("SignalPush", "$$ " + tz.toString());
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            //SimpleDateFormat("E, MMM d, yyyy hh:mm a");
            SimpleDateFormat sdf = new SimpleDateFormat("E, MMM d, yyyy hh:mm a");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        } catch (Exception e) {
        }
        return "";
    }

    public static String getDateCurrentTimeZoneCreateOrder(long timestamp) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getTimeZone("UTC");
            Log.e("SignalPush", "$$ " + tz.toString());
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            //SimpleDateFormat("E, MMM d, yyyy hh:mm a");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd hh:mm");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        } catch (Exception e) {
        }
        return "";
    }

    public final static boolean isValidEmail(String hex) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return checkValidation(hex, EMAIL_PATTERN);
    }

    private static boolean checkValidation(String hex, String EMAIL_PATTERN) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher;
        matcher = pattern.matcher(hex);
        return matcher.matches();
    }


    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getDateCurrentTimeZoneWithExpire(long timestamp, int valid_time) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getTimeZone("UTC");

            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            calendar.add(Calendar.HOUR, valid_time);
            Log.e("Expire", "### " + valid_time + " calender " + calendar);
//SimpleDateFormat("E, MMM d, yyyy hh:mm a");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd hh:mm");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        } catch (Exception e) {
        }
        return "";
    }

    public static BigDecimal truncateDecimal(double x, int numberofDecimals) {
        if (x > 0) {
            // Log.e("Instant", "11 " +String.valueOf(x) +  "is " +Math.round( x * Math.pow(10.0, numberofDecimals) ) / Math.pow(10.0, numberofDecimals));
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_HALF_UP);

        } else {
            //  Log.e("Instant", "22" +String.valueOf(x));
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_CEILING);

        }
    }

    public static String properCase(String inputVal) {
        if (inputVal.length() == 0) return "";
        if (inputVal.length() == 1) return inputVal.toUpperCase();
        return inputVal.substring(0, 1).toLowerCase()
                + inputVal.substring(1).toLowerCase();
    }

    public static String getSaltRandomString(String SALTRANDOMCHARS, int length) {

        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTRANDOMCHARS.length());
            salt.append(SALTRANDOMCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    public static String dateFromate(String date, String formate) {
//
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        Date startDate = null;
        String newDateString = null;
        String date1 = null, year = null, month = null;

        try {
            startDate = df.parse(date);
            date1 = "" + startDate.getDate();
            year = "" + (startDate.getYear() + 1900);
            month = "" + (startDate.getMonth() + 1);
            System.out.println("month===>>>" + (startDate.getMonth() + 1));
            System.out.println("date===>>>" + startDate.getDate());
            System.out.println("year===>>>" + (startDate.getYear() + 1900));

        } catch (Exception e) {
            e.printStackTrace();
        }

        date = month + "/" + date1 + "/" + year;
        Log.e("date ", date);
        SimpleDateFormat format = new SimpleDateFormat("MMM dd,yyyy");
        String date2 = format.format(Date.parse(date));
        return date2;

    }

    public static String seTwoDigitDecimal(String number) {
        float fNum = Float.parseFloat(number);
        String num = String.format("%.2f", fNum);
        return num;
    }


}
