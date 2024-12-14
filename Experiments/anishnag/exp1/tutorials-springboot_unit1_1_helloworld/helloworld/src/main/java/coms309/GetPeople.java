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
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

// Indicates that this class is a REST controller, capable of handling HTTP requests
@RestController
// Maps web requests to "/get/people"
@RequestMapping("/get/people")
public class GetPeople {

    // Handles GET requests to this endpoint
    @GetMapping
    // Method to retrieve all people
    public List<People.Person> getPeople() {
        // Returns the entire list of people stored in the static allPeople list
        return People.allPeople;
    }
}
