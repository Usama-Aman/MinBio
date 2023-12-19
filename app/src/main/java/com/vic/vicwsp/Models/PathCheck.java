package com.vic.vicwsp.Models;

public class PathCheck {

    private int id;
    private String path;
    private boolean isURL;

    public PathCheck(int id, String path, boolean isURL) {
        this.id = id;
        this.path = path;
        this.isURL = isURL;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isURL() {
        return isURL;
    }

    public void setURL(boolean URL) {
        isURL = URL;
    }
}
