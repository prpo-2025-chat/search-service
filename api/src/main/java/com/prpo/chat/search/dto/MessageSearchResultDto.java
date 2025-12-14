package com.prpo.chat.search.dto;

import java.util.Date;

public class MessageSearchResultDto {

    private String id;
    private String channelId;
    private String senderId;
    private String content;
    private Date dateSent;
    private String highlightedContent;

    public MessageSearchResultDto() {}

    public MessageSearchResultDto(String id, String channelId, String senderId, 
                                   String content, Date dateSent, String highlightedContent) {
        this.id = id;
        this.channelId = channelId;
        this.senderId = senderId;
        this.content = content;
        this.dateSent = dateSent;
        this.highlightedContent = highlightedContent;
    }

    public String getId() { return id; }
    public String getChannelId() { return channelId; }
    public String getSenderId() { return senderId; }
    public String getContent() { return content; }
    public Date getDateSent() { return dateSent; }
    public String getHighlightedContent() { return highlightedContent; }

    public void setId(String id) { this.id = id; }
    public void setChannelId(String channelId) { this.channelId = channelId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setContent(String content) { this.content = content; }
    public void setDateSent(Date dateSent) { this.dateSent = dateSent; }
    public void setHighlightedContent(String highlightedContent) { this.highlightedContent = highlightedContent; }
}
