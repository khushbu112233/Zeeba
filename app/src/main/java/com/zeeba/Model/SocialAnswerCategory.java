package com.zeeba.Model;

import io.realm.RealmObject;

/**
 * Created by aipxperts on 31/3/17.
 */

public class SocialAnswerCategory extends RealmObject {

    String que_id;
    String ans_id;
    String cate_id;


    public String getQue_id() {
        return que_id;
    }

    public void setQue_id(String que_id) {
        this.que_id = que_id;
    }

    public String getAns_id() {
        return ans_id;
    }

    public void setAns_id(String ans_id) {
        this.ans_id = ans_id;
    }

    public String getCate_id() {
        return cate_id;
    }

    public void setCate_id(String cate_id) {
        this.cate_id = cate_id;
    }
}
