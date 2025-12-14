package com.prpo.chat.search.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.prpo.chat.search.entity.MessageDocument;

public interface MessageSearchRepository extends ElasticsearchRepository<MessageDocument, String> {

    List<MessageDocument> findByContentContaining(String query);

    List<MessageDocument> findByChannelId(String channelId);

    List<MessageDocument> findBySenderId(String senderId);

    List<MessageDocument> findByChannelIdAndContentContaining(String channelId, String query);
}
