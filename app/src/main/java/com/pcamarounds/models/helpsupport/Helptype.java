
package com.pcamarounds.models.helpsupport;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Helptype {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("category_id")
    @Expose
    private long categoryId;
    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("question")
    @Expose
    private String question;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

}
