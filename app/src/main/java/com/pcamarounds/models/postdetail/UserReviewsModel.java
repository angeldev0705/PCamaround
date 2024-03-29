
package com.pcamarounds.models.postdetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserReviewsModel implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("order_id")
    @Expose
    private long orderId;
    @SerializedName("common_id")
    @Expose
    private long commonId;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("created_by")
    @Expose
    private long createdBy;
    @SerializedName("average")
    @Expose
    private float average;
    @SerializedName("user_id")
    @Expose
    private long userId;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("profile_image")
    @Expose
    private String profileImage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
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
