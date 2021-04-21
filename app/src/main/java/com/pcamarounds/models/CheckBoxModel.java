
package com.pcamarounds.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckBoxModel {

    @SerializedName("option")
    @Expose
    private String option;

    @SerializedName("isselected")
    @Expose
    private String isselected;

    private boolean ischecked;

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public boolean isChecked() {
        return ischecked;
    }

    public void setChecked(boolean checked) {
        ischecked = checked;
    }

    public String getIsselected() {
        return isselected;
    }

    public void setIsselected(String isselected) {
        this.isselected = isselected;
    }

    @Override
    public String toString() {
        return "{" + "\"option\""+":" +'\"'+ option + '\"' + ","+"\"isselected\""+":" + '\"'+ischecked +"\"}";
    }
}
