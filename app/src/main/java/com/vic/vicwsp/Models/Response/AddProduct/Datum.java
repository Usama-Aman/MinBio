
package com.vic.vicwsp.Models.Response.AddProduct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("product_variety")
    @Expose
    private String productVariety;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("image_path")
    @Expose
    private String file;

    /**
     * No args constructor for use in serialization
     */
    public Datum() {
    }

    /**
     * @param unit
     * @param file
     * @param price
     * @param productVariety
     * @param id
     */
    public Datum(Integer id, String price, String productVariety, String unit, String file) {
        super();
        this.id = id;
        this.price = price;
        this.productVariety = productVariety;
        this.unit = unit;
        this.file = file;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductVariety() {
        return productVariety;
    }

    public void setProductVariety(String productVariety) {
        this.productVariety = productVariety;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

}
