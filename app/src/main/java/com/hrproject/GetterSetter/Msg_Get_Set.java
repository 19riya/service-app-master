package com.hrproject.GetterSetter;

import java.util.ArrayList;

public class Msg_Get_Set {
    public String getWrite_msg() {
        return write_msg;
    }

    public void setWrite_msg(String write_msg) {
        this.write_msg = write_msg;
    }

    String write_msg;

    String error;
    String rating;

    ArrayList<Offers_get_set> result;

    public ArrayList<Offers_get_set> getResult() {
        return result;
    }

    public void setResult(ArrayList<Offers_get_set> result) {
        this.result = result;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRating_count() {
        return rating_count;
    }

    public void setRating_count(String rating_count) {
        this.rating_count = rating_count;
    }

    String rating_count;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    String msg;

    public String getVendor_msg() {
        return vendor_msg;
    }

    public void setVendor_msg(String vendor_msg) {
        this.vendor_msg = vendor_msg;
    }

    String vendor_msg;


    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    public ArrayList<Offers_get_set> getUser_msg() {
        return user_msg;
    }

    public void setUser_msg(ArrayList<Offers_get_set> user_msg) {
        this.user_msg = user_msg;
    }

    ArrayList<Offers_get_set> user_msg=new ArrayList<>();

    String user_name;
    String profile_image;
    String vendor_name;
}
