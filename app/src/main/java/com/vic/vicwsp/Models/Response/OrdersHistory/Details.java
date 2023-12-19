
package com.vic.vicwsp.Models.Response.OrdersHistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Details {

    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("vat")
    @Expose
    private String vat;
    @SerializedName("delivery_charges")
    @Expose
    private String delivery_charges;
    @SerializedName("is_reviewed")
    @Expose
    private int is_reviewed;
    @SerializedName("is_driver_reviewed")
    @Expose
    private int is_driver_reviewed;
    @SerializedName("is_delivered")
    @Expose
    private int is_delivered;
    @SerializedName("products")
    @Expose
    private List<Product> products = null;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("coverage_status")
    @Expose
    private String coverage_status;
    @SerializedName("coverage_amount")
    @Expose
    private String coverage_amount;
    @SerializedName("coverage_due_date")
    @Expose
    private String coverage_due_date;
    @SerializedName("is_pickup")
    @Expose
    private int is_pickup;
    @SerializedName("app_charges")
    @Expose
    private String app_charges;
    @SerializedName("merchant_amount")
    @Expose
    private String merchant_amount;
    @SerializedName("company_name")
    @Expose
    private String company_name;
    @SerializedName("driver_name")
    @Expose
    private String driverName;

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getApp_charges() {
        return app_charges;
    }

    public void setApp_charges(String app_charges) {
        this.app_charges = app_charges;
    }

    public String getMerchant_amount() {
        return merchant_amount;
    }

    public void setMerchant_amount(String merchant_amount) {
        this.merchant_amount = merchant_amount;
    }

    public int getIs_pickup() {
        return is_pickup;
    }

    public void setIs_pickup(int is_pickup) {
        this.is_pickup = is_pickup;
    }

    public String getCoverage_status() {
        return coverage_status;
    }

    public void setCoverage_status(String coverage_status) {
        this.coverage_status = coverage_status;
    }

    public String getCoverage_amount() {
        return coverage_amount;
    }

    public void setCoverage_amount(String coverage_amount) {
        this.coverage_amount = coverage_amount;
    }

    public String getCoverage_due_date() {
        return coverage_due_date;
    }

    public void setCoverage_due_date(String coverage_due_date) {
        this.coverage_due_date = coverage_due_date;
    }

    public int getIs_driver_reviewed() {
        return is_driver_reviewed;
    }

    public void setIs_driver_reviewed(int is_driver_reviewed) {
        this.is_driver_reviewed = is_driver_reviewed;
    }

    public int getIs_delivered() {
        return is_delivered;
    }

    public void setIs_delivered(int is_delivered) {
        this.is_delivered = is_delivered;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public int getIs_reviewed() {
        return is_reviewed;
    }

    public void setIs_reviewed(int is_reviewed) {
        this.is_reviewed = is_reviewed;
    }

    public String getDelivery_charges() {
        return delivery_charges;
    }

    public void setDelivery_charges(String delivery_charges) {
        this.delivery_charges = delivery_charges;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

}
