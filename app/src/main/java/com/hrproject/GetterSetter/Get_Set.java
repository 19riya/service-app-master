package com.hrproject.GetterSetter;

import java.util.ArrayList;

public class Get_Set
{

    String error;

  /*  public Get_Set(String category_name, String sub_categories, String category_image) {
        this.category_name=category_name;
        this.sub_categories=sub_categories;
        this.category_image=category_image;
    }*/


  String rating;

  String vendor_id;

    public String getRating() {
        return rating;
    }

    String vendor_description,vendor_experiance,vendor_rating,vendor_name;

    public String getVendor_description() {
        return vendor_description;
    }

    public void setVendor_description(String vendor_description) {
        this.vendor_description = vendor_description;
    }

    public String getVendor_experiance() {
        return vendor_experiance;
    }

    public void setVendor_experiance(String vendor_experiance) {
        this.vendor_experiance = vendor_experiance;
    }

    public String getVendor_rating() {
        return vendor_rating;
    }

    public void setVendor_rating(String vendor_rating) {
        this.vendor_rating = vendor_rating;
    }

    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

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

    public ArrayList<Offers_get_set> getResult() {
        return result;
    }

    public void setResult(ArrayList<Offers_get_set> result) {
        this.result = result;
    }

    ArrayList<Offers_get_set> result=new ArrayList<>();

    String category_id;
    String category_name;

    public String getSub_categories() {
        return sub_categories;
    }

    public void setSub_categories(String sub_categories) {
        this.sub_categories = sub_categories;
    }

    String sub_categories;

    public String getCategory_image() {
        return category_image;
    }

    public void setCategory_image(String category_image) {
        this.category_image = category_image;
    }

    String category_image;

    public String getSub_category() {
        return sub_category;
    }

    public void setSub_category(String sub_category) {
        this.sub_category = sub_category;
    }

    String sub_category;
    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getSubcategory_count() {
        return subcategory_count;
    }

    public void setSubcategory_count(String subcategory_count) {
        this.subcategory_count = subcategory_count;
    }

    String profile_image;

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;

    String subcategory_count;

    public String getVendor_image() {
        return vendor_image;
    }

    public void setVendor_image(String vendor_image) {
        this.vendor_image = vendor_image;
    }

    String vendor_image;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    String user_id;

    public String getSub_category_id() {
        return sub_category_id;
    }

    public void setSub_category_id(String sub_category_id) {
        this.sub_category_id = sub_category_id;
    }



    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    String unique_id;
    public String getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(String vendor_id) {
        this.vendor_id = vendor_id;
    }

    String sub_category_id;

    public String getRemaining_time() {
        return remaining_time;
    }

    public void setRemaining_time(String remaining_time) {
        this.remaining_time = remaining_time;
    }

    String remaining_time;

    public String getSub_category_image() {
        return sub_category_image;
    }

    public void setSub_category_image(String sub_category_image) {
        this.sub_category_image = sub_category_image;
    }

    String sub_category_image;

    String experiance_image;

    public String getExperiance_image() {
        return experiance_image;
    }

    public void setExperiance_image(String experiance_image) {
        this.experiance_image = experiance_image;
    }

    public String getExperiance() {
        return experiance;
    }

    public void setExperiance(String experiance) {
        this.experiance = experiance;
    }

    String experiance;
}
