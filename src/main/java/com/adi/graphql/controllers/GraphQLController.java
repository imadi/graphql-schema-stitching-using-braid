package com.adi.graphql.controllers;

import com.adi.graphql.components.RemoteIntrospection;
import com.adi.graphql.components.RemoteRetriever;
import com.atlassian.braid.Braid;
import com.atlassian.braid.BraidGraphQL;
import com.atlassian.braid.Link;
import com.atlassian.braid.LinkArgument;
import com.atlassian.braid.SchemaNamespace;
import com.atlassian.braid.source.QueryExecutorSchemaSource;
import com.google.gson.Gson;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.Reader;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;

@RestController
@Slf4j
public class GraphQLController {

    private final SchemaNamespace USERS = SchemaNamespace.of("users");
    private final String USERS_SCHEMA_URL = "http://localhost:8080/graphql";

    private final SchemaNamespace BOOKS = SchemaNamespace.of("books");
    private final String BOOKS_SCHEMA_URL = "http://localhost:8081/graphql";


    @GetMapping(path = "/graphql", produces = "application/json")
    public @ResponseBody
    String graphql() {
        String query = "{ users { book { name } name } }";
        Gson gson = new Gson();
        Supplier<Reader> usersSchemaProvider = () -> new RemoteIntrospection(USERS_SCHEMA_URL).get();
        Supplier<Reader> booksSchemaProvider = () -> new RemoteIntrospection(BOOKS_SCHEMA_URL).get();

        Braid braid = Braid
                .builder()
                .schemaSource(
                        QueryExecutorSchemaSource
                                .builder()
                                .namespace(USERS)
                                .schemaProvider(usersSchemaProvider)
                                .remoteRetriever(new RemoteRetriever<>(USERS_SCHEMA_URL))
                                .links(singletonList(Link.newComplexLink()
                                        .sourceNamespace(USERS)
                                        .sourceType("User")
                                        .newFieldName("book")
                                        .targetNamespace(BOOKS)
                                        .targetType("Book")
                                        .topLevelQueryField("bookById")
                                        .linkArgument(LinkArgument.newLinkArgument()
                                                .argumentSource(LinkArgument.ArgumentSource.OBJECT_FIELD)
                                                .sourceName("bookId")
                                                .queryArgumentName("id")
                                                .build())
                                        .build()))
                                .build())
                .schemaSource(
                        QueryExecutorSchemaSource
                                .builder()
                                .namespace(BOOKS)
                                .schemaProvider(booksSchemaProvider)
                                .remoteRetriever(new RemoteRetriever<>(BOOKS_SCHEMA_URL))
                                .build())
                .build();

        BraidGraphQL graphql = braid.newGraphQL();

        ExecutionResult result = graphql.execute(ExecutionInput.newExecutionInput().query(query).build())
                .join();

        return gson.toJson(result.toSpecification());
    }

}
