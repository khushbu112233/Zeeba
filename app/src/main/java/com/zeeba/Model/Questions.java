package com.zeeba.Model;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by aipxperts on 3/4/17.
 */

public class Questions extends RealmObject{

    String id;
    String q_id;
    String question;
    String days;
    String points;
    String point_criteria;
    String expire_date;
    String visible_to_user;


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

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getPoint_criteria() {
        return point_criteria;
    }

    public void setPoint_criteria(String point_criteria) {
        this.point_criteria = point_criteria;
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

    public String getVisible_to_user() {
        return visible_to_user;
    }

    public void setVisible_to_user(String visible_to_user) {
        this.visible_to_user = visible_to_user;
    }
}
