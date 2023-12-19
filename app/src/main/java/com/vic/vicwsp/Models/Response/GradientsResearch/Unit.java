
package com.vic.vicwsp.Models.Response.GradientsResearch;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Unit {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("sign")
    @Expose
    private String sign;
    @SerializedName("subunits")
    @Expose
    private List<Subunit> subunits = new ArrayList<>();


    public Unit(Integer id, String name, String sign, List<Subunit> subunits) {
        super();
        this.id = id;
        this.name = name;
        this.sign = sign;
        this.subunits = subunits;
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

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public List<Subunit> getSubunits() {
        return subunits;
    }

    public void setSubunits(List<Subunit> subunits) {
        this.subunits = subunits;
    }

}
