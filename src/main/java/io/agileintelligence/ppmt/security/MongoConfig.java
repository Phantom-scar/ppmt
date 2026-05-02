package io.agileintelligence.ppmt.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "io.agileintelligence.ppmt.repository")
public class MongoConfig {
    
    // Spring Boot 4.0.6 auto-configures MongoTemplate and MongoDB connection
    // No additional configuration needed - Spring Data MongoDB handles it automatically
    
}
