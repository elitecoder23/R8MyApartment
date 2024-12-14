package mk12;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


/**
 * The main application class for the WebSocket server.
 *
 * This class uses the *`@ComponentScan`* annotation to specify the base package
 * for component scanning. It scans the application packages for Spring components,
 * such as WebSocket endpoints and controllers.
 */
@SpringBootApplication
// Scan the application packages for Spring components
public class WebSocketSpringbootApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebSocketSpringbootApplication.class, args);
    }
}