package com.pcamarounds.retrofit;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UrlServices {

    @FormUrlEncoded
    @POST("api/generate_access_token")
    Call<ResponseBody> generateAccessToken(@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("api/get_country")
    Call<ResponseBody> get_country(@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("api/reset_password")
    Call<ResponseBody> reset_password(@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("api/verify_account")
    Call<ResponseBody> verify_account(@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("api/get_version")
    Call<ResponseBody> get_version(@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("api/resend_otp")
    Call<ResponseBody> resend_otp(@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("api/login")
    Call<ResponseBody> login(@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("api/forgot_password")
    Call<ResponseBody> forgot_password(@FieldMap HashMap<String, String> hashMap);

    @POST("api/user_update_profile")
    Call<ResponseBody> user_update_profile(@HeaderMap Map<String, String> headers,@Body RequestBody files);


    @POST("api/user_profilepic")
    Call<ResponseBody> user_profilepic(@HeaderMap Map<String, String> headers,@Body RequestBody files);

    @FormUrlEncoded
    @POST("booking/post_list")
    Call<ResponseBody> post_list(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/post_list_pro")
    Call<ResponseBody> post_list_pro(@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/my_job_list")
    Call<ResponseBody> my_job_list(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/delete_card")
    Call<ResponseBody> delete_card(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/post_details")
    Call<ResponseBody> post_details(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/post_bid")
    Call<ResponseBody> post_bid(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("api/delete_images")
    Call<ResponseBody> delete_images(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("api/get_my_profile")
    Call<ResponseBody> get_my_profile(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("api/change_password")
    Call<ResponseBody> change_password(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/accept_declined_request")
    Call<ResponseBody> accept_declined_request(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/add_group_id")
    Call<ResponseBody> add_group_id(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/user_reviews")
    Call<ResponseBody> user_reviews(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/admin_charges")
    Call<ResponseBody> admin_charges(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("api/verify_mobile")
    Call<ResponseBody> verify_mobile(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("api/resend_mobile_otp")
    Call<ResponseBody> resend_mobile_otp(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/my_reviews")
    Call<ResponseBody> my_reviews(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/request_complete")
    Call<ResponseBody> request_complete(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/tech_dashboard")
    Call<ResponseBody> tech_dashboard(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/tech_reviews_pending")
    Call<ResponseBody> tech_reviews_pending(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/job_card_add")
    Call<ResponseBody> job_card_add(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("payment/card_list")
    Call<ResponseBody> card_list(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/tech_give_review")
    Call<ResponseBody> tech_give_review(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/tech_address")
    Call<ResponseBody> tech_address(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/help_booking_list")
    Call<ResponseBody> help_booking_list(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/help_support_category")
    Call<ResponseBody> help_support_category(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/help_support_question")
    Call<ResponseBody> help_support_question(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);


    @POST("booking/help_support_input")
    Call<ResponseBody> help_support_input(@HeaderMap Map<String, String> headers,@Body RequestBody files);

    @FormUrlEncoded
    @POST("api/email_notification")
    Call<ResponseBody> email_notification(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/my_transaction")
    Call<ResponseBody> my_transaction(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/add_wallet_amount")
    Call<ResponseBody> add_wallet_amount(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/tech_bank_list")
    Call<ResponseBody> tech_bank_list(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/tech_bank_route")
    Call<ResponseBody> tech_bank_route(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/get_admin_bankinfo")
    Call<ResponseBody> get_admin_bankinfo(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/add_bank_infotech")
    Call<ResponseBody> add_bank_infotech(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/view_edit_infotech")
    Call<ResponseBody> view_edit_infotech(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @POST("booking/add_bank_transfer")
    Call<ResponseBody> add_bank_transfer(@HeaderMap Map<String, String> headers,@Body RequestBody files);

    @FormUrlEncoded
    @POST("api/logout")
    Call<ResponseBody> logout(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("booking/category_assign")
    Call<ResponseBody> category_assign(@HeaderMap Map<String, String> headers,@FieldMap HashMap<String, String> hashMap);


  /*@GET
    Call<ResponseBody> helpInfo(@Url String catId);*/
    /*******comet chat pro*/

    @POST("https://api-us.cometchat.io/v2.0/users")
    Call<ResponseBody> createUser(@HeaderMap Map<String, String> headers, @Body RequestBody files);

    @GET("https://api.cometchat.com/v1.8/users")
    Call<ResponseBody> getUserList(@HeaderMap Map<String, String> headers);

    @GET("https://api.cometchat.com/v1.8/users/{uid}/friends")
    Call<ResponseBody> getFriendList(@Path("uid") String uid, @HeaderMap Map<String, String> headers);

    @POST("https://api.cometchat.com/v1.8/users/{uid}/friends")
    Call<ResponseBody> addFriends(@Path("uid") String uid, @HeaderMap Map<String, String> headers, @Body RequestBody files);


    @HTTP(method = "DELETE", path = "https://api.cometchat.com/v1.8/users/{uid}/friends", hasBody = true)
    Call<ResponseBody> removeFriends(@Path("uid") String uid, @HeaderMap Map<String, String> headers, @Body RequestBody files);


    @PUT("updateUserProfile")
    Call<ResponseBody> updateUserProfile(@HeaderMap Map<String, String> headers, @Body RequestBody files);

    @PUT("https://api.cometchat.com/v1.8/users/{uid}")
    Call<ResponseBody> updateUser(@Path("uid") String uid, @HeaderMap Map<String, String> headers, @Body HashMap<String, String> hashMap);


    @POST("https://api.cometchat.com/v1.8/users/{uid}/blockedusers")
    Call<ResponseBody> blockedusers(@Path("uid") String uid, @HeaderMap Map<String, String> headers, @Body RequestBody files);

    @HTTP(method = "DELETE", path = "https://api.cometchat.com/v1.8/users/{uid}/blockedusers", hasBody = true)
    Call<ResponseBody> unblockedusers(@Path("uid") String uid, @HeaderMap Map<String, String> headers, @Body RequestBody files);
}
