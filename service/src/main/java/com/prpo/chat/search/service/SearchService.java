package com.prpo.chat.search.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.prpo.chat.search.entity.MessageDocument;
import com.prpo.chat.search.entity.UserDocument;
import com.prpo.chat.search.repository.MessageSearchRepository;
import com.prpo.chat.search.repository.UserSearchRepository;

@Service
public class SearchService {

    private final MessageSearchRepository messageRepository;
    private final UserSearchRepository userRepository;

    public SearchService(MessageSearchRepository messageRepository, UserSearchRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
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
        List<UserDocument> byUsername = userRepository.findByUsernameContaining(query);
        List<UserDocument> byDisplayName = userRepository.findByDisplayNameContaining(query);
        
        // Combine and remove duplicates
        byUsername.addAll(byDisplayName);
        return byUsername.stream()
            .distinct()
            .toList();
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
