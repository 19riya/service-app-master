package com.hrproject.GetterSetter;

public class profile_get_set
{

    /*name":"udir panday","email":"abd@gmail.com","mobile":"2536970700"," +
        ""address":"lalssl","second_address":"sgdndkd","dob":"2019-8-6","description":"udit mad ????????????????"," +
        ""profile_image":"http:\/\/kallibazaar.com\/service\/assets\/images\/user-305693.jpg"," +
        ""gov_id_type":"Driving Licence"," +
        ""gov_id_front_image":"http:\/\/kallibazaar.com\/service\/assets\/images\/user-888679.jpg"," +
        ""gov_id_back_image":"http:\/\/kallibazaar.com\/service\/assets\/images\/user-57289.jpg"*/

    String name;
    String email;
    String mobile;
    String dob;

    String long_status;

    public String getLong_status() {
        return long_status;
    }

    public void setLong_status(String long_status) {
        this.long_status = long_status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    String description;

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

    String error,msg;


    String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSecond_address() {
        return second_address;
    }

    public void setSecond_address(String second_address) {
        this.second_address = second_address;
    }

    String second_address;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    String profile_image;


    String experiance;

    public String getExperiance() {
        return experiance;
    }

    public void setExperiance(String experiance) {
        this.experiance = experiance;
    }

    public String getExperiance_certificate_image() {
        return experiance_certificate_image;
    }

    public void setExperiance_certificate_image(String experiance_certificate_image) {
        this.experiance_certificate_image = experiance_certificate_image;
    }

    public String getSkill_categories() {
        return skill_categories;
    }

    public void setSkill_categories(String skill_categories) {
        this.skill_categories = skill_categories;
    }

    public String getSkill_sub_categories() {
        return skill_sub_categories;
    }

    public void setSkill_sub_categories(String skill_sub_categories) {
        this.skill_sub_categories = skill_sub_categories;
    }

    public String getSkill_description() {
        return skill_description;
    }

    public void setSkill_description(String skill_description) {
        this.skill_description = skill_description;
    }

    public String getGov_id_type() {
        return gov_id_type;
    }

    public void setGov_id_type(String gov_id_type) {
        this.gov_id_type = gov_id_type;
    }

    public String getGov_id_front_image() {
        return gov_id_front_image;
    }

    public void setGov_id_front_image(String gov_id_front_image) {
        this.gov_id_front_image = gov_id_front_image;
    }

    public String getGov_id_back_image() {
        return gov_id_back_image;
    }

    public void setGov_id_back_image(String gov_id_back_image) {
        this.gov_id_back_image = gov_id_back_image;
    }

    String experiance_certificate_image;
    String skill_categories;
    String skill_sub_categories;
    String skill_description;
    String gov_id_type;
    String gov_id_front_image;
    String gov_id_back_image;
}
