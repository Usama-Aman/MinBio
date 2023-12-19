
package com.vic.vicwsp.Models.Response.EditProduct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Files {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("image_path")
    @Expose
    private String imagePath;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Files() {
    }

    /**
     * 
     * @param imagePath
     * @param id
     */
    public Files(Integer id, String imagePath) {
        super();
        this.id = id;
        this.imagePath = imagePath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
