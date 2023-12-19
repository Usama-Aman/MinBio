
package com.vic.vicwsp.Models.Response.SearchData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Country {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("name_fr")
    @Expose
    private String nameFR;
    @SerializedName("image_path")
    @Expose
    private String imagePath;

    public Country(Integer id, String name, String nameFR, String imagePath) {
        this.id = id;
        this.name = name;
        this.nameFR = nameFR;
        this.imagePath = imagePath;
    }

    public String getNameFR() {
        return nameFR;
    }

    public void setNameFR(String nameFR) {
        this.nameFR = nameFR;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
