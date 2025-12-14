package com.prpo.chat.search.dto;

import java.util.Date;

public class MessageSearchRequestDto {

    private String query;
    private String channelId;
    private String senderId;
    private Date dateFrom;
    private Date dateTo;

    public MessageSearchRequestDto() {}

    public String getQuery() { return query; }
    public String getChannelId() { return channelId; }
    public String getSenderId() { return senderId; }
    public Date getDateFrom() { return dateFrom; }
    public Date getDateTo() { return dateTo; }

    public void setQuery(String query) { this.query = query; }
    public void setChannelId(String channelId) { this.channelId = channelId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setDateFrom(Date dateFrom) { this.dateFrom = dateFrom; }
    public void setDateTo(Date dateTo) { this.dateTo = dateTo; }
}
