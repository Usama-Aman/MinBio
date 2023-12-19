package com.vic.vicwsp.Models.Response.BankDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BankDetailModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("bank_name")
    @Expose
    private String bankName;
    @SerializedName("bank_address")
    @Expose
    private String bankAddress;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("zip_code")
    @Expose
    private String zipCode;
    @SerializedName("account_name")
    @Expose
    private String accountName;
    @SerializedName("account_number")
    @Expose
    private String accountNumber;
    @SerializedName("routing_code")
    @Expose
    private String routingCode;
    @SerializedName("swift_number")
    @Expose
    private String swiftNumber;
    @SerializedName("iban")
    @Expose
    private String iban;
    @SerializedName("bank_detail_photo")
    @Expose
    private String bankDetailPhoto;
    @SerializedName("driver_id")
    @Expose
    private Integer driverId;
    @SerializedName("country_id")
    @Expose
    private int countryId;
    @SerializedName("country_name")
    @Expose
    private String countryName;
    @SerializedName("image_path")
    @Expose
    private String imagePath;
    @SerializedName("bank_detail_photo_ext")
    @Expose
    private String bankDetailImageExtension;

    public String getBankDetailImageExtension() {
        return bankDetailImageExtension;
    }

    public void setBankDetailImageExtension(String bankDetailImageExtension) {
        this.bankDetailImageExtension = bankDetailImageExtension;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAddress() {
        return bankAddress;
    }

    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getRoutingCode() {
        return routingCode;
    }

    public void setRoutingCode(String routingCode) {
        this.routingCode = routingCode;
    }

    public String getSwiftNumber() {
        return swiftNumber;
    }

    public void setSwiftNumber(String swiftNumber) {
        this.swiftNumber = swiftNumber;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBankDetailPhoto() {
        return bankDetailPhoto;
    }

    public void setBankDetailPhoto(String bankDetailPhoto) {
        this.bankDetailPhoto = bankDetailPhoto;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
