package ru.practicum.ewmservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.statsclient.EndpointHitsClient;

@Configuration
public class StatsClientConfig {
    @Value("${stats-service.url}")
    private String serverUrl;

    @Bean
    EndpointHitsClient getStatsClient() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        return new EndpointHitsClient(serverUrl, builder);
    }
}
