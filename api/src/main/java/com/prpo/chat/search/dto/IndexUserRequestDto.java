package com.prpo.chat.search.dto;

import jakarta.validation.constraints.NotBlank;

public class IndexUserRequestDto {

    @NotBlank(message = "id must not be blank")
    private String id;

    @NotBlank(message = "username must not be blank")
    private String username;

    private String displayName;

    public IndexUserRequestDto() {
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
