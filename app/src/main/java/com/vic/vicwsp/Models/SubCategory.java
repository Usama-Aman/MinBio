package com.vic.vicwsp.Models;

public class SubCategory {

    private Integer id;
    private String name;
    private String imagePath;
    private int type;

    public SubCategory(Integer id, String name, String imagePath, int type) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
        this.type = type;
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


    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
