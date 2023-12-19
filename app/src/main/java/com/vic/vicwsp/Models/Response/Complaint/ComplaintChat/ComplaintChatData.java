package com.vic.vicwsp.Models.Response.Complaint.ComplaintChat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ComplaintChatData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("complaint_id")
    @Expose
    private Integer complaintId;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("comment_from")
    @Expose
    private String commentFrom;
    @SerializedName("complaint_photo")
    @Expose
    private String complaintPhoto;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("image_path")
    @Expose
    private String imagePath;
    @SerializedName("user_detail")
    @Expose
    private ComplaintChatUserDetail userDetail;
    @SerializedName("admin_detail")
    @Expose
    private ComplaintChatAdminDetail adminDetail;
    @SerializedName("complaint_detail")
    @Expose
    private ComplaintChatDetail complaintChatDetail;


    public ComplaintChatData(Integer id, Integer complaintId, String comment,
                             Integer userId, String commentFrom, String complaintPhoto,
                             String createdAt, String updatedAt, String imagePath,
                             ComplaintChatUserDetail userDetail, ComplaintChatAdminDetail adminDetail, ComplaintChatDetail complaintChatDetail) {
        this.id = id;
        this.complaintId = complaintId;
        this.comment = comment;
        this.userId = userId;
        this.commentFrom = commentFrom;
        this.complaintPhoto = complaintPhoto;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.imagePath = imagePath;
        this.userDetail = userDetail;
        this.adminDetail = adminDetail;
        this.complaintChatDetail = complaintChatDetail;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Integer complaintId) {
        this.complaintId = complaintId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCommentFrom() {
        return commentFrom;
    }

    public void setCommentFrom(String commentFrom) {
        this.commentFrom = commentFrom;
    }

    public String getComplaintPhoto() {
        return complaintPhoto;
    }

    public void setComplaintPhoto(String complaintPhoto) {
        this.complaintPhoto = complaintPhoto;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


    public ComplaintChatAdminDetail getAdminDetail() {
        return adminDetail;
    }

    public void setAdminDetail(ComplaintChatAdminDetail adminDetail) {
        this.adminDetail = adminDetail;
    }

    public ComplaintChatUserDetail getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(ComplaintChatUserDetail userDetail) {
        this.userDetail = userDetail;
    }

    public ComplaintChatDetail getComplaintChatDetail() {
        return complaintChatDetail;
    }

    public void setComplaintChatDetail(ComplaintChatDetail complaintChatDetail) {
        this.complaintChatDetail = complaintChatDetail;
    }
}