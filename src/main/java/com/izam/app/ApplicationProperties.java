package com.izam.app;

import java.io.IOException;
import java.util.Properties;
import java.io.FileInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationProperties {
    private static final Logger log = LoggerFactory.getLogger(ApplicationProperties.class);

    private final Properties props;

    public ApplicationProperties(final String path) {
        props = new Properties();

        try {
            props.load(new FileInputStream(path));
        } catch(IOException ex) {
            log.error(ex.getMessage());
        }
    }

    public int getServerPort() {
        return Integer.parseInt(props.getProperty("server.port"));
    }

}
