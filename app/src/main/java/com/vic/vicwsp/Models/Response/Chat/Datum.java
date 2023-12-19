
package com.vic.vicwsp.Models.Response.Chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("chat_id")
    @Expose
    private Integer chatId;
    @SerializedName("sender_id")
    @Expose
    private Integer senderId;
    @SerializedName("receiver_id")
    @Expose
    private Integer receiverId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("is_delivered")
    @Expose
    private Integer isDelivered;
    @SerializedName("is_seen")
    @Expose
    private Integer isSeen;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("file_path")
    @Expose
    private String filePath;
    @SerializedName("duration")
    @Expose
    private String duration;

    private boolean audioPlaying = false;
    private boolean isAudioPaused = false;
    private int audioProgressBar = 0;

    public Datum(Integer id, Integer chatId, Integer senderId, Integer receiverId, String type, String message, Integer isDelivered, Integer isSeen, String date, String filePath, String duration) {
        this.id = id;
        this.chatId = chatId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = type;
        this.message = message;
        this.isDelivered = isDelivered;
        this.isSeen = isSeen;
        this.date = date;
        this.filePath = filePath;
        this.duration = duration;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isAudioPlaying() {
        return audioPlaying;
    }

    public void setAudioPlaying(boolean audioPlaying) {
        this.audioPlaying = audioPlaying;
    }

    public boolean isAudioPaused() {
        return isAudioPaused;
    }

    public void setAudioPaused(boolean audioPaused) {
        isAudioPaused = audioPaused;
    }

    public int getAudioProgressBar() {
        return audioProgressBar;
    }

    public void setAudioProgressBar(int audioProgressBar) {
        this.audioProgressBar = audioProgressBar;
    }


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getIsDelivered() {
        return isDelivered;
    }

    public void setIsDelivered(Integer isDelivered) {
        this.isDelivered = isDelivered;
    }

    public Integer getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(Integer isSeen) {
        this.isSeen = isSeen;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
