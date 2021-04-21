
package com.pcamarounds.models.postdetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostBidData {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("tech_id")
    @Expose
    private String techId;
    @SerializedName("job_id")
    @Expose
    private String jobId;
    @SerializedName("job_amount")
    @Expose
    private String jobAmount;
    @SerializedName("working_days")
    @Expose
    private long workingDays;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("material_include")
    @Expose
    private String materialInclude;
    @SerializedName("bid_status")
    @Expose
    private String bidStatus;
    @SerializedName("payment_type")
    @Expose
    private String paymentType;
    @SerializedName("created_on")
    @Expose
    private String createdOn;
    @SerializedName("updated_on")
    @Expose
    private String updatedOn;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("rating")
    @Expose
    private float rating;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("profile_image")
    @Expose
    private String profileImage;

    @SerializedName("admin_percent")
    @Expose
    private String admin_percent;

    @SerializedName("admin_commision")
    @Expose
    private String admin_commision;

    @SerializedName("calculate_amount")
    @Expose
    private String calculateAmount;

    @SerializedName("paidout")
    @Expose
    private String paidout;

    @SerializedName("user_discount")
    @Expose
    private String userDiscount;

    public String getUserDiscount() {
        return userDiscount;
    }

    public void setUserDiscount(String userDiscount) {
        this.userDiscount = userDiscount;
    }

    public String getPaidout() {
        return paidout;
    }

    public void setPaidout(String paidout) {
        this.paidout = paidout;
    }

    public String getCalculateAmount() {
        return calculateAmount;
    }

    public void setCalculateAmount(String calculateAmount) {
        this.calculateAmount = calculateAmount;
    }

    public String getAdmin_percent() {
        return admin_percent;
    }

    public void setAdmin_percent(String admin_percent) {
        this.admin_percent = admin_percent;
    }

    public String getAdmin_commision() {
        return admin_commision;
    }

    public void setAdmin_commision(String admin_commision) {
        this.admin_commision = admin_commision;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTechId() {
        return techId;
    }

    public void setTechId(String techId) {
        this.techId = techId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobAmount() {
        return jobAmount;
    }

    public void setJobAmount(String jobAmount) {
        this.jobAmount = jobAmount;
    }

    public long getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(long workingDays) {
        this.workingDays = workingDays;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getMaterialInclude() {
        return materialInclude;
    }

    public void setMaterialInclude(String materialInclude) {
        this.materialInclude = materialInclude;
    }

    public String getBidStatus() {
        return bidStatus;
    }

    public void setBidStatus(String bidStatus) {
        this.bidStatus = bidStatus;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

}
