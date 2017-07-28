package com.zeeba.Model;

/**
 * Created by aipxperts-ubuntu-01 on 13/5/17.
 */

public class ReportList  {

    String report_id;
    String ans_name;
    String percentage;
    String report_image;

    public String getReport_id() {
        return report_id;
    }

    public void setReport_id(String report_id) {
        this.report_id = report_id;
    }

    public String getAns_name() {
        return ans_name;
    }

    public void setAns_name(String ans_name) {
        this.ans_name = ans_name;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getReport_image() {

        return report_image;
    }

    public void setReport_image(String report_image) {
        this.report_image = report_image;
    }
}
