package com.zeeba.Activity.Dashboard;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.zeeba.R;
import com.zeeba.databinding.RemoveAdsLayoutBinding;
import com.zeeba.utils.FontCustom;
import com.zeeba.utils.Pref;

/**
 * Created by aipxperts-ubuntu-01 on 10/5/17.
 */

public class RemoveAdsFragment extends Fragment implements BillingProcessor.IBillingHandler{
    RemoveAdsLayoutBinding mBinding;
    View rootView;
    Context context;
    public static BillingProcessor bp;
    public static boolean isPurchase = false;

    int is_restore_purchase_click=0;
    int is_remove_click=0;
    int is_restore_click=0;
    public static String KEY_REMOVE_ADDS ="com.zeeba.removeads";
    //"android.test.purchased";//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.remove_ads_layout, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();
        bp = new BillingProcessor(getActivity(), null, this);
        bp.initialize();
        isPurchase = bp.isPurchased(KEY_REMOVE_ADDS);
        if(isPurchase)
        {
            Pref.setValue(context,"add_display","0");
        }else{
            Pref.setValue(context,"add_display","1");
        }

        ((DashBoardMainActivity)getActivity()).mBinding.imgRefreshData.setVisibility(View.GONE);
        mBinding.txtRemoveAds.setEnabled(true);
        mBinding.txtRestoreAds.setTypeface(FontCustom.setFontBold(context));
        mBinding.txtRemoveAds.setTypeface(FontCustom.setFontBold(context));
        mBinding.txtRemoveDes.setTypeface(FontCustom.setFontBold(context));
        mBinding.txtRemoveAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                is_remove_click=1;
                if(bp.isPurchased(KEY_REMOVE_ADDS)) {
                    opendialog((getResources().getString(R.string.app_name)), "All Ads already removed!", true, false);
                }else
                {
                    opendialog((getResources().getString(R.string.app_name)), "All Ads removed!", true, false);
                }
                // openDialog1(KEY_REMOVE_ADDS);
            }
        });
        mBinding.txtRestoreAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                is_restore_click=1;

                mBinding.txtRestoreAds.setEnabled(false);

                Log.e("purchase_product", bp.getPurchaseListingDetails(KEY_REMOVE_ADDS) + "---" + bp.getPurchaseTransactionDetails(KEY_REMOVE_ADDS) + "--" + bp.loadOwnedPurchasesFromGoogle() + " " + bp.listOwnedProducts());

                if (bp.isPurchased(KEY_REMOVE_ADDS)) {
                    opendialog_restore((getResources().getString(R.string.app_name)), "Restored successfully!", true, false);

                } else {
                    opendialog((getResources().getString(R.string.app_name)), "First purchase then only you can restore it!", true, false);
                }


            }
        });
        return rootView;
    }


    public void opendialog_restore(String title, String msg, boolean isVisiblePositive, boolean isVisibleNagative) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //Uncomment the below code to Set the message and title from the strings.xml file
        //builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

        //Setting message manually and performing action on button click
        mBinding.txtRestoreAds.setEnabled(true);
        builder.setMessage(msg)
                .setCancelable(false);
        if (isVisiblePositive) {
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    is_restore_purchase_click=0;
                    Log.e("check_purchase",""+bp.isPurchased(KEY_REMOVE_ADDS));
                    isPurchase = bp.isPurchased(KEY_REMOVE_ADDS);
                    if(isPurchase)
                    {
                        Pref.setValue(context,"add_display","0");
                    }else{
                        Pref.setValue(context,"add_display","1");
                    }
                    getActivity().getSupportFragmentManager().popBackStack();
                    dialog.cancel();
                }
            });
        }
        if (isVisibleNagative) {
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //  Action for 'NO' Button
                    is_restore_purchase_click=0;
                    dialog.cancel();
                }
            });
        }

        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle(title);
        alert.show();
    }

    private void openDialog1(final String KEY) {


        final Dialog openDialog = new Dialog(context);
        openDialog.setContentView(R.layout.cust_doalog);


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.gravity = Gravity.CENTER;

        Window window = openDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        TextView txtCancel = null, txtPurchse = null;


        txtCancel = (TextView) openDialog.findViewById(R.id.txtCancel);
        txtPurchse = (TextView) openDialog.findViewById(R.id.txtPurchse);

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog.cancel();

            }
        });


        txtPurchse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.cancel();
                if (!isPurchase) {
                    bp.purchase(getActivity(), KEY);
                }
            }
        });

        openDialog.show();
    }
    public void opendialog(String title, String msg, boolean isVisiblePositive, boolean isVisibleNagative) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //Uncomment the below code to Set the message and title from the strings.xml file
        //builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

        if(is_remove_click==1) {
            mBinding.txtRemoveAds.setEnabled(false);
            mBinding.txtRestoreAds.setEnabled(true);
        }
        if(is_restore_click==1)
        {
            mBinding.txtRemoveAds.setEnabled(true);
            mBinding.txtRestoreAds.setEnabled(false);
        }
        //Setting message manually and performing action on button click
        builder.setMessage(msg).setCancelable(false);
        if (isVisiblePositive) {
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    bp.purchase(getActivity(), KEY_REMOVE_ADDS);
                    dialog.cancel();
                    mBinding.txtRemoveAds.setEnabled(true);
                    mBinding.txtRestoreAds.setEnabled(true);
                }
            });
        }
        if (isVisibleNagative) {
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //  Action for 'NO' Button
                    dialog.cancel();
                }
            });
        }
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(is_remove_click==1) {
                    mBinding.txtRemoveAds.setEnabled(true);
                    mBinding.txtRestoreAds.setEnabled(true);
                }
                if(is_restore_click==1)
                {
                    mBinding.txtRemoveAds.setEnabled(true);
                    mBinding.txtRestoreAds.setEnabled(true);
                }
            }
        });

        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle(title);
        alert.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        bp = new BillingProcessor(getActivity(), null, this);
        bp.initialize();
        isPurchase = bp.isPurchased(KEY_REMOVE_ADDS);
        if(isPurchase)
        {
            Pref.setValue(context,"add_display","0");
        }else{
            Pref.setValue(context,"add_display","1");
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Log.e("0","product_purchased");
        if (productId.equals(KEY_REMOVE_ADDS)) {
            isPurchase = bp.isPurchased(KEY_REMOVE_ADDS);
        }
        if(isPurchase)
        {
            Pref.setValue(context,"add_display","0");
        }else{
            Pref.setValue(context,"add_display","1");
        }
        is_restore_purchase_click=1;
        mBinding.txtRestoreAds.setEnabled(true);
        mBinding.txtRemoveAds.setEnabled(true);
    }

    @Override
    public void onPurchaseHistoryRestored() {
        Log.e("0","product_historyRestored");


        bp = new BillingProcessor(getActivity(), null, this);
        bp.initialize();
        isPurchase = bp.isPurchased(KEY_REMOVE_ADDS);
        if(isPurchase)
        {
            Toast.makeText(context,"Your purchase is restore automatically,because you already purchase.",Toast.LENGTH_LONG).show();
            Pref.setValue(context,"add_display","0");
        }else{
            Pref.setValue(context,"add_display","1");
        }
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Log.e("0","billing error"+errorCode+error);
        //   Toast.makeText(context,"Please first add google account !",Toast.LENGTH_LONG).show();
        is_restore_purchase_click=0;
        mBinding.txtRestoreAds.setEnabled(true);
        mBinding.txtRemoveAds.setEnabled(true);
    }

    @Override
    public void onBillingInitialized() {
        is_restore_purchase_click=0;
        mBinding.txtRemoveAds.setEnabled(true);
        mBinding.txtRestoreAds.setEnabled(true);
        Log.e("0","billing initialize");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                            fm.popBackStack();

                        }
                        DashboardFragment_new fragment = new DashboardFragment_new();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).commit();
                        Pref.setValue(getActivity(), "facebook_request", "0");
                        Pref.setValue(getActivity(), "reload_data", "1");
                        Pref.setValue(getActivity(), "drawer_value", "8");
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
