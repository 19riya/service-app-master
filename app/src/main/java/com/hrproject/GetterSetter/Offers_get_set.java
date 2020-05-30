package com.hrproject.GetterSetter;

import java.util.ArrayList;

public class Offers_get_set {

    String image,fees,validity,error,msg;
    public String getWrite_msg() {
        return write_msg;
    }

    public void setWrite_msg(String write_msg) {
        this.write_msg = write_msg;
    }

    String write_msg;
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getError() {
        return error;
    }

    String offer_id;
    String date;

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    String days;

    public String getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(String offer_id) {
        this.offer_id = offer_id;
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

    public String getDescription() {
        return description;
    }

    ArrayList<Offers_get_set> datalist=new ArrayList<>();

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMax_discount() {
        return max_discount;
    }

    public void setMax_discount(String max_discount) {
        this.max_discount = max_discount;
    }

    public String getMax_use() {
        return max_use;
    }

    public void setMax_use(String max_use) {
        this.max_use = max_use;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    String description;
    String title;
    String max_discount;
    String max_use;
    String code;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    String message;
    String type;


}
