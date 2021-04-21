
package com.pcamarounds.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IFSCModel {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("bank_id")
    @Expose
    private String bankId;
    @SerializedName("routes")
    @Expose
    private String routes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getRoutes() {
        return routes;
    }

    public void setRoutes(String routes) {
        this.routes = routes;
    }


}
