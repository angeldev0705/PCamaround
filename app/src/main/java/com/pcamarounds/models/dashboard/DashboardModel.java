
package com.pcamarounds.models.dashboard;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DashboardModel {

    @SerializedName("status")
    @Expose
    private long status;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("inprogress_list")
    @Expose
    private List<InprogressList> inprogressList = null;
    @SerializedName("awarded_list")
    @Expose
    private List<AwardedList> awardedList = null;

    @SerializedName("pending_jobs")
    @Expose
    private String pendingjobs;

    @SerializedName("my_balance")
    @Expose
    private String mybalance;

    @SerializedName("pending_amt")
    @Expose
    private String pendingamt;

    @SerializedName("myinprogress")
    @Expose
    private String myinprogress;

    public String getPendingjobs() {
        return pendingjobs;
    }

    public void setPendingjobs(String pendingjobs) {
        this.pendingjobs = pendingjobs;
    }

    public String getMybalance() {
        return mybalance;
    }

    public void setMybalance(String mybalance) {
        this.mybalance = mybalance;
    }

    public String getPendingamt() {
        return pendingamt;
    }

    public void setPendingamt(String pendingamt) {
        this.pendingamt = pendingamt;
    }

    public String getMyinprogress() {
        return myinprogress;
    }

    public void setMyinprogress(String myinprogress) {
        this.myinprogress = myinprogress;
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

    public List<InprogressList> getInprogressList() {
        return inprogressList;
    }

    public void setInprogressList(List<InprogressList> inprogressList) {
        this.inprogressList = inprogressList;
    }

    public List<AwardedList> getAwardedList() {
        return awardedList;
    }

    public void setAwardedList(List<AwardedList> awardedList) {
        this.awardedList = awardedList;
    }

}
