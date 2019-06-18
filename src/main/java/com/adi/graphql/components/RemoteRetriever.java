package com.adi.graphql.components;

import com.atlassian.braid.source.GraphQLRemoteRetriever;
import com.atlassian.braid.source.Query;
import com.google.common.collect.ImmutableMap;
import graphql.ExecutionInput;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RemoteRetriever<C> implements GraphQLRemoteRetriever<C> {

    private final WebClient webClient;

    public RemoteRetriever(String url, WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(url).build();
    }

    @Override
    public CompletableFuture<Map<String, Object>> queryGraphQL(Query query, C c) {
        ExecutionInput executionInput = query.asExecutionInput();
        return webClient.post()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .syncBody(ImmutableMap.of("query", executionInput.getQuery(), "variables", executionInput.getVariables()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .toFuture();
    }
}
