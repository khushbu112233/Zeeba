package com.zeeba.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by aipxperts on 17/3/17.
 */

public class FacebookuserzeebaListModel implements Parcelable {


    public String name;
    public String id;


    protected FacebookuserzeebaListModel(Parcel in) {
        name = in.readString();
        id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FacebookuserzeebaListModel> CREATOR = new Creator<FacebookuserzeebaListModel>() {
        @Override
        public FacebookuserzeebaListModel createFromParcel(Parcel in) {
            return new FacebookuserzeebaListModel(in);
        }

        @Override
        public FacebookuserzeebaListModel[] newArray(int size) {
            return new FacebookuserzeebaListModel[size];
        }
    };
}

