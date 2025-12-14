package com.prpo.chat.search.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.prpo.chat.search.repository")
public class ElasticsearchConfig {
}
