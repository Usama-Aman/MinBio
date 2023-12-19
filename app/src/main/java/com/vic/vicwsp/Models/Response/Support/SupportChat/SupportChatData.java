package com.vic.vicwsp.Models.Response.Support.SupportChat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SupportChatData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("support_id")
    @Expose
    private Integer supportId;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("comment_from")
    @Expose
    private String commentFrom;
    @SerializedName("support_photo")
    @Expose
    private String supportPhoto;
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
    private SupportChatUserDetail supportChatUserDetail;
    @SerializedName("admin_detail")
    @Expose
    private SupportChatAdminDetail adminDetail;
    @SerializedName("support_detail")
    @Expose
    private SupportChatDetail supportChatDetail;


    public SupportChatData(Integer id, Integer supportId, String comment,
                             Integer userId, String commentFrom, String supportPhoto,
                             String createdAt, String updatedAt, String imagePath,
                           SupportChatUserDetail supportChatUserDetail, SupportChatAdminDetail adminDetail, SupportChatDetail supportChatDetail) {
        this.id = id;
        this.supportId = supportId;
        this.comment = comment;
        this.userId = userId;
        this.commentFrom = commentFrom;
        this.supportPhoto = supportPhoto;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.imagePath = imagePath;
        this.supportChatUserDetail = supportChatUserDetail;
        this.adminDetail = adminDetail;
        this.supportChatDetail = supportChatDetail;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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


    public SupportChatAdminDetail getAdminDetail() {
        return adminDetail;
    }

    public void setAdminDetail(SupportChatAdminDetail adminDetail) {
        this.adminDetail = adminDetail;
    }

    public SupportChatDetail getSupportChatDetail() {
        return supportChatDetail;
    }

    public void setSupportChatDetail(SupportChatDetail supportChatDetail) {
        this.supportChatDetail = supportChatDetail;
    }

    public Integer getSupportId() {
        return supportId;
    }

    public void setSupportId(Integer supportId) {
        this.supportId = supportId;
    }

    public String getSupportPhoto() {
        return supportPhoto;
    }

    public void setSupportPhoto(String supportPhoto) {
        this.supportPhoto = supportPhoto;
    }

    public SupportChatUserDetail getSupportChatUserDetail() {
        return supportChatUserDetail;
    }

    public void setSupportChatUserDetail(SupportChatUserDetail supportChatUserDetail) {
        this.supportChatUserDetail = supportChatUserDetail;
    }
}