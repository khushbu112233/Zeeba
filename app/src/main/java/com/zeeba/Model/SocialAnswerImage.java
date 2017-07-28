package com.zeeba.Model;

import io.realm.RealmObject;

/**
 * Created by aipxperts on 3/4/17.
 */

public class SocialAnswerImage extends RealmObject {

    String ans_img;
    String ans_id;
    String q_id;
    String name;
    String priority;



    public String getAns_img() {
        return ans_img;
    }

    public void setAns_img(String ans_img) {
        this.ans_img = ans_img;
    }

    public String getAns_id() {
        return ans_id;
    }

    public void setAns_id(String ans_id) {
        this.ans_id = ans_id;
    }

    public String getQ_id() {
        return q_id;
    }

    public void setQ_id(String q_id) {
        this.q_id = q_id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
