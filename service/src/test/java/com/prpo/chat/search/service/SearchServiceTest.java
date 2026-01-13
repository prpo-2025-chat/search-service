package com.prpo.chat.search.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import com.prpo.chat.search.entity.MessageDocument;
import com.prpo.chat.search.entity.UserDocument;
import com.prpo.chat.search.repository.MessageSearchRepository;
import com.prpo.chat.search.repository.UserSearchRepository;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private MessageSearchRepository messageRepository;

    @Mock
    private UserSearchRepository userRepository;

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @InjectMocks
    private SearchService searchService;

    @Test
    void indexMessage_savesToRepository() {
        MessageDocument doc = new MessageDocument("msg-1", "channel-1", "user-1", "hello world", new Date());
        when(messageRepository.save(any(MessageDocument.class))).thenReturn(doc);

        MessageDocument result = searchService.indexMessage(doc);

        assertEquals("msg-1", result.getId());
        verify(messageRepository).save(doc);
    }

    @Test
    void indexMessage_withParameters_createsAndSavesDocument() {
        String id = "msg-1";
        String channelId = "channel-1";
        String senderId = "user-1";
        String content = "hello world";
        Date dateSent = new Date();

        when(messageRepository.save(any(MessageDocument.class))).thenAnswer(inv -> inv.getArgument(0));

        MessageDocument result = searchService.indexMessage(id, channelId, senderId, content, dateSent);

        assertEquals(id, result.getId());
        assertEquals(channelId, result.getChannelId());
        assertEquals(senderId, result.getSenderId());
        assertEquals(content, result.getContent());
    }

    @Test
    void indexUser_savesToRepository() {
        UserDocument doc = new UserDocument("user-1", "john_doe", "John Doe");
        when(userRepository.save(any(UserDocument.class))).thenReturn(doc);

        UserDocument result = searchService.indexUser(doc);

        assertEquals("user-1", result.getId());
        verify(userRepository).save(doc);
    }

    @Test
    void indexUser_withParameters_createsAndSavesDocument() {
        when(userRepository.save(any(UserDocument.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDocument result = searchService.indexUser("user-1", "john_doe", "John Doe");

        assertEquals("user-1", result.getId());
        assertEquals("john_doe", result.getUsername());
        assertEquals("John Doe", result.getDisplayName());
    }

    @Test
    void deleteMessage_removesFromRepository() {
        searchService.deleteMessage("msg-1");

        verify(messageRepository).deleteById("msg-1");
    }

    @Test
    void deleteUser_removesFromRepository() {
        searchService.deleteUser("user-1");

        verify(userRepository).deleteById("user-1");
    }

    @Test
    void findMessageById_returnsOptional() {
        MessageDocument doc = new MessageDocument("msg-1", "channel-1", "user-1", "hello", new Date());
        when(messageRepository.findById("msg-1")).thenReturn(Optional.of(doc));

        Optional<MessageDocument> result = searchService.findMessageById("msg-1");

        assertTrue(result.isPresent());
        assertEquals("msg-1", result.get().getId());
    }

    @Test
    void findUserById_returnsOptional() {
        UserDocument doc = new UserDocument("user-1", "john_doe", "John Doe");
        when(userRepository.findById("user-1")).thenReturn(Optional.of(doc));

        Optional<UserDocument> result = searchService.findUserById("user-1");

        assertTrue(result.isPresent());
        assertEquals("user-1", result.get().getId());
    }

    @Test
    void searchMessages_callsRepositoryWithQuery() {
        MessageDocument doc = new MessageDocument("msg-1", "channel-1", "user-1", "hello world", new Date());
        when(messageRepository.findByContentContaining("hello")).thenReturn(List.of(doc));

        List<MessageDocument> result = searchService.searchMessages("hello");

        assertEquals(1, result.size());
        assertEquals("msg-1", result.get(0).getId());
    }

    @Test
    void getMessagesByChannel_filtersCorrectly() {
        MessageDocument doc = new MessageDocument("msg-1", "channel-1", "user-1", "hello", new Date());
        when(messageRepository.findByChannelId("channel-1")).thenReturn(List.of(doc));

        List<MessageDocument> result = searchService.getMessagesByChannel("channel-1");

        assertEquals(1, result.size());
        assertEquals("channel-1", result.get(0).getChannelId());
    }

    @Test
    void getMessagesBySender_filtersCorrectly() {
        MessageDocument doc = new MessageDocument("msg-1", "channel-1", "user-1", "hello", new Date());
        when(messageRepository.findBySenderId("user-1")).thenReturn(List.of(doc));

        List<MessageDocument> result = searchService.getMessagesBySender("user-1");

        assertEquals(1, result.size());
        assertEquals("user-1", result.get(0).getSenderId());
    }
}
