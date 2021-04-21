
package com.pcamarounds.models.postdetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserDetailsModel {

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
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("date_of_birth")
    @Expose
    private String dateOfBirth;
    @SerializedName("personal_id")
    @Expose
    private String personalId;
    @SerializedName("mobile_no")
    @Expose
    private String mobileNo;
    @SerializedName("other_no")
    @Expose
    private String otherNo;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("profile_image")
    @Expose
    private String profileImage;
    @SerializedName("civil_status")
    @Expose
    private String civilStatus;
    @SerializedName("dependent")
    @Expose
    private String dependent;
    @SerializedName("access")
    @Expose
    private String access;
    @SerializedName("push_notification")
    @Expose
    private String pushNotification;
    @SerializedName("email_notification")
    @Expose
    private String emailNotification;
    @SerializedName("reg_step")
    @Expose
    private long regStep;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("admin_status")
    @Expose
    private String adminStatus;
    @SerializedName("created_by")
    @Expose
    private long createdBy;
    @SerializedName("created_on")
    @Expose
    private String createdOn;
    @SerializedName("updated_by")
    @Expose
    private long updatedBy;
    @SerializedName("updated_on")
    @Expose
    private String updatedOn;
    @SerializedName("rating")
    @Expose
    private float rating;
    @SerializedName("total_reviews")
    @Expose
    private String totalReviews;
    @SerializedName("ac_activation_code")
    @Expose
    private String acActivationCode;
    @SerializedName("forgotten_password_code")
    @Expose
    private String forgottenPasswordCode;
    @SerializedName("age")
    @Expose
    private String age;
    @SerializedName("ac_verify_status")
    @Expose
    private long acVerifyStatus;
    @SerializedName("class")
    @Expose
    private String _class;
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
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("country_code")
    @Expose
    private String countryCode;
    @SerializedName("wallet_amount")
    @Expose
    private String walletAmount;
    @SerializedName("aboutus")
    @Expose
    private String aboutus;
    @SerializedName("performance")
    @Expose
    private String performance;
    @SerializedName("radious")
    @Expose
    private String radious;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getOtherNo() {
        return otherNo;
    }

    public void setOtherNo(String otherNo) {
        this.otherNo = otherNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getCivilStatus() {
        return civilStatus;
    }

    public void setCivilStatus(String civilStatus) {
        this.civilStatus = civilStatus;
    }

    public String getDependent() {
        return dependent;
    }

    public void setDependent(String dependent) {
        this.dependent = dependent;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getPushNotification() {
        return pushNotification;
    }

    public void setPushNotification(String pushNotification) {
        this.pushNotification = pushNotification;
    }

    public String getEmailNotification() {
        return emailNotification;
    }

    public void setEmailNotification(String emailNotification) {
        this.emailNotification = emailNotification;
    }

    public long getRegStep() {
        return regStep;
    }

    public void setRegStep(long regStep) {
        this.regStep = regStep;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdminStatus() {
        return adminStatus;
    }

    public void setAdminStatus(String adminStatus) {
        this.adminStatus = adminStatus;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
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

    public String getAcActivationCode() {
        return acActivationCode;
    }

    public void setAcActivationCode(String acActivationCode) {
        this.acActivationCode = acActivationCode;
    }

    public String getForgottenPasswordCode() {
        return forgottenPasswordCode;
    }

    public void setForgottenPasswordCode(String forgottenPasswordCode) {
        this.forgottenPasswordCode = forgottenPasswordCode;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public long getAcVerifyStatus() {
        return acVerifyStatus;
    }

    public void setAcVerifyStatus(long acVerifyStatus) {
        this.acVerifyStatus = acVerifyStatus;
    }

    public String getClass_() {
        return _class;
    }

    public void setClass_(String _class) {
        this._class = _class;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(String walletAmount) {
        this.walletAmount = walletAmount;
    }

    public String getAboutus() {
        return aboutus;
    }

    public void setAboutus(String aboutus) {
        this.aboutus = aboutus;
    }

    public String getPerformance() {
        return performance;
    }

    public void setPerformance(String performance) {
        this.performance = performance;
    }

    public String getRadious() {
        return radious;
    }

    public void setRadious(String radious) {
        this.radious = radious;
    }

}
