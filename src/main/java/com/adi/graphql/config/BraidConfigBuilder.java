package com.adi.graphql.config;

import com.atlassian.braid.Braid;
import com.atlassian.braid.BraidGraphQL;
import com.atlassian.braid.SchemaSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BraidConfigBuilder {

    @Autowired
    private List<SchemaSource> schemaSourceList;

    @Bean
    public BraidGraphQL graphQL() {
        return Braid.builder().schemaSources(schemaSourceList).build().newGraphQL();
    }

}
