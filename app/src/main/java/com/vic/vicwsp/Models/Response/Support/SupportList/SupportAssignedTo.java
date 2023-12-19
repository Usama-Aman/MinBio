package com.vic.vicwsp.Models.Response.Support.SupportList;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SupportAssignedTo implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;

    public SupportAssignedTo(Integer id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }


    protected SupportAssignedTo(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }

        name = in.readString();
        email = in.readString();

    }

    public static final Creator<SupportAssignedTo> CREATOR = new Creator<SupportAssignedTo>() {
        @Override
        public SupportAssignedTo createFromParcel(Parcel in) {
            return new SupportAssignedTo(in);
        }

        @Override
        public SupportAssignedTo[] newArray(int size) {
            return new SupportAssignedTo[size];
        }
    };


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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }

        dest.writeValue(name);
        dest.writeString(email);

    }

}