
package com.vic.vicwsp.Models.Response.CartOrder;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("failed")
    @Expose
    private List<Failed> failed = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Data() {
    }

    /**
     * 
     * @param failed
     */
    public Data(List<Failed> failed) {
        super();
        this.failed = failed;
    }

    public List<Failed> getFailed() {
        return failed;
    }

    public void setFailed(List<Failed> failed) {
        this.failed = failed;
    }

}
