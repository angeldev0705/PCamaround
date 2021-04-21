
package com.pcamarounds.models.dashboard;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BalanceData {

    @SerializedName("mybalance")
    @Expose
    private String mybalance;
    @SerializedName("counts_jobs")
    @Expose
    private long countsJobs;
    @SerializedName("pending_amt")
    @Expose
    private String pendingAmt;
    @SerializedName("pending_jobs")
    @Expose
    private long pendingJobs;

    public String getMybalance() {
        return mybalance;
    }

    public void setMybalance(String mybalance) {
        this.mybalance = mybalance;
    }

    public long getCountsJobs() {
        return countsJobs;
    }

    public void setCountsJobs(long countsJobs) {
        this.countsJobs = countsJobs;
    }

    public String getPendingAmt() {
        return pendingAmt;
    }

    public void setPendingAmt(String pendingAmt) {
        this.pendingAmt = pendingAmt;
    }

    public long getPendingJobs() {
        return pendingJobs;
    }

    public void setPendingJobs(long pendingJobs) {
        this.pendingJobs = pendingJobs;
    }

}
