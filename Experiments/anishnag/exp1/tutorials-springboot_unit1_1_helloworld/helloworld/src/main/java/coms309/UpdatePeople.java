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
import coms309.People;


import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

// Indicates that this class is a REST controller, capable of handling HTTP requests
@RestController
// Maps web requests to "/update/people"
@RequestMapping("/insert/people")
public class UpdatePeople {
    // Commented out: Instance of People class. Currently not used.
    //private People people = new People();

    // Handles POST requests to this endpoint
    @PostMapping
    // Method to create a new person and add it to the list
    // @RequestBody annotation maps the HTTP request body to a Person object
    public List<People.Person> postCreatePerson(@RequestBody People.Person newPerson) {
        // Adds the new person to the static list in the People class
        People.allPeople.add(newPerson);
        // Returns the updated list of all people
        return People.allPeople;
    }
}

