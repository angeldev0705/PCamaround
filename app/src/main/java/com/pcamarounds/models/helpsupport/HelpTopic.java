
package com.pcamarounds.models.helpsupport;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HelpTopic {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("heading_one")
    @Expose
    private String headingOne;
    @SerializedName("decp_one")
    @Expose
    private String decpOne;
    @SerializedName("heading_two")
    @Expose
    private String headingTwo;
    @SerializedName("descp_two")
    @Expose
    private String descpTwo;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_on")
    @Expose
    private String createdOn;
    @SerializedName("created_by")
    @Expose
    private long createdBy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHeadingOne() {
        return headingOne;
    }

    public void setHeadingOne(String headingOne) {
        this.headingOne = headingOne;
    }

    public String getDecpOne() {
        return decpOne;
    }

    public void setDecpOne(String decpOne) {
        this.decpOne = decpOne;
    }

    public String getHeadingTwo() {
        return headingTwo;
    }

    public void setHeadingTwo(String headingTwo) {
        this.headingTwo = headingTwo;
    }

    public String getDescpTwo() {
        return descpTwo;
    }

    public void setDescpTwo(String descpTwo) {
        this.descpTwo = descpTwo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

}
