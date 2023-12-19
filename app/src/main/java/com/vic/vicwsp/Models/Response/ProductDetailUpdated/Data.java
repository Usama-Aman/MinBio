
package com.vic.vicwsp.Models.Response.ProductDetailUpdated;

import java.util.ArrayList;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("image_path")
    @Expose
    private String imagePath;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("product_description")
    @Expose
    private String productDescription;
    @SerializedName("category_id")
    @Expose
    private Integer categoryId;
    @SerializedName("is_warranted")
    @Expose
    private Integer isWarranted;
    @SerializedName("details")
    @Expose
    private Details details;
    @SerializedName("sellers")
    @Expose
    private ArrayList<Seller> sellers = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Data() {
    }

    /**
     * 
     * @param unit
     * @param imagePath
     * @param name
     * @param isWarranted
     * @param details
     * @param id
     * @param productDescription
     * @param categoryId
     * @param sellers
     */
    public Data(Integer id, String name, String imagePath, String unit, String productDescription, Integer categoryId, Integer isWarranted, Details details, ArrayList<Seller> sellers) {
        super();
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
        this.unit = unit;
        this.productDescription = productDescription;
        this.categoryId = categoryId;
        this.isWarranted = isWarranted;
        this.details = details;
        this.sellers = sellers;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getIsWarranted() {
        return isWarranted;
    }

    public void setIsWarranted(Integer isWarranted) {
        this.isWarranted = isWarranted;
    }

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    public ArrayList<Seller> getSellers() {
        return sellers;
    }

    public void setSellers(ArrayList<Seller> sellers) {
        this.sellers = sellers;
    }

}
