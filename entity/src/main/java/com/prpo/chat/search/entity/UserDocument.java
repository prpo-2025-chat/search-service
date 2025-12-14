package com.prpo.chat.search.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "users")
public class UserDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String username;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String displayName;

    public UserDocument() {
    }

    public UserDocument(String id, String username, String displayName) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
