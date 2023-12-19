
package com.vic.vicwsp.Models.Response.DriversTrackingList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Driver {

    @SerializedName("is_reviewed")
    @Expose
    private Integer isReviewed;
    @SerializedName("current_lat")
    @Expose
    private String currentLat;
    @SerializedName("current_lng")
    @Expose
    private String currentLng;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("profile_image")
    @Expose
    private String profileImage;
    @SerializedName("truck_plate")
    @Expose
    private String truckPlate;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Driver() {
    }

    /**
     * 
     * @param currentLat
     * @param truckPlate
     * @param phone
     * @param isReviewed
     * @param name
     * @param currentLng
     * @param id
     * @param profileImage
     */
    public Driver(Integer isReviewed, String currentLat, String currentLng, Integer id, String name, String phone, String profileImage, String truckPlate) {
        super();
        this.isReviewed = isReviewed;
        this.currentLat = currentLat;
        this.currentLng = currentLng;
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.profileImage = profileImage;
        this.truckPlate = truckPlate;
    }

    public Integer getIsReviewed() {
        return isReviewed;
    }

    public void setIsReviewed(Integer isReviewed) {
        this.isReviewed = isReviewed;
    }

    public String getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(String currentLat) {
        this.currentLat = currentLat;
    }

    public String getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(String currentLng) {
        this.currentLng = currentLng;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getTruckPlate() {
        return truckPlate;
    }

    public void setTruckPlate(String truckPlate) {
        this.truckPlate = truckPlate;
    }

}
