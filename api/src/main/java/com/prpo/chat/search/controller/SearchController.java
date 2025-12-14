package com.prpo.chat.search.controller;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prpo.chat.search.dto.MessageSearchRequestDto;
import com.prpo.chat.search.dto.MessageSearchResultDto;
import com.prpo.chat.search.dto.UserSearchResultDto;
import com.prpo.chat.search.entity.MessageDocument;
import com.prpo.chat.search.entity.UserDocument;
import com.prpo.chat.search.service.SearchService;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/messages")
    public List<MessageSearchResultDto> searchMessages(
            @RequestParam String query,
            @RequestParam(required = false) String channelId) {
        
        List<MessageDocument> results;
        if (channelId != null && !channelId.isEmpty()) {
            results = searchService.searchMessagesInChannel(channelId, query);
        } else {
            results = searchService.searchMessages(query);
        }
        
        return results.stream()
            .map(this::toMessageResultDto)
            .collect(Collectors.toList());
    }

    @GetMapping("/messages/channel/{channelId}")
    public List<MessageSearchResultDto> getMessagesByChannel(@PathVariable String channelId) {
        return searchService.getMessagesByChannel(channelId).stream()
            .map(this::toMessageResultDto)
            .collect(Collectors.toList());
    }

    @GetMapping("/messages/sender/{senderId}")
    public List<MessageSearchResultDto> getMessagesBySender(@PathVariable String senderId) {
        return searchService.getMessagesBySender(senderId).stream()
            .map(this::toMessageResultDto)
            .collect(Collectors.toList());
    }

    @GetMapping("/users")
    public List<UserSearchResultDto> searchUsers(@RequestParam String query) {
        return searchService.searchUsers(query).stream()
            .map(this::toUserResultDto)
            .collect(Collectors.toList());
    }

    @PostMapping("/index/message")
    public MessageSearchResultDto indexMessage(@RequestBody MessageDocument message) {
        MessageDocument indexed = searchService.indexMessage(message);
        return toMessageResultDto(indexed);
    }

    @PostMapping("/index/user")
    public UserSearchResultDto indexUser(@RequestBody UserDocument user) {
        UserDocument indexed = searchService.indexUser(user);
        return toUserResultDto(indexed);
    }

    @DeleteMapping("/index/message/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable String id) {
        searchService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/index/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        searchService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    private MessageSearchResultDto toMessageResultDto(MessageDocument doc) {
        return new MessageSearchResultDto(
            doc.getId(),
            doc.getChannelId(),
            doc.getSenderId(),
            doc.getContent(),
            doc.getDateSent(),
            null
        );
    }

    private UserSearchResultDto toUserResultDto(UserDocument doc) {
        return new UserSearchResultDto(
            doc.getId(),
            doc.getUsername(),
            doc.getDisplayName()
        );
    }
}
