package com.vic.vicwsp.Models.Response.Support.SupportList;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SupportList implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("support_topic_id")
    @Expose
    private Integer supportTopicId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("is_read")
    @Expose
    private Integer isRead;
    @SerializedName("support_no")
    @Expose
    private String supportNo;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("assigned_to")
    @Expose
    private Integer assignedTo;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("assigned_detail")
    @Expose
    private SupportAssignedTo supportAssignedTo;

    public SupportList(Integer id, Integer userId, Integer supportTopicId, String type, String email,
                       String comment, Integer isRead, String supportNo, String status, Integer assignedTo, String createdAt, String updatedAt, SupportAssignedTo supportAssignedTo) {
        this.id = id;
        this.userId = userId;
        this.supportTopicId = supportTopicId;
        this.type = type;
        this.email = email;
        this.comment = comment;
        this.isRead = isRead;
        this.supportNo = supportNo;
        this.assignedTo = assignedTo;
        this.status = status;
        this.supportAssignedTo = supportAssignedTo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    protected SupportList(Parcel in) {

        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }

        if (in.readByte() == 0) {
            supportTopicId = null;
        } else {
            supportTopicId = in.readInt();
        }


        type = in.readString();
        email = in.readString();
        comment = in.readString();

        if (in.readByte() == 0) {
            isRead = null;
        } else {
            isRead = in.readInt();
        }

        supportNo = in.readString();

        if (in.readByte() == 0) {
            assignedTo = null;
        } else {
            assignedTo = in.readInt();
        }

        status = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();

        this.supportAssignedTo = ((SupportAssignedTo) in.readValue((SupportAssignedTo.class.getClassLoader())));
    }

    public static final Creator<SupportList> CREATOR = new Creator<SupportList>() {
        @Override
        public SupportList createFromParcel(Parcel in) {
            return new SupportList(in);
        }

        @Override
        public SupportList[] newArray(int size) {
            return new SupportList[size];
        }
    };


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getSupportTopicId() {
        return supportTopicId;
    }

    public void setSupportTopicId(Integer supportTopicId) {
        this.supportTopicId = supportTopicId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSupportNo() {
        return supportNo;
    }

    public void setSupportNo(String supportNo) {
        this.supportNo = supportNo;
    }

    public Integer getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Integer assignedTo) {
        this.assignedTo = assignedTo;
    }

    public SupportAssignedTo getSupportAssignedTo() {
        return supportAssignedTo;
    }

    public void setSupportAssignedTo(SupportAssignedTo supportAssignedTo) {
        this.supportAssignedTo = supportAssignedTo;
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


        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }

        if (supportTopicId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(supportTopicId);
        }


        dest.writeString(type);
        dest.writeString(email);
        dest.writeString(comment);

        if (isRead == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isRead);
        }

        dest.writeString(supportNo);

        if (assignedTo == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(assignedTo);
        }

        dest.writeString(email);

        dest.writeString(status);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);

        dest.writeValue(supportAssignedTo);

    }

}