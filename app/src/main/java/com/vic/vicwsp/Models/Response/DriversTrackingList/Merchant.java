
package com.vic.vicwsp.Models.Response.DriversTrackingList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Merchant {

    @SerializedName("company_name")
    @Expose
    private String companyName;
    @SerializedName("seller_name")
    @Expose
    private String sellerName;
    @SerializedName("siret_no")
    @Expose
    private String siretNo;
    @SerializedName("items")
    @Expose
    private Integer items;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Merchant() {
    }

    /**
     * 
     * @param siretNo
     * @param companyName
     * @param sellerName
     * @param items
     */
    public Merchant(String companyName, String sellerName, String siretNo, Integer items) {
        super();
        this.companyName = companyName;
        this.sellerName = sellerName;
        this.siretNo = siretNo;
        this.items = items;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSiretNo() {
        return siretNo;
    }

    public void setSiretNo(String siretNo) {
        this.siretNo = siretNo;
    }

    public Integer getItems() {
        return items;
    }

    public void setItems(Integer items) {
        this.items = items;
    }

}
