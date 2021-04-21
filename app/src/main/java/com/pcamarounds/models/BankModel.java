
package com.pcamarounds.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class BankModel {

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("tech_id")
    @Expose
    private long techId;
    @SerializedName("acc_holder")
    @Expose
    private String accHolder;
    @SerializedName("acc_number")
    @Expose
    private String accNumber;
    @SerializedName("acc_bank")
    @Expose
    private String accBank;
    @SerializedName("acc_type")
    @Expose
    private String accType;
    @SerializedName("created_on")
    @Expose
    private String createdOn;
    @SerializedName("created_by")
    @Expose
    private long createdBy;
    @SerializedName("banks_id")
    @Expose
    private String banksId;
    @SerializedName("route_no")
    @Expose
    private String routeNo;

    public String getRouteNo() {
        return routeNo;
    }

    public void setRouteNo(String routeNo) {
        this.routeNo = routeNo;
    }

    public String getBanksId() {
        return banksId;
    }

    public void setBanksId(String banksId) {
        this.banksId = banksId;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTechId() {
        return techId;
    }

    public void setTechId(long techId) {
        this.techId = techId;
    }

    public String getAccHolder() {
        return accHolder;
    }

    public void setAccHolder(String accHolder) {
        this.accHolder = accHolder;
    }

    public String getAccNumber() {
        return accNumber;
    }

    public void setAccNumber(String accNumber) {
        this.accNumber = accNumber;
    }

    public String getAccBank() {
        return accBank;
    }

    public void setAccBank(String accBank) {
        this.accBank = accBank;
    }

    public String getAccType() {
        return accType;
    }

    public void setAccType(String accType) {
        this.accType = accType;
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
