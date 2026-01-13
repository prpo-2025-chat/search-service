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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @Operation(summary = "Search messages with filters", description = "Full-text search on messages with optional filters for channel, sender, and date range")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    })
    @GetMapping("/messages")
    public SearchPageDto<MessageSearchResultDto> searchMessages(
            @Parameter(description = "Search query text", required = true) @RequestParam @NotBlank String query,

            @Parameter(description = "Filter by channel ID") @RequestParam(required = false) String channelId,

            @Parameter(description = "Filter by sender ID") @RequestParam(required = false) String senderId,

            @Parameter(description = "Filter messages sent after this date (ISO format)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date dateFrom,

            @Parameter(description = "Filter messages sent before this date (ISO format)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date dateTo,

            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size) {

        SearchPage<MessageSearchResult> results = searchService.searchMessagesWithFilters(
                query, channelId, senderId, dateFrom, dateTo, page, size);

        List<MessageSearchResultDto> content = results.getContent().stream()
                .map(this::toMessageResultDto)
                .collect(Collectors.toList());

        return new SearchPageDto<>(content, results.getPage(), results.getSize(),
                results.getTotalElements(), results.getTotalPages());
    }

    @Operation(summary = "Get messages by channel", description = "Returns all indexed messages for a specific channel")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Messages retrieved successfully")
    })
    @GetMapping("/messages/channel/{channelId}")
    public List<MessageSearchResultDto> getMessagesByChannel(
            @Parameter(description = "Channel ID", required = true) @PathVariable @NotBlank String channelId) {
        return searchService.getMessagesByChannel(channelId).stream()
                .map(this::toMessageResultDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get messages by sender", description = "Returns all indexed messages from a specific sender")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Messages retrieved successfully")
    })
    @GetMapping("/messages/sender/{senderId}")
    public List<MessageSearchResultDto> getMessagesBySender(
            @Parameter(description = "Sender user ID", required = true) @PathVariable @NotBlank String senderId) {
        return searchService.getMessagesBySender(senderId).stream()
                .map(this::toMessageResultDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Search users", description = "Full-text search on users by username or display name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    @GetMapping("/users")
    public List<UserSearchResultDto> searchUsers(
            @Parameter(description = "Search query text", required = true) @RequestParam @NotBlank String query) {
        return searchService.searchUsers(query).stream()
                .map(this::toUserResultDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Index a message", description = "Adds or updates a message in the search index")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Message indexed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    @PostMapping("/index/message")
    public MessageSearchResultDto indexMessage(
            @Parameter(description = "Message data to index", required = true) @Valid @RequestBody IndexMessageRequestDto request) {
        MessageDocument indexed = searchService.indexMessage(
                request.getId(),
                request.getChannelId(),
                request.getSenderId(),
                request.getContent(),
                request.getDateSent());
        return toMessageResultDto(indexed);
    }

    @Operation(summary = "Index a user", description = "Adds or updates a user in the search index")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User indexed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    @PostMapping("/index/user")
    public UserSearchResultDto indexUser(
            @Parameter(description = "User data to index", required = true) @Valid @RequestBody IndexUserRequestDto request) {
        UserDocument indexed = searchService.indexUser(
                request.getId(),
                request.getUsername(),
                request.getDisplayName());
        return toUserResultDto(indexed);
    }

    @Operation(summary = "Delete a message from the index", description = "Removes a message from the search index")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Message deleted from index"),
            @ApiResponse(responseCode = "404", description = "Message not found in index")
    })
    @DeleteMapping("/index/message/{id}")
    public ResponseEntity<Void> deleteMessage(
            @Parameter(description = "Message ID", required = true) @PathVariable @NotBlank String id) {
        searchService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a user from the index", description = "Removes a user from the search index")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted from index"),
            @ApiResponse(responseCode = "404", description = "User not found in index")
    })
    @DeleteMapping("/index/user/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID", required = true) @PathVariable @NotBlank String id) {
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
