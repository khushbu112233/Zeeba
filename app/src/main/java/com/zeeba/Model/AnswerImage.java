package com.zeeba.Model;

import io.realm.RealmObject;

/**
 * Created by aipxperts on 3/4/17.
 */

public class AnswerImage extends RealmObject {
    String ans_img;
    String ans_id;
    String q_id;
    String category_id;
    String name;
    String priority;
    String point;
    String sound;
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

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
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

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }
}
