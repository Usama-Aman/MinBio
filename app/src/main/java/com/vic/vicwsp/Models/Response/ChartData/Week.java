
package com.vic.vicwsp.Models.Response.ChartData;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Week {

    @SerializedName("averageprice")
    @Expose
    private String averageprice;
    @SerializedName("report")
    @Expose
    private List<Report> report = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Week() {
    }

    /**
     * 
     * @param averageprice
     * @param report
     */
    public Week(String averageprice, List<Report> report) {
        super();
        this.averageprice = averageprice;
        this.report = report;
    }

    public String getAverageprice() {
        return averageprice;
    }

    public void setAverageprice(String averageprice) {
        this.averageprice = averageprice;
    }

    public List<Report> getReport() {
        return report;
    }

    public void setReport(List<Report> report) {
        this.report = report;
    }

}
