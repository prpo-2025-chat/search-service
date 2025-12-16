package com.prpo.chat.search.controller;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prpo.chat.search.dto.IndexMessageRequestDto;
import com.prpo.chat.search.dto.IndexUserRequestDto;
import com.prpo.chat.search.dto.MessageSearchRequestDto;
import com.prpo.chat.search.dto.MessageSearchResultDto;
import com.prpo.chat.search.dto.SearchPageDto;
import com.prpo.chat.search.dto.UserSearchResultDto;
import com.prpo.chat.search.entity.MessageDocument;
import com.prpo.chat.search.entity.UserDocument;
import com.prpo.chat.search.service.MessageSearchResult;
import com.prpo.chat.search.service.SearchPage;
import com.prpo.chat.search.service.SearchService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/messages")
    public SearchPageDto<MessageSearchResultDto> searchMessages(
            @RequestParam String query,
            @RequestParam(required = false) String channelId,
            @RequestParam(required = false) String senderId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        SearchPage<MessageSearchResult> results = searchService.searchMessagesWithFilters(
                query, channelId, senderId, dateFrom, dateTo, page, size);

        List<MessageSearchResultDto> content = results.getContent().stream()
                .map(this::toMessageResultDto)
                .collect(Collectors.toList());

        return new SearchPageDto<>(content, results.getPage(), results.getSize(),
                results.getTotalElements(), results.getTotalPages());
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
    public MessageSearchResultDto indexMessage(@Valid @RequestBody IndexMessageRequestDto request) {
        MessageDocument indexed = searchService.indexMessage(
                request.getId(),
                request.getChannelId(),
                request.getSenderId(),
                request.getContent(),
                request.getDateSent());
        return toMessageResultDto(indexed);
    }

    @PostMapping("/index/user")
    public UserSearchResultDto indexUser(@Valid @RequestBody IndexUserRequestDto request) {
        UserDocument indexed = searchService.indexUser(
                request.getId(),
                request.getUsername(),
                request.getDisplayName());
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

    private MessageSearchResultDto toMessageResultDto(MessageSearchResult result) {
        MessageDocument doc = result.getDocument();
        return new MessageSearchResultDto(
                doc.getId(),
                doc.getChannelId(),
                doc.getSenderId(),
                doc.getContent(),
                doc.getDateSent(),
                result.getHighlightedContent());
    }

    private MessageSearchResultDto toMessageResultDto(MessageDocument doc) {
        return new MessageSearchResultDto(
                doc.getId(),
                doc.getChannelId(),
                doc.getSenderId(),
                doc.getContent(),
                doc.getDateSent(),
                null);
    }

    private UserSearchResultDto toUserResultDto(UserDocument doc) {
        return new UserSearchResultDto(
                doc.getId(),
                doc.getUsername(),
                doc.getDisplayName());
    }
}
