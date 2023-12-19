
package com.vic.vicwsp.Models.Response.SendComplaintResponse;

import java.util.ArrayList;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("types")
    @Expose
    private ArrayList<Type> types = null;
    @SerializedName("orders")
    @Expose
    private ArrayList<Order> orders = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Data() {
    }

    /**
     * 
     * @param types
     * @param orders
     */
    public Data(ArrayList<Type> types, ArrayList<Order> orders) {
        super();
        this.types = types;
        this.orders = orders;
    }

    public ArrayList<Type> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<Type> types) {
        this.types = types;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

}
