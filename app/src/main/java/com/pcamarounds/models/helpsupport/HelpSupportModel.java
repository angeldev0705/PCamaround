
package com.pcamarounds.models.helpsupport;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pcamarounds.models.HelpCategoryModel;

public class HelpSupportModel {

    @SerializedName("status")
    @Expose
    private long status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<HelpCategoryModel> helpTopics = null;


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

    public List<HelpCategoryModel> getHelpTopics() {
        return helpTopics;
    }

    public void setHelpTopics(List<HelpCategoryModel> helpTopics) {
        this.helpTopics = helpTopics;
    }

}
