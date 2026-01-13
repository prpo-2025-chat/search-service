package com.prpo.chat.search.client;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.prpo.chat.search.dto.ServerDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ServerClient {
    private final RestTemplate restTemplate;

    @Value("${server-service.base-url}")
    private String baseUrl;

    public List<String> getChannelIdsForUser(String userId) {
        if (userId == null || userId.isBlank()) {
            return Collections.emptyList();
        }

        try {
            String url = baseUrl + "memberships/" + userId + "/servers";

            List<ServerDto> servers = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ServerDto>>() {
                    }).getBody();

            if (servers == null) {
                return Collections.emptyList();
            }

            return servers.stream()
                    .map(ServerDto::getId)
                    .collect(Collectors.toList());
        } catch (RestClientException e) {
            System.err.println("Failed to fetch channels for user " + userId + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
