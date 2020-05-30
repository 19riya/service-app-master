package com.hrproject.GetterSetter;

import android.app.Application;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hrproject.FirebaseClasses.ExampleNotificationOpenHandler;
import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import java.io.File;
import java.util.ArrayList;

public class GlobalVariables extends Application
{
    public static GlobalVariables mInstance;

    public static GlobalVariables getInstance() {
        if (mInstance == null)
            mInstance = new GlobalVariables();
        return mInstance;
    }

    String name;
    String dob;
    String email;
    String password;
    String addres1;

    String user_name;

    public String getEmployee_name() {
        return employee_name;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }

    String employee_name;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_address() {
        return user_address;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    String user_address;

    String user_id;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    String vendor_id;

    public String getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(String vendor_id) {
        this.vendor_id = vendor_id;
    }


    public String getService_status() {
        return service_status;
    }

    public void setService_status(String service_status) {
        this.service_status = service_status;
    }

    String service_status;

    public ArrayList<Get_Set> getUpdate_location() {
        return update_location;
    }

    public void setUpdate_location(ArrayList<Get_Set> update_location) {
        this.update_location = update_location;
    }

    public ArrayList<Offers_get_set> getOffer_list() {
        return offer_list;
    }

    public void setOffer_list(ArrayList<Offers_get_set> offer_list) {
        this.offer_list = offer_list;
    }

    ArrayList<Get_Set> update_location=new ArrayList<>();
    ArrayList<Offers_get_set> offer_list=new ArrayList<>();

    public ArrayList<Get_Set> getCategory_list() {
        return category_list;
    }

    public void setCategory_list(ArrayList<Get_Set> category_list) {
        this.category_list = category_list;
    }

    ArrayList<Get_Set> category_list=new ArrayList<>();


    public ArrayList<Slider_Get_Set> getViewPager() {
        return viewPager;
    }

    public void setViewPager(ArrayList<Slider_Get_Set> viewPager) {
        this.viewPager = viewPager;
    }

    ArrayList<Slider_Get_Set> viewPager =new ArrayList<>();

    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    String mobile;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddres1() {
        return addres1;
    }

    public void setAddres1(String addres1) {
        this.addres1 = addres1;
    }

    public String getAddres2() {
        return addres2;
    }
    public void setAddres2(String addres2) {
        this.addres2 = addres2;
    }
    String addres2;

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public File getImg_Url() {
        return img_Url;
    }

    public void setImg_Url(File img_Url) {
        this.img_Url = img_Url;
    }

    File img_Url;

    String user_image;

    public File getFrontImg_Url() {
        return frontImg_Url;
    }

    public void setFrontImg_Url(File frontImg_Url) {
        this.frontImg_Url = frontImg_Url;
    }

    public File getBackImg_Url() {
        return backImg_Url;
    }

    public void setBackImg_Url(File backImg_Url) {
        this.backImg_Url = backImg_Url;
    }

    File frontImg_Url;
    File backImg_Url;

    public String getUser_descrp() {
        return user_descrp;
    }

    public void setUser_descrp(String user_descrp) {
        this.user_descrp = user_descrp;
    }

    String user_descrp;

    public String getLatitude() {
        return latitude;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    String unique_id;

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    String latitude,longitude;
    String service_otp;

    public String getService_otp() {
        return service_otp;
    }

    public String getService_amt() {
        return service_amt;
    }

    public void setService_amt(String service_amt) {
        this.service_amt = service_amt;
    }

    String service_amt;

    public void setService_otp(String service_otp) {
        this.service_otp = service_otp;
    }
// VENDOR FIELDS

    String vendor_skill_description;

    public String getVendor_skill_description() {
        return vendor_skill_description;
    }

    public void setVendor_skill_description(String vendor_skill_description) {
        this.vendor_skill_description = vendor_skill_description;
    }

    public String getVendor_experience() {
        return vendor_experience;
    }

    public void setVendor_experience(String vendor_experience) {
        this.vendor_experience = vendor_experience;
    }

    String vendor_experience;

    public File getVenfrontImg_Url() {
        return venfrontImg_Url;
    }

    public void setVenfrontImg_Url(File venfrontImg_Url) {
        this.venfrontImg_Url = venfrontImg_Url;
    }

    public File getVenbackImg_Url() {
        return venbackImg_Url;
    }

    public void setVenbackImg_Url(File venbackImg_Url) {
        this.venbackImg_Url = venbackImg_Url;
    }

    public File getVenPoliceImg_Url() {
        return venPoliceImg_Url;
    }

    public void setVenPoliceImg_Url(File venPoliceImg_Url) {
        this.venPoliceImg_Url = venPoliceImg_Url;
    }

    public File getVenExperImg_Url() {
        return venExperImg_Url;
    }

    public void setVenExperImg_Url(File venExperImg_Url) {
        this.venExperImg_Url = venExperImg_Url;
    }

    File venfrontImg_Url;
    File venbackImg_Url;
    File venPoliceImg_Url;
    File venExperImg_Url;


    public File getVenImg_Url() {
        return venImg_Url;
    }

    public void setVenImg_Url(File venImg_Url) {
        this.venImg_Url = venImg_Url;
    }

    File venImg_Url;

    String skill_category;
    String sub_category;

    public String getSkill_category() {
        return skill_category;
    }

    public void setSkill_category(String skill_category) {
        this.skill_category = skill_category;
    }

    public String getSub_category() {
        return sub_category;
    }

    public void setSub_category(String sub_category) {
        this.sub_category = sub_category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    String description;
    String experience;


    String vendor_name;
    String vendor_dob;
    String vendor_image;
    String vendor_mail;
    String vendor_password;
    String vendor_address;

    public String getVendor_mobile() {
        return vendor_mobile;
    }

    public void setVendor_mobile(String vendor_mobile) {
        this.vendor_mobile = vendor_mobile;
    }

    String vendor_mobile;
    String vendor_front_image;
    String vendor_back_image;
    String vendor_description;

    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    public String getVendor_dob() {
        return vendor_dob;
    }

    public void setVendor_dob(String vendor_dob) {
        this.vendor_dob = vendor_dob;
    }

    public String getVendor_image() {
        return vendor_image;
    }

    public void setVendor_image(String vendor_image) {
        this.vendor_image = vendor_image;
    }

    public String getVendor_mail() {
        return vendor_mail;
    }

    public void setVendor_mail(String vendor_mail) {
        this.vendor_mail = vendor_mail;
    }

    public String getVendor_password() {
        return vendor_password;
    }

    public void setVendor_password(String vendor_password) {
        this.vendor_password = vendor_password;
    }

    public String getVendor_address() {
        return vendor_address;
    }

    public void setVendor_address(String vendor_address) {
        this.vendor_address = vendor_address;
    }

    public String getVendor_front_image() {
        return vendor_front_image;
    }

    public void setVendor_front_image(String vendor_front_image) {
        this.vendor_front_image = vendor_front_image;
    }

    public String getVendor_back_image() {
        return vendor_back_image;
    }

    public void setVendor_back_image(String vendor_back_image) {
        this.vendor_back_image = vendor_back_image;
    }

    public String getVendor_description() {
        return vendor_description;
    }

    public void setVendor_description(String vendor_description) {
        this.vendor_description = vendor_description;
    }

    public String getVendor_id_type() {
        return vendor_id_type;
    }

    public void setVendor_id_type(String vendor_id_type) {
        this.vendor_id_type = vendor_id_type;
    }

    String vendor_id_type;

    public String getLong_status() {
        return long_status;
    }

    public void setLong_status(String long_status) {
        this.long_status = long_status;
    }

    String long_status;

    public String getVendor_police_verify() {
        return vendor_police_verify;
    }

    public void setVendor_police_verify(String vendor_police_verify) {
        this.vendor_police_verify = vendor_police_verify;
    }

    String vendor_police_verify;






    // product description bottom dialogs
    public ArrayList<Offers_get_set> getOffers_get_sets() {
        return offers_get_sets;
    }
    public void setOffers_get_sets(ArrayList<Offers_get_set> offers_get_sets) {
        this.offers_get_sets = offers_get_sets;
    }
    ArrayList<Offers_get_set> offers_get_sets=new ArrayList<>();


    public String getAccepted_vendor_id() {
        return accepted_vendor_id;
    }

    public void setAccepted_vendor_id(String accepted_vendor_id) {
        this.accepted_vendor_id = accepted_vendor_id;
    }

    String accepted_vendor_id;

    public String getAccepted_user_id() {
        return accepted_user_id;
    }

    public void setAccepted_user_id(String accepted_user_id) {
        this.accepted_user_id = accepted_user_id;
    }

    String accepted_user_id;

}
