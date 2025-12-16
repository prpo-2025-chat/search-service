package com.prpo.chat.search.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Service;

import com.prpo.chat.search.entity.MessageDocument;
import com.prpo.chat.search.entity.UserDocument;
import com.prpo.chat.search.repository.MessageSearchRepository;
import com.prpo.chat.search.repository.UserSearchRepository;

@Service
public class SearchService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final MessageSearchRepository messageRepository;
    private final UserSearchRepository userRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public SearchService(
            MessageSearchRepository messageRepository, 
            UserSearchRepository userRepository,
            ElasticsearchOperations elasticsearchOperations) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public SearchPage<MessageSearchResult> searchMessagesWithFilters(
            String query, 
            String channelId, 
            String senderId, 
            Date dateFrom, 
            Date dateTo,
            int page,
            int size) {
        
        // Validate and cap page size
        if (size <= 0) size = DEFAULT_PAGE_SIZE;
        if (size > MAX_PAGE_SIZE) size = MAX_PAGE_SIZE;
        if (page < 0) page = 0;
        
        Criteria criteria = new Criteria("content").fuzzy(query);
        
        if (channelId != null && !channelId.isEmpty()) {
            criteria = criteria.and(new Criteria("channelId").is(channelId));
        }
        if (senderId != null && !senderId.isEmpty()) {
            criteria = criteria.and(new Criteria("senderId").is(senderId));
        }
        if (dateFrom != null) {
            criteria = criteria.and(new Criteria("dateSent").greaterThanEqual(dateFrom));
        }
        if (dateTo != null) {
            criteria = criteria.and(new Criteria("dateSent").lessThanEqual(dateTo));
        }
        
        // Configure highlighting
        HighlightParameters highlightParams = HighlightParameters.builder()
            .withPreTags("<em>")
            .withPostTags("</em>")
            .build();
        
        Highlight highlight = new Highlight(
            highlightParams,
            List.of(new HighlightField("content"))
        );
        
        CriteriaQuery searchQuery = new CriteriaQuery(criteria);
        searchQuery.setPageable(PageRequest.of(page, size));
        searchQuery.setHighlightQuery(new org.springframework.data.elasticsearch.core.query.HighlightQuery(highlight, MessageDocument.class));
        
        SearchHits<MessageDocument> hits = elasticsearchOperations.search(
            searchQuery, MessageDocument.class);
        
        List<MessageSearchResult> content = hits.getSearchHits().stream()
            .map(hit -> {
                String highlighted = null;
                List<String> highlightField = hit.getHighlightField("content");
                if (highlightField != null && !highlightField.isEmpty()) {
                    highlighted = String.join(" ... ", highlightField);
                }
                return new MessageSearchResult(hit.getContent(), highlighted);
            })
            .toList();
        
        long totalHits = hits.getTotalHits();
        int totalPages = (int) Math.ceil((double) totalHits / size);
        
        return new SearchPage<>(content, page, size, totalHits, totalPages);
    }

    public List<MessageDocument> searchMessages(String query) {
        return messageRepository.findByContentContaining(query);
    }

    public List<MessageDocument> searchMessagesInChannel(String channelId, String query) {
        return messageRepository.findByChannelIdAndContentContaining(channelId, query);
    }

    public List<MessageDocument> getMessagesByChannel(String channelId) {
        return messageRepository.findByChannelId(channelId);
    }

    public List<MessageDocument> getMessagesBySender(String senderId) {
        return messageRepository.findBySenderId(senderId);
    }

    public List<UserDocument> searchUsers(String query) {
        
        Criteria criteria = new Criteria("username").fuzzy(query)
            .or(new Criteria("displayName").fuzzy(query));
        
        CriteriaQuery searchQuery = new CriteriaQuery(criteria);
        SearchHits<UserDocument> hits = elasticsearchOperations.search(
            searchQuery, UserDocument.class);
        
        // Deduplicate by ID using a map
        java.util.Map<String, UserDocument> uniqueUsers = new java.util.LinkedHashMap<>();
        for (SearchHit<UserDocument> hit : hits.getSearchHits()) {
            UserDocument user = hit.getContent();
            uniqueUsers.putIfAbsent(user.getId(), user);
        }
        
        return new ArrayList<>(uniqueUsers.values());
    }

    public MessageDocument indexMessage(MessageDocument message) {
        return messageRepository.save(message);
    }

    public MessageDocument indexMessage(String id, String channelId, String senderId, String content, Date dateSent) {
        MessageDocument doc = new MessageDocument(id, channelId, senderId, content, dateSent);
        return messageRepository.save(doc);
    }

    public UserDocument indexUser(UserDocument user) {
        return userRepository.save(user);
    }

    public UserDocument indexUser(String id, String username, String displayName) {
        UserDocument doc = new UserDocument(id, username, displayName);
        return userRepository.save(doc);
    }

    public void deleteMessage(String id) {
        messageRepository.deleteById(id);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    public Optional<MessageDocument> findMessageById(String id) {
        return messageRepository.findById(id);
    }

    public Optional<UserDocument> findUserById(String id) {
        return userRepository.findById(id);
    }
}
