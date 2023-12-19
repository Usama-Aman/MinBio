package com.vic.vicwsp.Models.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileRequest {

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("company_name")
    @Expose
    private String companyName;
    @SerializedName("siret_no")
    @Expose
    private String siretNo;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("password_confirmation")
    @Expose
    private String passwordConfirmation;
    @SerializedName("country_code")
    @Expose
    private String countryCode;
    @SerializedName("iso2")
    @Expose
    private String iso2;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("nego_auto_accept")
    @Expose
    private int nego_auto_accept;

    public ProfileRequest(String email, String name, String lastName, String phone, String companyName, String siretNo, String password, String passwordConfirmation, String countryCode, String iso2, String lat, String lng, String address, int nego_auto_accept) {
        this.email = email;
        this.name = name;
        this.lastName = lastName;
        this.phone = phone;
        this.companyName = companyName;
        this.siretNo = siretNo;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        this.countryCode = countryCode;
        this.iso2 = iso2;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.nego_auto_accept = nego_auto_accept;
    }

    public int getNego_auto_accept() {
        return nego_auto_accept;
    }

    public void setNego_auto_accept(int nego_auto_accept) {
        this.nego_auto_accept = nego_auto_accept;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIso2() {
        return iso2;
    }

    public void setIso2(String iso2) {
        this.iso2 = iso2;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSiretNo() {
        return siretNo;
    }

    public void setSiretNo(String siretNo) {
        this.siretNo = siretNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }


}
