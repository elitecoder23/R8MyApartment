package mk12.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


//Jackson will automatically bind the properties in the application.properties file to the fields in this class
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {

    /**
     * The secret key used for signing the JWT.
     * It should be at least 256 bits long and very secure.
     */
    private String secret = "your-secret-key-should-be-at-least-256-bits-long-and-very-secure";

    /**
     * The expiration time of the JWT in milliseconds.
     * Default is 24 hours (86400000 milliseconds).
     */
    private long expirationMs = 86400000; // 24 hours
}
