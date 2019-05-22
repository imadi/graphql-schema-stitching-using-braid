package com.adi.graphql.utils;

import com.atlassian.braid.java.util.BraidObjects;
import com.google.common.base.Charsets;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class ResourceUtils {

    private ResourceUtils() {}

    public static Reader getResourceAsReader(String schemaPath) {
        try {
            return new InputStreamReader(getResourceAsStream(schemaPath), Charsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("It's UTF-8");
        }
    }

    private static InputStream getResourceAsStream(String schemaPath) {
        return ResourceUtils.class.getClassLoader().getResourceAsStream(schemaPath);
    }

    public static Map<String, Object> loadYamlMap(Reader source) {
        return (Map) BraidObjects.cast((new Yaml()).load(source));
    }

}

