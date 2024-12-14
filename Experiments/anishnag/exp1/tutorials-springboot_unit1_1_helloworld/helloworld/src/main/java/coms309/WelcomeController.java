package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.PathVariable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
import java.util.*;
import org.springframework.web.bind.annotation.RequestBody;



//In postman, request to http://localhost:8080/api/welcome



@RestController
@RequestMapping("/api/welcome")
public class WelcomeController {



    //public static List<Person>people = new ArrayList<>();



    @GetMapping
        public WelcomeResponse getWelcomeMessage() {
            return new WelcomeResponse(
                    "Welcome to our service!",
                    UUID.randomUUID().toString(),
                    LocalDateTime.now()
            );
        }

        private static class WelcomeResponse {
            private String message;
            private String uniqueId;
            private LocalDateTime timestamp;

            public WelcomeResponse(String message, String uniqueId, LocalDateTime timestamp) {
                this.message = message;
                this.uniqueId = uniqueId;
                this.timestamp = timestamp;
            }

            public String getMessage() {
                return message;
            }

            public String getUniqueId() {
                return uniqueId;
            }

            public LocalDateTime getTimestamp() {
                return timestamp;
            }
        }
    }





