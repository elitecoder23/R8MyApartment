package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.stream.Collectors;

// Indicates that this class is a REST controller, capable of handling HTTP requests
@RestController
// Maps web requests to "/list/people"
@RequestMapping("/list/people")
public class ListPeople {

    // Handles GET requests to "/list/people/names"
    @GetMapping("/names")
    public List<String> listPeopleNames() {
        // Use Java streams to map each Person object to its name
        return People.allPeople.stream()
                .map(People.Person::getName)  // Extract name from each Person object
                .collect(Collectors.toList());  // Collect names into a List
    }

    // Handles GET requests to "/list/people/count"
    @GetMapping("/count")
    public int getPeopleCount() {
        // Return the size of the allPeople list
        return People.allPeople.size();
    }
}