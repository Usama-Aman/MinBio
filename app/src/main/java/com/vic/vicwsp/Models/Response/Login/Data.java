
package com.vic.vicwsp.Models.Response.Login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("company_name")
    @Expose
    private String companyName;
    @SerializedName("siret_no")
    @Expose
    private String siretNo;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("country_code")
    @Expose
    private String countryCode;
    @SerializedName("lang")
    @Expose
    private String lang;
    @SerializedName("phone_verified")
    @Expose
    private int phone_verified;
    @SerializedName("email_verified")
    @Expose
    private int email_verified;
    @SerializedName("is_active")
    @Expose
    private int is_active;
    @SerializedName("is_merchant")
    @Expose
    private int is_merchant;
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
    @SerializedName("kbis_file_path")
    @Expose
    private String kbis_file_path;
    @SerializedName("id_file_path")
    @Expose
    private String id_file_path;
    @SerializedName("bank_detail_id")
    @Expose
    private int bank_detail_id;

    public Data(Integer id, String name, String lastName, String email, String companyName, String siretNo, String phone, String countryCode, String lang, int phone_verified, int email_verified, int is_active, int is_merchant, String lat, String lng, String address, int nego_auto_accept, String kbis_file_path, String id_file_path, int bank_detail_id) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.companyName = companyName;
        this.siretNo = siretNo;
        this.phone = phone;
        this.countryCode = countryCode;
        this.lang = lang;
        this.phone_verified = phone_verified;
        this.email_verified = email_verified;
        this.is_active = is_active;
        this.is_merchant = is_merchant;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.nego_auto_accept = nego_auto_accept;
        this.kbis_file_path = kbis_file_path;
        this.id_file_path = id_file_path;
        this.bank_detail_id = bank_detail_id;
    }

    public int getBank_detail_id() {
        return bank_detail_id;
    }

    public void setBank_detail_id(int bank_detail_id) {
        this.bank_detail_id = bank_detail_id;
    }

    public String getKbis_file_path() {
        return kbis_file_path;
    }

    public void setKbis_file_path(String kbis_file_path) {
        this.kbis_file_path = kbis_file_path;
    }

    public String getId_file_path() {
        return id_file_path;
    }

    public void setId_file_path(String id_file_path) {
        this.id_file_path = id_file_path;
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

    public int getIs_merchant() {
        return is_merchant;
    }

    public void setIs_merchant(int is_merchant) {
        this.is_merchant = is_merchant;
    }

    public int getPhone_verified() {
        return phone_verified;
    }

    public void setPhone_verified(int phone_verified) {
        this.phone_verified = phone_verified;
    }

    public int getEmail_verified() {
        return email_verified;
    }

    public void setEmail_verified(int email_verified) {
        this.email_verified = email_verified;
    }

    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

}
