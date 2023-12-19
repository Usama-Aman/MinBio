package com.vic.vicwsp.Models.Response.Complaint.ComplaintChat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ComplaintChatUserDetail {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("last_name")
    @Expose
    private Object lastName;
    @SerializedName("lang")
    @Expose
    private String lang;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("country_code")
    @Expose
    private String countryCode;
    @SerializedName("iso2")
    @Expose
    private String iso2;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("company_name")
    @Expose
    private String companyName;
    @SerializedName("siret_no")
    @Expose
    private String siretNo;
    @SerializedName("is_active")
    @Expose
    private Integer isActive;
    @SerializedName("is_merchant")
    @Expose
    private Integer isMerchant;
    @SerializedName("is_warranted")
    @Expose
    private Integer isWarranted;
    @SerializedName("overdraft_amount")
    @Expose
    private String overdraftAmount;
    @SerializedName("is_allow_overdraft")
    @Expose
    private Integer isAllowOverdraft;
    @SerializedName("phone_verified_code")
    @Expose
    private Object phoneVerifiedCode;
    @SerializedName("file")
    @Expose
    private Object file;
    @SerializedName("id_file")
    @Expose
    private Object idFile;
    @SerializedName("kbis_file")
    @Expose
    private Object kbisFile;
    @SerializedName("phone_verified_at")
    @Expose
    private String phoneVerifiedAt;
    @SerializedName("email_verified_at")
    @Expose
    private String emailVerifiedAt;
    @SerializedName("country_id")
    @Expose
    private Integer countryId;
    @SerializedName("sic_id")
    @Expose
    private String sicId;
    @SerializedName("client_id")
    @Expose
    private Object clientId;
    @SerializedName("radiation_status")
    @Expose
    private Integer radiationStatus;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("image_path")
    @Expose
    private String imagePath;
    @SerializedName("kbis_file_path")
    @Expose
    private String kbisFilePath;
    @SerializedName("id_file_path")
    @Expose
    private String idFilePath;
    @SerializedName("kbis_file_ext")
    @Expose
    private String kbisFileExt;
    @SerializedName("id_file_ext")
    @Expose
    private String idFileExt;
    @SerializedName("full_name")
    @Expose
    private String fullName;

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

    public Object getLastName() {
        return lastName;
    }

    public void setLastName(Object lastName) {
        this.lastName = lastName;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getIso2() {
        return iso2;
    }

    public void setIso2(String iso2) {
        this.iso2 = iso2;
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

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Integer getIsMerchant() {
        return isMerchant;
    }

    public void setIsMerchant(Integer isMerchant) {
        this.isMerchant = isMerchant;
    }

    public Integer getIsWarranted() {
        return isWarranted;
    }

    public void setIsWarranted(Integer isWarranted) {
        this.isWarranted = isWarranted;
    }

    public String getOverdraftAmount() {
        return overdraftAmount;
    }

    public void setOverdraftAmount(String overdraftAmount) {
        this.overdraftAmount = overdraftAmount;
    }

    public Integer getIsAllowOverdraft() {
        return isAllowOverdraft;
    }

    public void setIsAllowOverdraft(Integer isAllowOverdraft) {
        this.isAllowOverdraft = isAllowOverdraft;
    }

    public Object getPhoneVerifiedCode() {
        return phoneVerifiedCode;
    }

    public void setPhoneVerifiedCode(Object phoneVerifiedCode) {
        this.phoneVerifiedCode = phoneVerifiedCode;
    }

    public Object getFile() {
        return file;
    }

    public void setFile(Object file) {
        this.file = file;
    }

    public Object getIdFile() {
        return idFile;
    }

    public void setIdFile(Object idFile) {
        this.idFile = idFile;
    }

    public Object getKbisFile() {
        return kbisFile;
    }

    public void setKbisFile(Object kbisFile) {
        this.kbisFile = kbisFile;
    }

    public String getPhoneVerifiedAt() {
        return phoneVerifiedAt;
    }

    public void setPhoneVerifiedAt(String phoneVerifiedAt) {
        this.phoneVerifiedAt = phoneVerifiedAt;
    }

    public String getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(String emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getSicId() {
        return sicId;
    }

    public void setSicId(String sicId) {
        this.sicId = sicId;
    }

    public Object getClientId() {
        return clientId;
    }

    public void setClientId(Object clientId) {
        this.clientId = clientId;
    }

    public Integer getRadiationStatus() {
        return radiationStatus;
    }

    public void setRadiationStatus(Integer radiationStatus) {
        this.radiationStatus = radiationStatus;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getKbisFilePath() {
        return kbisFilePath;
    }

    public void setKbisFilePath(String kbisFilePath) {
        this.kbisFilePath = kbisFilePath;
    }

    public String getIdFilePath() {
        return idFilePath;
    }

    public void setIdFilePath(String idFilePath) {
        this.idFilePath = idFilePath;
    }

    public String getKbisFileExt() {
        return kbisFileExt;
    }

    public void setKbisFileExt(String kbisFileExt) {
        this.kbisFileExt = kbisFileExt;
    }

    public String getIdFileExt() {
        return idFileExt;
    }

    public void setIdFileExt(String idFileExt) {
        this.idFileExt = idFileExt;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

}