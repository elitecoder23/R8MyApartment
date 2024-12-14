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
import java.util.List;

// Indicates that this class is a REST controller, capable of handling HTTP requests
@RestController
// Maps web requests to "/delete/people"
@RequestMapping("/delete/people")
public class DeletePeople {

    // Handles POST requests to this endpoint
    @PostMapping
    // Method to delete a person based on the name provided in the request body
    // @RequestBody annotation maps the HTTP request body to a Person object
    public List<People.Person> postCreatePerson(@RequestBody People.Person newPerson) {
        // Iterate through the list of all people
        for (int i = 0; i < People.allPeople.size(); i++) {
            // Check if the name of the current person matches the name in the request
            if (People.allPeople.get(i).getName().equals(newPerson.getName())) {
                // If a match is found, remove that person from the list
                People.allPeople.remove(i);
            }
        }
        // Return the updated list of all people
        return People.allPeople;
    }
}
