package com.prpo.chat.search.service;

import com.prpo.chat.search.entity.MessageDocument;

public class MessageSearchResult {
    
    private final MessageDocument document;
    private final String highlightedContent;

    public MessageSearchResult(MessageDocument document, String highlightedContent) {
        this.document = document;
        this.highlightedContent = highlightedContent;
    }

    public MessageDocument getDocument() { return document; }
    public String getHighlightedContent() { return highlightedContent; }
}
