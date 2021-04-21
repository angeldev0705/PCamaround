
package com.pcamarounds.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RadioBoxModel {

    @SerializedName("option")
    @Expose
    private String option;

    @SerializedName("isselected")
    @Expose
    private String isselected;

    private boolean ischeked;

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public boolean isIscheked() {
        return ischeked;
    }

    public void setIscheked(boolean ischeked) {
        this.ischeked = ischeked;
    }

    public String getIsselected() {
        return isselected;
    }

    public void setIsselected(String isselected) {
        this.isselected = isselected;
    }

    @Override
    public String toString() {
        return "{" + "\"option\""+":" +'\"'+ option + '\"' + ","+"\"isselected\""+":" + '\"'+ischeked +"\"}";
    }
}
