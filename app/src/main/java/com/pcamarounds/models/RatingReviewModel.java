
package com.pcamarounds.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RatingReviewModel {

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("booking_amount")
    @Expose
    private String bookingAmount;
    @SerializedName("created_on")
    @Expose
    private String createdOn;
    @SerializedName("order_id")
    @Expose
    private long orderId;
    @SerializedName("wrkid")
    @Expose
    private long wrkid;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("work_job_id")
    @Expose
    private long workJobId;
    @SerializedName("common_id")
    @Expose
    private long commonId;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("created_by")
    @Expose
    private long createdBy;
    @SerializedName("average")
    @Expose
    private float average;
    @SerializedName("post_number")
    @Expose
    private String postNumber;
    @SerializedName("user_id")
    @Expose
    private long userId;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("profile_image")
    @Expose
    private String profileImage;

    @SerializedName("last_name")
    @Expose
    private String lastName;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBookingAmount() {
        return bookingAmount;
    }

    public void setBookingAmount(String bookingAmount) {
        this.bookingAmount = bookingAmount;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getWrkid() {
        return wrkid;
    }

    public void setWrkid(long wrkid) {
        this.wrkid = wrkid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getWorkJobId() {
        return workJobId;
    }

    public void setWorkJobId(long workJobId) {
        this.workJobId = workJobId;
    }

    public long getCommonId() {
        return commonId;
    }

    public void setCommonId(long commonId) {
        this.commonId = commonId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public float getAverage() {
        return average;
    }

    public void setAverage(float average) {
        this.average = average;
    }

    public String getPostNumber() {
        return postNumber;
    }

    public void setPostNumber(String postNumber) {
        this.postNumber = postNumber;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

}
