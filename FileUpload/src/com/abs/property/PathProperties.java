package com.abs.property;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

public final class PathProperties {
    private static final PathProperties instance = new PathProperties();
    private static final Properties props = new Properties();
    static {
        InputStream in = null;
        try {
            in = PathProperties.class.getClassLoader().getResourceAsStream("path.properties");
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public static PathProperties getInstance() {
        return instance;
    }

    public String getProperty(String proName) {
        return props.getProperty(proName);
    }

    public String getOppositePath() {
        return props.getProperty("opposite_path");
    }
}
