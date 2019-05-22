package com.adi.graphql.controllers;

import com.adi.graphql.components.GraphQLRequestBody;
import com.atlassian.braid.BraidGraphQL;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
public class GraphQLController {

    @Autowired
    private BraidGraphQL graphQL;

    @PostMapping("/")
    //request body "query":"{\n  users{\n    id\n    book{\n      id\n    }\n  }\n}"}
    public Object graphQLPost(@RequestBody String body) throws IOException {
        GraphQLRequestBody request = new ObjectMapper().readValue(body, GraphQLRequestBody.class);
        return graphQL.execute(ExecutionInput.newExecutionInput().query(request.getQuery()).build()).join();
    }


}
