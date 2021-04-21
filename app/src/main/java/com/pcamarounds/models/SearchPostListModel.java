package com.pcamarounds.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Developed by : Kamal Patidar
 * Author : Shanti Infotech Pvt. Ltd.
 * Email : kamal.shantiinfotech@gmail.com
 * Website : https://shantiinfotech.com/
 * Created on : 20,March,2020
 */
public class SearchPostListModel {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("post_number")
    @Expose
    private String postNumber;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("tech_id")
    @Expose
    private String techId;
    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @SerializedName("subcategory_id")
    @Expose
    private String subcategoryId;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("is_rated")
    @Expose
    private String isRated;
    @SerializedName("payment_type")
    @Expose
    private String paymentType;
    @SerializedName("booking_amount")
    @Expose
    private String bookingAmount;
    @SerializedName("total_amount")
    @Expose
    private String totalAmount;
    @SerializedName("booking_status")
    @Expose
    private String bookingStatus;
    @SerializedName("payment_status")
    @Expose
    private String paymentStatus;
    @SerializedName("created_on")
    @Expose
    private String createdOn;
    @SerializedName("updated_on")
    @Expose
    private String updatedOn;
    @SerializedName("created_by")
    @Expose
    private long createdBy;
    @SerializedName("updated_by")
    @Expose
    private long updatedBy;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("service_details")
    @Expose
    private String serviceDetails;
    @SerializedName("service_name")
    @Expose
    private String serviceName;
    @SerializedName("tech_name")
    @Expose
    private String techName;

    @SerializedName("distance")
    @Expose
    private double diatamnce;

    @SerializedName("bid_count")
    @Expose
    private long bidCount;

    @SerializedName("bid_data")
    @Expose
    private BidData bidData;

    @SerializedName("bid_avg_price")
    @Expose
    private String bid_avg_price;

    @SerializedName("tech_job_amounts")
    @Expose
    private String techJobAmounts;

    public String getTechJobAmounts() {
        return techJobAmounts;
    }

    public void setTechJobAmounts(String techJobAmounts) {
        this.techJobAmounts = techJobAmounts;
    }

    public String getBid_avg_price() {
        return bid_avg_price;
    }

    public void setBid_avg_price(String bid_avg_price) {
        this.bid_avg_price = bid_avg_price;
    }

    public long getBidCount() {
        return bidCount;
    }

    public void setBidCount(long bidCount) {
        this.bidCount = bidCount;
    }

    public BidData getBidData() {
        return bidData;
    }

    public void setBidData(BidData bidData) {
        this.bidData = bidData;
    }

    public double getDiatamnce() {
        return diatamnce;
    }

    public void setDiatamnce(double diatamnce) {
        this.diatamnce = diatamnce;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostNumber() {
        return postNumber;
    }

    public void setPostNumber(String postNumber) {
        this.postNumber = postNumber;
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(String subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIsRated() {
        return isRated;
    }

    public void setIsRated(String isRated) {
        this.isRated = isRated;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getBookingAmount() {
        return bookingAmount;
    }

    public void setBookingAmount(String bookingAmount) {
        this.bookingAmount = bookingAmount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
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

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getServiceDetails() {
        return serviceDetails;
    }

    public void setServiceDetails(String serviceDetails) {
        this.serviceDetails = serviceDetails;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getTechName() {
        return techName;
    }

    public void setTechName(String techName) {
        this.techName = techName;
    }

}
