
package com.pcamarounds.models.helpsupport;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HelpData {

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("employee")
    @Expose
    private long employee;
    @SerializedName("customer")
    @Expose
    private long customer;
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
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("counts")
    @Expose
    private long counts;
    @SerializedName("helptypes")
    @Expose
    private List<Helptype> helptypes = null;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public long getEmployee() {
        return employee;
    }

    public void setEmployee(long employee) {
        this.employee = employee;
    }

    public long getCustomer() {
        return customer;
    }

    public void setCustomer(long customer) {
        this.customer = customer;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCounts() {
        return counts;
    }

    public void setCounts(long counts) {
        this.counts = counts;
    }

    public List<Helptype> getHelptypes() {
        return helptypes;
    }

    public void setHelptypes(List<Helptype> helptypes) {
        this.helptypes = helptypes;
    }

}
