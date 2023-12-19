package com.vic.vicwsp.Models;

public class User {

    private Integer id;
    private String name;
    private String lastName;
    private String email;
    private String companyName;
    private String siretNo;
    private String phone = "";
    private String lang = "";
    private String countryCode = "";
    private String lat = "";
    private String lng = "";
    private String address = "";
    private int is_merchant;
    private int negoAutoAccept;
    private String kbisPath;
    private String idPath;
    private int bankDetailId;

    public User(Integer id, String name, String lastName, String email, String companyName, String siretNo, String phone, String lang, String countryCode, String lat, String lng, String address, int is_merchant, int negoAutoAccept, String kbisPath, String idPath, int bankDetailId) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.companyName = companyName;
        this.siretNo = siretNo;
        this.phone = phone;
        this.lang = lang;
        this.countryCode = countryCode;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.is_merchant = is_merchant;
        this.negoAutoAccept = negoAutoAccept;
        this.kbisPath = kbisPath;
        this.idPath = idPath;
        this.bankDetailId = bankDetailId;
    }

    public int getBankDetailId() {
        return bankDetailId;
    }

    public void setBankDetailId(int bankDetailId) {
        this.bankDetailId = bankDetailId;
    }

    public String getKbisPath() {
        return kbisPath;
    }

    public void setKbisPath(String kbisPath) {
        this.kbisPath = kbisPath;
    }

    public String getIdPath() {
        return idPath;
    }

    public void setIdPath(String idPath) {
        this.idPath = idPath;
    }

    public int getNegoAutoAccept() {
        return negoAutoAccept;
    }

    public void setNegoAutoAccept(int negoAutoAccept) {
        this.negoAutoAccept = negoAutoAccept;
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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
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

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }


}
