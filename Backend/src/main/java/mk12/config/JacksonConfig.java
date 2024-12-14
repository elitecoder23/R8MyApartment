package mk12.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Jackson ObjectMapper.
 * This configuration is used to register the JavaTimeModule
 * to handle Java 8 date and time API serialization and deserialization.
 */
@Configuration
public class JacksonConfig {

    /**
     * Creates and configures an {@link ObjectMapper} bean.
     * The {@link ObjectMapper} is configured to register the {@link JavaTimeModule}
     * to handle Java 8 date and time API serialization and deserialization.
     *
     * @return a configured {@link ObjectMapper} instance
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}