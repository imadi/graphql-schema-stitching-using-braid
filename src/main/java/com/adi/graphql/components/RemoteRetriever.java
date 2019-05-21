package com.adi.graphql.components;

import com.atlassian.braid.source.GraphQLRemoteRetriever;
import com.atlassian.braid.source.Query;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import graphql.ExecutionInput;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.dataloader.impl.CompletableFutureKit.failedFuture;

@Slf4j
public class RemoteRetriever<C> implements GraphQLRemoteRetriever<C> {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final String url;

    public RemoteRetriever(String url) {
        this.url = url;
    }

    @Override
    public CompletableFuture<Map<String, Object>> queryGraphQL(Query query, C c) {
        Gson gson = new Gson();
        OkHttpClient client = new OkHttpClient();
        ExecutionInput executionInput = query.asExecutionInput();
        Map<String, Object> bodyMap = ImmutableMap.of("query", executionInput.getQuery(), "variables", executionInput.getVariables());

        String json = gson.toJson(bodyMap);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            HashMap<String, Object> jsonResult = gson.fromJson(response.body().string(), HashMap.class);
            return CompletableFuture.completedFuture(jsonResult);
        } catch (IOException error) {
            log.error("Exception while getting data from graphql ", error);
            return failedFuture(error);
        }
    }
}
