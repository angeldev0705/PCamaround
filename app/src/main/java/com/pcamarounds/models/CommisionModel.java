
package com.pcamarounds.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommisionModel {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("commision")
    @Expose
    private long commision;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCommision() {
        return commision;
    }

    public void setCommision(long commision) {
        this.commision = commision;
    }

}
