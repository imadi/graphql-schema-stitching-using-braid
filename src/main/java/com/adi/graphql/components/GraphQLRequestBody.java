package com.adi.graphql.components;

import lombok.Data;

import java.util.Map;

@Data
public class GraphQLRequestBody {

    private String query;
    private String operationName;
    private Map<String, Object> variables;
}
