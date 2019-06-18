package com.adi.graphql.config;

import com.adi.graphql.components.RemoteIntrospection;
import com.adi.graphql.components.RemoteRetriever;
import com.atlassian.braid.Link;
import com.atlassian.braid.SchemaNamespace;
import com.atlassian.braid.SchemaSource;
import com.atlassian.braid.java.util.BraidMaps;
import com.atlassian.braid.java.util.BraidObjects;
import com.atlassian.braid.source.QueryExecutorSchemaSource;
import com.atlassian.braid.source.yaml.YamlRemoteSchemaSourceBuilder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.adi.graphql.utils.ResourceUtils.getResourceAsReader;
import static com.adi.graphql.utils.ResourceUtils.loadYamlMap;

@Configuration
@Data
public class SchemaSourceConfig {

    @Autowired
    private WebClient.Builder webClientConfigBuilder;

    private static final String USERS_SCHEMA_URL = "http://localhost:8080/graphql"; //This one can be read from config

    private static final String BOOKS_SCHEMA_URL = "http://localhost:8081/graphql";

    private List<Map<String, Object>> schemaSources;

    private List<Link> links;

    @PostConstruct
    public void init() {
        Map<String, Object> configMap = loadYamlMap(getResourceAsReader("braidConfig.yml"));
        links = BraidMaps.get(configMap, "schemaSources")
                .map(BraidObjects::<List<Map<String, Object>>>cast)
                .map(sources -> sources.stream()
                        .map(YamlRemoteSchemaSourceBuilder::buildLinks).collect(Collectors.toList())
                ).orElse(Collections.emptyList())
                .stream().flatMap(Collection::stream).collect(Collectors.toList());

    }

    @Bean
    public SchemaSource userSchemaSource() {
        return querySchemaSourceBuilder("users", USERS_SCHEMA_URL, links);
    }

    @Bean
    public SchemaSource bookSchemaSource() {
        return querySchemaSourceBuilder("books", BOOKS_SCHEMA_URL, Collections.emptyList());
    }

    private SchemaSource querySchemaSourceBuilder(String namespace, String schemaUrl, List<Link> links) {
        return QueryExecutorSchemaSource.builder().namespace(SchemaNamespace.of(namespace))
                .schemaProvider(() -> new RemoteIntrospection(schemaUrl, webClientConfigBuilder).get())
                .remoteRetriever(new RemoteRetriever<>(schemaUrl, webClientConfigBuilder))
                .links(links)
                .build();
    }

}

