package com.ponteshop.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Configuration
public class DatabaseConfig implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSourceProperties) {
            DataSourceProperties properties = (DataSourceProperties) bean;
            String url = properties.getUrl();
            
            // 1. Check if the configured URL itself is a non-standard postgres URL
            if (url != null && (url.startsWith("postgres://") || url.startsWith("postgresql://"))) {
                logger.info("Detected non-standard Postgres URL in spring.datasource.url, converting to JDBC...");
                applyDatabaseUrl(properties, url);
            } 
            // 2. Otherwise, check if DATABASE_URL env var is present and can be used as a fallback/override
            else {
                String envDatabaseUrl = System.getenv("DATABASE_URL");
                if (envDatabaseUrl != null && (envDatabaseUrl.startsWith("postgres://") || envDatabaseUrl.startsWith("postgresql://"))) {
                    logger.info("Detected DATABASE_URL environment variable starting with postgres scheme. Overriding DataSource URL...");
                    applyDatabaseUrl(properties, envDatabaseUrl);
                }
            }
        }
        return bean;
    }

    private void applyDatabaseUrl(DataSourceProperties properties, String rawUrl) {
        try {
            // URL format: postgres://username:password@host:port/database
            URI uri = new URI(rawUrl);
            String host = uri.getHost();
            int port = uri.getPort();
            if (port == -1) {
                port = 5432;
            }
            String path = uri.getPath(); // starts with '/'
            
            String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
            properties.setUrl(jdbcUrl);
            
            String userInfo = uri.getUserInfo();
            if (userInfo != null && userInfo.contains(":")) {
                String[] parts = userInfo.split(":", 2);
                String username = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
                String password = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
                
                properties.setUsername(username);
                properties.setPassword(password);
                logger.info("Extracted database username and password from connection string.");
            }
            
            logger.info("Successfully configured JDBC URL: {}", jdbcUrl);
        } catch (URISyntaxException e) {
            logger.error("Failed to parse database URL: {}", rawUrl, e);
        }
    }
}
