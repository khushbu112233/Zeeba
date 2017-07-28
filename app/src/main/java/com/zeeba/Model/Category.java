package com.zeeba.Model;

import java.util.ArrayList;

import io.realm.RealmObject;

/**
 * Created by aipxperts on 31/3/17.
 */

public class Category extends RealmObject {

    String id;
    String name;
    String background;
    String foreground;
    String page_flag;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getForeground() {
        return foreground;
    }

    public void setForeground(String foreground) {
        this.foreground = foreground;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPage_flag() {
        return page_flag;
    }

    public void setPage_flag(String page_flag) {
        this.page_flag = page_flag;
    }



}
