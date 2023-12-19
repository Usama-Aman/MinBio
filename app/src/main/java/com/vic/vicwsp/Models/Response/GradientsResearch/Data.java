
package com.vic.vicwsp.Models.Response.GradientsResearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Data {

    @SerializedName("categories")
    @Expose
    private ArrayList<Category> categories = null;
    @SerializedName("countries")
    @Expose
    private ArrayList<Country> countries = null;
    @SerializedName("sizes")
    @Expose
    private ArrayList<Size> sizes = null;
    @SerializedName("classes")
    @Expose
    private ArrayList<Classes> classes = null;
    @SerializedName("units")
    @Expose
    private ArrayList<Unit> units = null;
    @SerializedName("vats")
    @Expose
    private ArrayList<Vat> vats= null;

    public Data(ArrayList<Category> categories, ArrayList<Country> countries, ArrayList<Size> sizes, ArrayList<Classes> classes, ArrayList<Unit> units, ArrayList<Vat> vats) {
        this.categories = categories;
        this.countries = countries;
        this.sizes = sizes;
        this.classes = classes;
        this.units = units;
        this.vats = vats;
    }

    public ArrayList<Vat> getVats() {
        return vats;
    }

    public void setVats(ArrayList<Vat> vats) {
        this.vats = vats;
    }

    public ArrayList<Classes> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<Classes> classes) {
        this.classes = classes;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public ArrayList<Country> getCountries() {
        return countries;
    }

    public void setCountries(ArrayList<Country> countries) {
        this.countries = countries;
    }

    public ArrayList<Size> getSizes() {
        return sizes;
    }

    public void setSizes(ArrayList<Size> sizes) {
        this.sizes = sizes;
    }

    public ArrayList<Unit> getUnits() {
        return units;
    }

    public void setUnits(ArrayList<Unit> units) {
        this.units = units;
    }
}
