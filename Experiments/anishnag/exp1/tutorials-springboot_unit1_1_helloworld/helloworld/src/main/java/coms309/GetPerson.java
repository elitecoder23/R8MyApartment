package coms309;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Indicates that this class is a REST controller, capable of handling HTTP requests
@RestController
// Maps web requests to "/get/person"
@RequestMapping("/get/person")
public class GetPerson {

    // Handles POST requests to this endpoint
    @PostMapping
    // Method to retrieve a person based on the name provided in the request body
    // @RequestBody annotation maps the HTTP request body to a Person object
    public People.Person postGetPerson(@RequestBody People.Person newPerson) {
        // Iterate through the list of all people
        for (int i = 0; i < People.allPeople.size(); i++) {
            // Check if the name of the current person matches the name in the request
            if (People.allPeople.get(i).getName().equals(newPerson.getName())) {
                // If a match is found, return that person
                return People.allPeople.get(i);
            }
        }
        // If no match is found, return null
        return null;
    }
}





