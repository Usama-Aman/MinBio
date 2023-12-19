package com.vic.vicwsp.Models.Response.Complaint.ComplaintList;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CompliantList implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("order_id")
    @Expose
    private Integer orderId;
    @SerializedName("order_detail_id")
    @Expose
    private String orderDetailId;
    @SerializedName("complaint_type_id")
    @Expose
    private Integer complaintTypeId;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("is_read")
    @Expose
    private Integer isRead;
    @SerializedName("assigned_to")
    @Expose
    private CompliantAssignedTo assignedTo;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("complaint_no")
    @Expose
    private String complaintNo;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("type")
    @Expose
    private ComplaintTypeModel complaintTypeModel;


    public CompliantList(Integer id, Integer orderId, String orderDetailId, Integer complaintTypeId, Integer userId, String comment, Integer isRead, CompliantAssignedTo assignedTo, String status, String complaintNo, String createdAt, String updatedAt, ComplaintTypeModel complaintTypeModel) {
        this.id = id;
        this.orderId = orderId;
        this.orderDetailId = orderDetailId;
        this.complaintTypeId = complaintTypeId;
        this.userId = userId;
        this.comment = comment;
        this.isRead = isRead;
        this.assignedTo = assignedTo;
        this.status = status;
        this.complaintNo = complaintNo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.complaintTypeModel = complaintTypeModel;
    }

    protected CompliantList(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            orderId = null;
        } else {
            orderId = in.readInt();
        }
        orderDetailId = in.readString();
        if (in.readByte() == 0) {
            complaintTypeId = null;
        } else {
            complaintTypeId = in.readInt();
        }
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
        comment = in.readString();
        if (in.readByte() == 0) {
            isRead = null;
        } else {
            isRead = in.readInt();
        }
        assignedTo = in.readParcelable(CompliantAssignedTo.class.getClassLoader());
        status = in.readString();
        complaintNo = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
    }

    public static final Creator<CompliantList> CREATOR = new Creator<CompliantList>() {
        @Override
        public CompliantList createFromParcel(Parcel in) {
            return new CompliantList(in);
        }

        @Override
        public CompliantList[] newArray(int size) {
            return new CompliantList[size];
        }
    };

    public ComplaintTypeModel getComplaintTypeModel() {
        return complaintTypeModel;
    }

    public void setComplaintTypeModel(ComplaintTypeModel complaintTypeModel) {
        this.complaintTypeModel = complaintTypeModel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(String orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public Integer getComplaintTypeId() {
        return complaintTypeId;
    }

    public void setComplaintTypeId(Integer complaintTypeId) {
        this.complaintTypeId = complaintTypeId;
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

    public CompliantAssignedTo getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(CompliantAssignedTo assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComplaintNo() {
        return complaintNo;
    }

    public void setComplaintNo(String complaintNo) {
        this.complaintNo = complaintNo;
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
        if (orderId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(orderId);
        }
        dest.writeString(orderDetailId);
        if (complaintTypeId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(complaintTypeId);
        }
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }
        dest.writeString(comment);
        if (isRead == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isRead);
        }
        dest.writeParcelable(assignedTo, flags);
        dest.writeString(status);
        dest.writeString(complaintNo);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
    }
}