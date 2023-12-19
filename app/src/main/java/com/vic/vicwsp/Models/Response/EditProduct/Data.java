
package com.vic.vicwsp.Models.Response.EditProduct;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("product_variety")
    @Expose
    private String productVariety;
    @SerializedName("pro_description")
    @Expose
    private String proDescription;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("minprice")
    @Expose
    private String minprice;
    @SerializedName("maxprice")
    @Expose
    private String maxprice;
    @SerializedName("stock")
    @Expose
    private Float stock;
    @SerializedName("sale_case")
    @Expose
    private Float saleCase;
    @SerializedName("is_nego")
    @Expose
    private Integer isNego;
    @SerializedName("is_bio")
    @Expose
    private Integer isBio;
    @SerializedName("vat")
    @Expose
    private String vat;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("country_id")
    @Expose
    private Integer countryId;
    @SerializedName("size_id")
    @Expose
    private Integer sizeId;
    @SerializedName("class_id")
    @Expose
    private Integer classId;
    @SerializedName("unit_id")
    @Expose
    private Integer unitId;
    @SerializedName("subunit_id")
    @Expose
    private Integer subunit_id;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("vat_id")
    @Expose
    private int vatId;
    @SerializedName("files")
    @Expose
    private List<Files> files = null;

    public Data(Integer id, String productVariety, String proDescription, String price, String minprice, String maxprice, Float stock, Float saleCase, Integer isNego, Integer isBio, String vat, String discount, Integer countryId, Integer sizeId, Integer classId, Integer unitId, Integer subunit_id, String unit, int vatId, List<Files> files) {
        this.id = id;
        this.productVariety = productVariety;
        this.proDescription = proDescription;
        this.price = price;
        this.minprice = minprice;
        this.maxprice = maxprice;
        this.stock = stock;
        this.saleCase = saleCase;
        this.isNego = isNego;
        this.isBio = isBio;
        this.vat = vat;
        this.discount = discount;
        this.countryId = countryId;
        this.sizeId = sizeId;
        this.classId = classId;
        this.unitId = unitId;
        this.subunit_id = subunit_id;
        this.unit = unit;
        this.vatId = vatId;
        this.files = files;
    }

    public int getVatId() {
        return vatId;
    }

    public void setVatId(int vatId) {
        this.vatId = vatId;
    }

    public Integer getSubunit_id() {
        return subunit_id;
    }

    public void setSubunit_id(Integer subunit_id) {
        this.subunit_id = subunit_id;
    }

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductVariety() {
        return productVariety;
    }

    public void setProductVariety(String productVariety) {
        this.productVariety = productVariety;
    }

    public String getProDescription() {
        return proDescription;
    }

    public void setProDescription(String proDescription) {
        this.proDescription = proDescription;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMinprice() {
        return minprice;
    }

    public void setMinprice(String minprice) {
        this.minprice = minprice;
    }

    public String getMaxprice() {
        return maxprice;
    }

    public void setMaxprice(String maxprice) {
        this.maxprice = maxprice;
    }

    public Float getStock() {
        return stock;
    }

    public void setStock(Float stock) {
        this.stock = stock;
    }

    public Float getSaleCase() {
        return saleCase;
    }

    public void setSaleCase(Float saleCase) {
        this.saleCase = saleCase;
    }

    public Integer getIsNego() {
        return isNego;
    }

    public void setIsNego(Integer isNego) {
        this.isNego = isNego;
    }

    public Integer getIsBio() {
        return isBio;
    }

    public void setIsBio(Integer isBio) {
        this.isBio = isBio;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public Integer getSizeId() {
        return sizeId;
    }

    public void setSizeId(Integer sizeId) {
        this.sizeId = sizeId;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<Files> getFiles() {
        return files;
    }

    public void setFiles(List<Files> files) {
        this.files = files;
    }

}
