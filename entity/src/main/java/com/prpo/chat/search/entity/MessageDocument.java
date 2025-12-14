package com.prpo.chat.search.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "messages")
public class MessageDocument {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String channelId;

    @Field(type = FieldType.Keyword)
    private String senderId;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String content;

    @Field(type = FieldType.Date)
    private Date dateSent;

    public MessageDocument() {}

    public MessageDocument(String id, String channelId, String senderId, String content, Date dateSent) {
        this.id = id;
        this.channelId = channelId;
        this.senderId = senderId;
        this.content = content;
        this.dateSent = dateSent;
    }

    public String getId() { return id; }
    public String getChannelId() { return channelId; }
    public String getSenderId() { return senderId; }
    public String getContent() { return content; }
    public Date getDateSent() { return dateSent; }

    public void setId(String id) { this.id = id; }
    public void setChannelId(String channelId) { this.channelId = channelId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setContent(String content) { this.content = content; }
    public void setDateSent(Date dateSent) { this.dateSent = dateSent; }
}
