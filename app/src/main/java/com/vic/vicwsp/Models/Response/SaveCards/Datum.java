
package com.vic.vicwsp.Models.Response.SaveCards;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("card_id")
    @Expose
    private String cardId;
    @SerializedName("card_type")
    @Expose
    private String card_type;
    @SerializedName("last4")
    @Expose
    private Integer last4;
    @SerializedName("exp_month")
    @Expose
    private Integer expMonth;
    @SerializedName("exp_year")
    @Expose
    private Integer expYear;

    public Datum(Integer id, String name, String cardId, String card_type, Integer last4, Integer expMonth, Integer expYear) {
        this.id = id;
        this.name = name;
        this.cardId = cardId;
        this.card_type = card_type;
        this.last4 = last4;
        this.expMonth = expMonth;
        this.expYear = expYear;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
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

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public Integer getLast4() {
        return last4;
    }

    public void setLast4(Integer last4) {
        this.last4 = last4;
    }

    public Integer getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(Integer expMonth) {
        this.expMonth = expMonth;
    }

    public Integer getExpYear() {
        return expYear;
    }

    public void setExpYear(Integer expYear) {
        this.expYear = expYear;
    }

}
