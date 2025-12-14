package com.prpo.chat.search.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.prpo.chat.search.entity.UserDocument;

public interface UserSearchRepository extends ElasticsearchRepository<UserDocument, String> {
    
    List<UserDocument> findByUsernameContaining(String query);
    
    List<UserDocument> findByDisplayNameContaining(String query);
}
