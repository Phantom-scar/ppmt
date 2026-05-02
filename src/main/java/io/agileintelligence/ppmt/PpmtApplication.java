package io.agileintelligence.ppmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableMongoAuditing
public class PpmtApplication {

    public static void main(String[] args) {
        SpringApplication.run(PpmtApplication.class, args);
    }
}
