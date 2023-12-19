
package com.vic.vicwsp.Models.Response.Outstanding;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("quote_id")
    @Expose
    private String quoteId;
    @SerializedName("merchant_id")
    @Expose
    private Integer merchantId;
    @SerializedName("merchantname")
    @Expose
    private String merchantname;
    @SerializedName("amount")
    @Expose
    private Double amount;
    @SerializedName("status")
    @Expose
    private String status;

    /**
     * No args constructor for use in serialization
     */
    public Datum() {
    }

    /**
     * @param amount
     * @param merchantId
     * @param merchantname
     * @param quoteId
     * @param status
     */
    public Datum(String quoteId, Integer merchantId, String merchantname, Double amount, String status) {
        super();
        this.quoteId = quoteId;
        this.merchantId = merchantId;
        this.merchantname = merchantname;
        this.amount = amount;
        this.status = status;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantname() {
        return merchantname;
    }

    public void setMerchantname(String merchantname) {
        this.merchantname = merchantname;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
