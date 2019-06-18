package com.adi.graphql.components;

import graphql.introspection.IntrospectionResultToSchema;
import graphql.language.Document;
import graphql.schema.idl.SchemaPrinter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RemoteIntrospection {

    private final WebClient webClient;

    public RemoteIntrospection(String url, WebClient.Builder webClientConfigBuilder) {
        this.webClient = webClientConfigBuilder.baseUrl(url).build();
    }

    public Reader get() {
        try {
            return webClient.post()
                    .header(HttpHeaders.CONTENT_TYPE, org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
                    .syncBody(Collections.singletonMap("query", introspectionQuery()))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .flatMap(introspectionResult -> {
                        Document schema = new IntrospectionResultToSchema().createSchemaDefinition((Map<String, Object>) introspectionResult.get("data"));
                        String printedSchema = new SchemaPrinter().print(schema);
                        return Mono.just(new StringReader(printedSchema));
                    }).toFuture().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new StringReader("");
        } catch (ExecutionException e) {
            e.printStackTrace();
            return new StringReader("");
        }
    }

    private String introspectionQuery() {
        return "query IntrospectionQuery {\n" +
                "__schema {\n" +
                "queryType { name }\n" +
                "mutationType { name }\n" +
                "subscriptionType { name }\n" +
                "types {\n" +
                "...FullType\n" +
                "}\n" +
                "directives {\n" +
                "name\n" +
                "description\n" +
                "locations\n" +
                "args {\n" +
                "...InputValue\n" +
                "}\n" +
                "}\n" +
                "}\n" +
                "}\n" +
                "fragment FullType on __Type {\n" +
                "kind\n" +
                "name\n" +
                "description\n" +
                "fields(includeDeprecated: true) {\n" +
                "name\n" +
                "description\n" +
                "args {\n" +
                "...InputValue\n" +
                "}\n" +
                "type {\n" +
                "...TypeRef\n" +
                "}\n" +
                "isDeprecated\n" +
                "deprecationReason\n" +
                "}\n" +
                "inputFields {\n" +
                "...InputValue\n" +
                "}\n" +
                "interfaces {\n" +
                "...TypeRef\n" +
                "}\n" +
                "enumValues(includeDeprecated: true) {\n" +
                "name\n" +
                "description\n" +
                "isDeprecated\n" +
                "deprecationReason\n" +
                "}\n" +
                "possibleTypes {\n" +
                "...TypeRef\n" +
                "}\n" +
                "}\n" +
                "fragment InputValue on __InputValue {\n" +
                "name\n" +
                "description\n" +
                "type { ...TypeRef }\n" +
                "defaultValue\n" +
                "}\n" +
                "fragment TypeRef on __Type {\n" +
                "kind\n" +
                "name\n" +
                "ofType {\n" +
                "kind\n" +
                "name\n" +
                "ofType {\n" +
                "kind\n" +
                "name\n" +
                "ofType {\n" +
                "kind\n" +
                "name\n" +
                "ofType {\n" +
                "kind\n" +
                "name\n" +
                "ofType {\n" +
                "kind\n" +
                "name\n" +
                "ofType {\n" +
                "kind\n" +
                "name\n" +
                "ofType {\n" +
                "kind\n" +
                "name\n" +
                "}\n" +
                "}\n" +
                "}\n" +
                "}\n" +
                "}\n" +
                "}\n" +
                "}\n" +
                "}\n";
    }

}
