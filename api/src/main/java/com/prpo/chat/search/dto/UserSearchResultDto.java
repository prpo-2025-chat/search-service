package com.prpo.chat.search.dto;

public class UserSearchResultDto {

    private String id;
    private String username;
    private String displayName;

    public UserSearchResultDto() {}

    public UserSearchResultDto(String id, String username, String displayName) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }

    public void setId(String id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}
