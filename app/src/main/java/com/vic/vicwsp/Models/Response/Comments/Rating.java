
package com.vic.vicwsp.Models.Response.Comments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rating {

    @SerializedName("pricing")
    @Expose
    private String pricing;
    @SerializedName("quality")
    @Expose
    private String quality;
    @SerializedName("availability")
    @Expose
    private String availability;
    @SerializedName("relation")
    @Expose
    private String relation;
    @SerializedName("trustrelation")
    @Expose
    private String trustrelation;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Rating() {
    }

    /**
     * 
     * @param trustrelation
     * @param availability
     * @param pricing
     * @param quality
     * @param relation
     */
    public Rating(String pricing, String quality, String availability, String relation, String trustrelation) {
        super();
        this.pricing = pricing;
        this.quality = quality;
        this.availability = availability;
        this.relation = relation;
        this.trustrelation = trustrelation;
    }

    public String getPricing() {
        return pricing;
    }

    public void setPricing(String pricing) {
        this.pricing = pricing;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getTrustrelation() {
        return trustrelation;
    }

    public void setTrustrelation(String trustrelation) {
        this.trustrelation = trustrelation;
    }

}
