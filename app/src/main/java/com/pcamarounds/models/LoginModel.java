
package com.pcamarounds.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LoginModel extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("date_of_birth")
    @Expose
    private String dateOfBirth;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("mobile_no")
    @Expose
    private String mobileNo;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("ac_verify_status")
    @Expose
    private long acVerifyStatus;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("access")
    @Expose
    private String access;
    @SerializedName("nonverify_email")
    @Expose
    private String nonverifyEmail;
    @SerializedName("nonverify_mobile")
    @Expose
    private String nonverifyMobile;
    @SerializedName("nonverify_email_code")
    @Expose
    private String nonverifyEmailCode;
    @SerializedName("nonverify_mobile_code")
    @Expose
    private String nonverifyMobileCode;

    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("country_code")
    @Expose
    private String countryCode;

    @SerializedName("profile_image")
    @Expose
    private String profileImage;
    @SerializedName("aboutus")
    @Expose
    private String aboutus;

    @SerializedName("rating")
    @Expose
    private float rating;

    @SerializedName("total_reviews")
    @Expose
    private String totalReviews;

    @SerializedName("radious")
    @Expose
    private String radious;

    @SerializedName("latitude")
    @Expose
    private String latitude;

    @SerializedName("longitude")
    @Expose
    private String longitude;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("wallet_amount")
    @Expose
    private String walletAmount;

    @SerializedName("performance")
    @Expose
    private String performance;

    @SerializedName("email_on")
    @Expose
    private String emailOn;
    @SerializedName("notification_on")
    @Expose
    private String notificationOn;

    public String getEmailOn() {
        return emailOn;
    }

    public void setEmailOn(String emailOn) {
        this.emailOn = emailOn;
    }

    public String getNotificationOn() {
        return notificationOn;
    }

    public void setNotificationOn(String notificationOn) {
        this.notificationOn = notificationOn;
    }

    public String getPerformance() {
        return performance;
    }

    public void setPerformance(String performance) {
        this.performance = performance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(String walletAmount) {
        this.walletAmount = walletAmount;
    }

    public String getRadious() {
        return radious;
    }

    public void setRadious(String radious) {
        this.radious = radious;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(String totalReviews) {
        this.totalReviews = totalReviews;
    }

    public String getAboutus() {
        return aboutus;
    }

    public void setAboutus(String aboutus) {
        this.aboutus = aboutus;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getAcVerifyStatus() {
        return acVerifyStatus;
    }

    public void setAcVerifyStatus(long acVerifyStatus) {
        this.acVerifyStatus = acVerifyStatus;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getNonverifyEmail() {
        return nonverifyEmail;
    }

    public void setNonverifyEmail(String nonverifyEmail) {
        this.nonverifyEmail = nonverifyEmail;
    }

    public String getNonverifyMobile() {
        return nonverifyMobile;
    }

    public void setNonverifyMobile(String nonverifyMobile) {
        this.nonverifyMobile = nonverifyMobile;
    }

    public String getNonverifyEmailCode() {
        return nonverifyEmailCode;
    }

    public void setNonverifyEmailCode(String nonverifyEmailCode) {
        this.nonverifyEmailCode = nonverifyEmailCode;
    }

    public String getNonverifyMobileCode() {
        return nonverifyMobileCode;
    }

    public void setNonverifyMobileCode(String nonverifyMobileCode) {
        this.nonverifyMobileCode = nonverifyMobileCode;
    }

}
