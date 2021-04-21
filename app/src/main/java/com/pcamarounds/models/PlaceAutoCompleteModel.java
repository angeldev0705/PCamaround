package com.pcamarounds.models;

public class PlaceAutoCompleteModel {
    private String placeId;
    private String placeText;

    public PlaceAutoCompleteModel(String placeId, String toString) {
        this.placeId = placeId;
        this.placeText = toString;
    }
    public PlaceAutoCompleteModel() {
    }


    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceText() {
        return placeText;
    }

    public void setPlaceText(String placeText) {
        this.placeText = placeText;
    }
    public String toString(){
        return placeText;
    }

}
