
package com.pcamarounds.models.postdetail;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostDetailModel {

    @SerializedName("status")
    @Expose
    private long status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private PostData data;
    @SerializedName("post_bid_data")
    @Expose
    private PostBidData postBidData;
   @SerializedName("post_bid_logs")
    @Expose
    private List<PostBidLogs> postBidLogs = null;
    @SerializedName("addons")
    @Expose
    private List<Addon> addons = null;
    @SerializedName("images")
    @Expose
    private List<ImagesModel> images = null;

    @SerializedName("user_details")
    @Expose
    private UserDetailsModel userDetailsModel = null;

    @SerializedName("user_reviews")
    @Expose
    private UserReviewsModel userReviewsModel = null;

    @SerializedName("pro_job_reviews")
    @Expose
    private ProJobReviews proJobReviews = null;

    public ProJobReviews getProJobReviews() {
        return proJobReviews;
    }

    public void setProJobReviews(ProJobReviews proJobReviews) {
        this.proJobReviews = proJobReviews;
    }

    public UserReviewsModel getUserReviewsModel() {
        return userReviewsModel;
    }

    public void setUserReviewsModel(UserReviewsModel userReviewsModel) {
        this.userReviewsModel = userReviewsModel;
    }

    public UserDetailsModel getUserDetailsModel() {
        return userDetailsModel;
    }

    public void setUserDetailsModel(UserDetailsModel userDetailsModel) {
        this.userDetailsModel = userDetailsModel;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PostData getData() {
        return data;
    }

    public void setData(PostData data) {
        this.data = data;
    }

    public PostBidData getPostBidData() {
        return postBidData;
    }

    public void setPostBidData(PostBidData postBidData) {
        this.postBidData = postBidData;
    }

    public List<PostBidLogs> getPostBidLogs() {
        return postBidLogs;
    }

    public void setPostBidLogs(List<PostBidLogs> postBidLogs) {
        this.postBidLogs = postBidLogs;
    }

    public List<Addon> getAddons() {
        return addons;
    }

    public void setAddons(List<Addon> addons) {
        this.addons = addons;
    }

    public List<ImagesModel> getImages() {
        return images;
    }

    public void setImages(List<ImagesModel> images) {
        this.images = images;
    }

}
