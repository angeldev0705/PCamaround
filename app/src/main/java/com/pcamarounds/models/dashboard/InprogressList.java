
package com.pcamarounds.models.dashboard;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InprogressList {

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
    @SerializedName("admin_percent")
    @Expose
    private String adminPercent;
    @SerializedName("admin_commision")
    @Expose
    private String adminCommision;
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
    @SerializedName("total_reviews")
    @Expose
    private long totalReviews;
    @SerializedName("performance")
    @Expose
    private String performance;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("profile_image")
    @Expose
    private String profileImage;
    @SerializedName("bid_data")
    @Expose
    private String bidData;


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

    public String getAdminPercent() {
        return adminPercent;
    }

    public void setAdminPercent(String adminPercent) {
        this.adminPercent = adminPercent;
    }

    public String getAdminCommision() {
        return adminCommision;
    }

    public void setAdminCommision(String adminCommision) {
        this.adminCommision = adminCommision;
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

    public long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(long totalReviews) {
        this.totalReviews = totalReviews;
    }

    public String getPerformance() {
        return performance;
    }

    public void setPerformance(String performance) {
        this.performance = performance;
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

    public String getBidData() {
        return bidData;
    }

    public void setBidData(String bidData) {
        this.bidData = bidData;
    }

}
