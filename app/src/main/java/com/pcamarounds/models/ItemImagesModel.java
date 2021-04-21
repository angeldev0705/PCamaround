
package com.pcamarounds.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;

public class ItemImagesModel extends RealmObject implements Serializable {
    @SerializedName("fileName")
    private String mFileName;
    @SerializedName("type")
    private String mType;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("job_id")
    @Expose
    private String jobId;
    @SerializedName("tech_id")
    @Expose
    private String techId;
    @SerializedName("image")
    @Expose
    private String image;

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getTechId() {
        return techId;
    }

    public void setTechId(String techId) {
        this.techId = techId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getmFileName() {
        return mFileName;
    }

    public void setmFileName(String mFileName) {
        this.mFileName = mFileName;
    }
}
