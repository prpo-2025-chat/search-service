package com.prpo.chat.search.dto;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class IndexMessageRequestDto {

    @NotBlank(message = "id must not be blank")
    private String id;

    @NotBlank(message = "channelId must not be blank")
    private String channelId;

    @NotBlank(message = "senderId must not be blank")
    private String senderId;

    @NotBlank(message = "content must not be blank")
    private String content;

    @NotNull(message = "dateSent must not be null")
    private Date dateSent;

    public IndexMessageRequestDto() {
    }

    public String getId() {
        return id;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getContent() {
        return content;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }
}
