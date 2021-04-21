
package com.pcamarounds.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppVersionModel {

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("camaroundsandroid")
    @Expose
    private double camaroundsandroid;
    @SerializedName("pcamaroundsandroid")
    @Expose
    private double pcamaroundsandroid;
    @SerializedName("camaroundios")
    @Expose
    private String camaroundios;
    @SerializedName("pcamaroundsios")
    @Expose
    private String pcamaroundsios;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getCamaroundsandroid() {
        return camaroundsandroid;
    }

    public void setCamaroundsandroid(double camaroundsandroid) {
        this.camaroundsandroid = camaroundsandroid;
    }

    public double getPcamaroundsandroid() {
        return pcamaroundsandroid;
    }

    public void setPcamaroundsandroid(double pcamaroundsandroid) {
        this.pcamaroundsandroid = pcamaroundsandroid;
    }

    public String getCamaroundios() {
        return camaroundios;
    }

    public void setCamaroundios(String camaroundios) {
        this.camaroundios = camaroundios;
    }

    public String getPcamaroundsios() {
        return pcamaroundsios;
    }

    public void setPcamaroundsios(String pcamaroundsios) {
        this.pcamaroundsios = pcamaroundsios;
    }

}
