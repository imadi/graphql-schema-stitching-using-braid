package com.adi.graphql.components;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import graphql.introspection.IntrospectionResultToSchema;
import graphql.language.Document;
import graphql.schema.idl.SchemaPrinter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RemoteIntrospection {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final String url;

    public RemoteIntrospection(String url) {
        this.url = url;
    }

    public Reader get() {
        Gson gson = new Gson();
        OkHttpClient client = new OkHttpClient();

        Map<String, String> bodyMap = ImmutableMap.of("query", introspectionQuery());

        String json = gson.toJson(bodyMap);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            Map<String, Object> introspectionResult = gson.fromJson(response.body().string(), HashMap.class);
            Document schema = new IntrospectionResultToSchema().createSchemaDefinition((Map<String, Object>) introspectionResult.get("data"));
            String printedSchema = new SchemaPrinter().print(schema);
            return new StringReader(printedSchema);
        } catch (IOException ex) {
            System.out.println(ex);
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
