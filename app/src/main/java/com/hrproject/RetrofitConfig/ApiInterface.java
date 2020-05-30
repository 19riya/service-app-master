package com.hrproject.RetrofitConfig;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.hrproject.GetterSetter.Address_get_set;
import com.hrproject.GetterSetter.Get_Set;
import com.hrproject.GetterSetter.Msg_Get_Set;
import com.hrproject.GetterSetter.Offers_get_set;
import com.hrproject.GetterSetter.Slider_Get_Set;
import com.hrproject.GetterSetter.profile_get_set;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface
{

    // all skills list in vendor
    @FormUrlEncoded
    @POST("category_list")
    Call<ArrayList<Get_Set>>category_list(@Field("language_type") String language_type);



    // user Slider
    @FormUrlEncoded
    @POST("view_slider")
    Call<ArrayList<Slider_Get_Set>>view_slider(@Field("status") String status);


    // all Sub skills list in vendor
    @FormUrlEncoded
    @POST("sub_category_list")
    Call<ArrayList<Get_Set>>sub_category_list(@Field("category_id") String category_id,
                                              @Field("language_type") String language_type);

    @FormUrlEncoded
    @POST("user_registration")
    Call<ArrayList<Get_Set>>getUser_registration(@Field("name") String name , @Field("email") String email ,
                                              @Field("mobile") String mobile,@Field("dob") String dob,
                                              @Field("password") String password,@Field("address") String address,
                                              @Field("address_second") String address_second,
                                              @Field("description") String description,
                                              @Field("gov_id_type") String gov_id_type,
                                              @Field("gov_id_front") String gov_id_front,
                                              @Field("gov_id_back") String gov_id_back,
                                              @Field("profile_image") String profile_image);

    @FormUrlEncoded
    @POST("vendor_registration")
    Call<ArrayList<Get_Set>>getVendor_registration(@Field("name") String name,@Field("email") String email,
                                                 @Field("mobile") String mobile,@Field("dob") String dob,
                                                 @Field("password") String password,@Field("address") String address,
                                                @Field("description") String description,
                                                 @Field("gov_id_type") String gov_id_type,
                                                 @Field("gov_id_front") String gov_id_front,
                                                 @Field("gov_id_back") String gov_id_back,
                                                 @Field("police_varification") String police_varification,
                                                 @Field("skill_description") String skill_description,
                                                 @Field("experiane_description") String experiane_description,
                                                 @Field("experiane_image") String experiane_image,
                                                   @Field("skill_category") String  skill_category,
                                                   @Field("skill_sub_category") String skill_sub_category,
                                                   @Field("profile_image") String profile_image,
                                                   @Field("long_status") String long_status);

    @FormUrlEncoded
    @POST("verify_otp")
    Call<ArrayList<Get_Set>>getverify_otp(@Field("mobile") String mobile,@Field("otp") String otp, @Field("type") String type,
                                          @Field("key") String key);

    @FormUrlEncoded
    @POST("resend_otp")
    Call<ArrayList<Get_Set>>getresend_otp(@Field("mobile") String mobile , @Field("type") String type);

    @FormUrlEncoded
    @POST("update_user_rating_review")
    Call<ArrayList<Address_get_set>>upd_user_rating_review(@Field("unique_id") String unique_id
            , @Field("user_rating") String user_rating
            , @Field("user_review") String user_review);

    @FormUrlEncoded
    @POST("update_vendor_rating_review")
    Call<ArrayList<Address_get_set>>upd_vendor_rating_review(@Field("unique_id") String unique_id
            , @Field("vendor_rating") String vendor_rating
            , @Field("vendor_review") String vendor_review);

    @FormUrlEncoded
    @POST("user_login")
    Call<ArrayList<Get_Set>>getuser_login(@Field("mobile") String mobile , @Field("password") String password);

    @FormUrlEncoded
    @POST("vendor_login")
    Call<ArrayList<Get_Set>>getvendor_login(@Field("mobile") String mobile , @Field("password") String password);

    @FormUrlEncoded
    @POST("forgot_password")
    Call<ArrayList<Get_Set>>getforgot_password(@Field("mobile") String mobile,@Field("otp") String otp,
                                               @Field("password") String password,@Field("type") String type);

    @FormUrlEncoded
    @POST("user_request")
    Call<ArrayList<Get_Set>>user_request(@Field("user_id") String user_id,@Field("cat_id") String cat_id,
                                                 @Field("subcat_id") String subcat_id,@Field("address") String address,
                                                 @Field("longitude") String longitude,@Field("latitude") String latitude,
                                                 @Field("range") String range,@Field("status") String status,
                                         @Field("code") String code, @Field("from_service_date") String from_service_date, @Field("to_service_date") String to_service_date);

    @FormUrlEncoded
    @POST("show_user_profile")
    Call<ArrayList<profile_get_set>>show_user_profile(@Field("user_id") String user_id);


    @FormUrlEncoded
    @POST("change_password")
    Call<ArrayList<Get_Set>>change_password(@Field("user_id") String user_id, @Field("old_password")String old_password,
                                            @Field("new_password") String new_password,@Field("type") String type);

    @FormUrlEncoded
    @POST("show_user_address")
    Call<ArrayList<profile_get_set>>show_user_address(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("show_vendor_profile")
    Call<ArrayList<profile_get_set>>show_vendor_profile(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("update_user_location")
    Call<ArrayList<Get_Set>>update_user_location(@Field("user_id") String user_id,
                                                         @Field("latitude") String lat,
                                                         @Field("longitude") String longi,
                                                         @Field("location") String location);

    @FormUrlEncoded
    @POST("show_vendor_basic_details")
    Call<ArrayList<Get_Set>>vendor_basic_details(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("update_vendor_location")
    Call<ArrayList<Get_Set>>update_vendor_location(@Field("user_id") String user_id,
                                                 @Field("longitude") String longi,
                                                 @Field("latitude") String lat,
                                                 @Field("location") String location);

    @GET("send_service_request")
    Call<ArrayList<Address_get_set>>send_service_request();

    @FormUrlEncoded
    @POST("update_vendor_profile")
    Call<ArrayList<profile_get_set>>update_vendor_profile(@Field("user_id") String user_id,@Field("name") String name,
                                                          @Field("dob") String dob, @Field("address") String address,
                                                          @Field("profile_image") String profile_image,
                                                          @Field("skill_category")String skill_category,
                                                          @Field("skill_sub_category") String skill_sub_category,
                                                          @Field("skill_description") String skill_description,
                                                          @Field("experiane_description") String experiane_description,
                                                          @Field("long_status") String long_status);

    @FormUrlEncoded
    @POST("update_user_profile")
    Call<ArrayList<profile_get_set>>update_user_profile(@Field("user_id") String user_id, @Field("dob") String dob,
                                                        @Field("address") String address,
                                                        @Field("address_second") String address_second,
                                                        @Field("description") String description,
                                                        @Field("profile_image") String profile_image,
                                                        @Field("name") String name);

    @FormUrlEncoded
    @POST("check_request_status")
    Call<ArrayList<Address_get_set>>check_request_status(@Field("unique_id") String unique_id);

    @FormUrlEncoded
    @POST("accept_user_request")
    Call<ArrayList<Address_get_set>>accept_user_request(@Field("unique_id") String unique_id,@Field("vendor_id") String vendor_id);

    @FormUrlEncoded
    @POST("accepted_vendor_information")
    Call<ArrayList<Address_get_set>>accepted_vendor_information(@Field("unique_id") String unique_id);

    @FormUrlEncoded
    @POST("check_service_otp")
    Call<ArrayList<Address_get_set>>check_service_otp(@Field("unique_id") String unique_id,
                                                      @Field("service_otp") String service_otp);

    @FormUrlEncoded
    @POST("vendor_service_history")
    Call<ArrayList<Address_get_set>>vendor_service_history(@Field("vendor_id") String vendor_id);

    @FormUrlEncoded
    @POST("user_service_history")
    Call<ArrayList<Address_get_set>>user_service_history(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("update_user_rating")
    Call<ArrayList<Address_get_set>>update_user_rating(@Field("unique_id") String unique_id,
                                                       @Field("user_rating") String user_rating);

    @FormUrlEncoded
    @POST("update_vendor_rating")
    Call<ArrayList<Address_get_set>>update_vendor_rating(@Field("unique_id") String unique_id,
                                                       @Field("vendor_rating") String user_rating);

    @FormUrlEncoded
    @POST("update_vendor_review")
    Call<ArrayList<Address_get_set>>update_vendor_review(@Field("unique_id") String unique_id,
                                                         @Field("vendor_review") String vendor_review);

    @FormUrlEncoded
    @POST("update_user_review")
    Call<ArrayList<Address_get_set>>update_user_review(@Field("unique_id") String unique_id,
                                                         @Field("user_review") String user_review);

    @POST("monthly_subscription")
    Call<ArrayList<Offers_get_set>>monthly_subscription();

    @FormUrlEncoded
    @POST("coupons")
    Call<ArrayList<Get_Set>>coupons(@Field("sub_cat_id") String sub_cat_id);

    @FormUrlEncoded
    @POST("top_rated_vendors")
    Call<ArrayList<Get_Set>>vendor_list(@Field("user_id") String user_id,@Field("category") String category,
                                        @Field("subcategory") String subcategory);


    @FormUrlEncoded
    @POST("max_use_offer")
    Call<ArrayList<Offers_get_set>>max_use_offer(@Field("offer_id") String offer_id,@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("send_notification")
    Call<ArrayList<Offers_get_set>>send_notification(@Field("type") String type);

    @GET("share_app")
    Call<ArrayList<Get_Set>>share_app();


    @FormUrlEncoded
    @POST("chat_history")
    Call<ArrayList<Msg_Get_Set>>chat_history(@Field("unique_id") String unique_id);

    @FormUrlEncoded
    @POST("save_payble_amount")
    Call<ArrayList<Get_Set>>getamount(@Field("unique_id") String unique_id , @Field("amount") String amount);

    @FormUrlEncoded
    @POST("complete_cancel_status")
    Call<ArrayList<Address_get_set>>service_status(@Field("unique_id") String unique_id , @Field("status") String status);

    @Multipart
    @POST("notification_vendor_chat_service")
    Call<ArrayList<Msg_Get_Set>>notification_vendor_chat_service(@Part("vendor_id") RequestBody vendor_id,
                                                                @Part MultipartBody.Part user_msg,
                                                                  @Part("status") RequestBody status,
                                                                  @Part("unique_id") RequestBody unique_id,
                                                                 @Part("booking_status") RequestBody booking_status);

    @Multipart
    @POST("notification_vendor_chat_service")
    Call<ArrayList<Msg_Get_Set>>notification_vendor_chat_service1(@Part("vendor_id") RequestBody vendor_id,
                                                                 @Part("user_msg") RequestBody user_msg,
                                                                 @Part("status") RequestBody status,
                                                                   @Part("unique_id") RequestBody unique_id,
                                                                  @Part("booking_status") RequestBody booking_status);


    @Multipart
    @POST("notification_user_chat_service")
    Call<ArrayList<Msg_Get_Set>>notification_user_chat_service(@Part("user_id") RequestBody user_id,
                                                                @Part MultipartBody.Part vendor_msg,
                                                                @Part("status") RequestBody status,
                                                                @Part("unique_id") RequestBody unique_id,
                                                               @Part("booking_status") RequestBody booking_status);

    @Multipart
    @POST("notification_user_chat_service")
    Call<ArrayList<Msg_Get_Set>>notification_user_chat_service1(@Part("user_id") RequestBody user_id,
                                                                 @Part("vendor_msg") RequestBody vendor_msg,
                                                                 @Part("status") RequestBody status,
                                                                 @Part("unique_id") RequestBody unique_id,
                                                                @Part("booking_status") RequestBody booking_status);

  /*  @FormUrlEncoded
    @POST("complete_otp")
    Call<ArrayList<Msg_Get_Set>>complete_otp(@Field("unique_id") String unique_id);*/
}
