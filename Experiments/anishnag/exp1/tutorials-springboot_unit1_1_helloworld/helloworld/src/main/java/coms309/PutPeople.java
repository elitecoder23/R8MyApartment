package coms309;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;

// Indicates that this class is a REST controller
@RestController
// Sets the base URL path for all endpoints in this controller
@RequestMapping("/put/people")
public class PutPeople {

    // Handles PUT requests to "/put/people/{name}"
    @PutMapping("/{name}")
    public ResponseEntity<People.Person> updatePerson(
            // Extracts the 'name' variable from the URL path
            @PathVariable String name,
            // Maps the HTTP request body to a People.Person object
            @RequestBody People.Person updatedPerson) {

        // Iterate through the list of all people
        for (int i = 0; i < People.allPeople.size(); i++) {
            People.Person person = People.allPeople.get(i);
            // Check if the current person's name matches the name in the URL
            if (person.getName().equals(name)) {
                // Update the person's details with the new information
                People.allPeople.set(i, new People.Person(
                        updatedPerson.getName(),
                        updatedPerson.getAge(),
                        updatedPerson.getLocation()
                ));
                // Return the updated person with a 200 OK status
                return ResponseEntity.ok(People.allPeople.get(i));
            }
        }
        // If no matching person is found, return a 404 Not Found status
        return ResponseEntity.notFound().build();
    }
}