package com.zeeba.Model;

import java.util.ArrayList;

import io.realm.RealmObject;

/**
 * Created by aipxperts on 3/4/17.
 */

public class SocialQuestions {

    String id;
    String q_id;
    String question;
    String days;
    String expire_date;
    String answer_given;
    String is_paid_cate;
    String selected_answer;

    ArrayList<SocialAnswerImage> img_url=new ArrayList<>();


    public String getQ_id() {
        return q_id;
    }

    public void setQ_id(String q_id) {
        this.q_id = q_id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getExpire_date() {
        return expire_date;
    }

    public void setExpire_date(String expire_date) {
        this.expire_date = expire_date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<SocialAnswerImage> getImg_url() {
        return img_url;
    }

    public void setImg_url(ArrayList<SocialAnswerImage> img_url) {
        this.img_url = img_url;
    }

    public String getAnswer_given() {
        return answer_given;
    }

    public void setAnswer_given(String answer_given) {
        this.answer_given = answer_given;
    }


    public String getIs_paid_cate() {
        return is_paid_cate;
    }

    public void setIs_paid_cate(String is_paid_cate) {
        this.is_paid_cate = is_paid_cate;
    }


    public String getSelected_answer() {
        return selected_answer;
    }

    public void setSelected_answer(String selected_answer) {
        this.selected_answer = selected_answer;
    }
}
