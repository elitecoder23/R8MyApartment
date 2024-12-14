package coms309;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

@RestController
@RequestMapping("/people")
public class WelcomeController {

    // This will act as our database
    private static final Map<String, Person> personMap = new HashMap<>();

    // Create a new person
    @PostMapping
    public ResponseEntity<Person> createPerson(@RequestBody Person person) {
        if (personMap.containsKey(person.getFirstName())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // 409 Conflict if the person already exists
        }
        personMap.put(person.getFirstName(), person);
        return new ResponseEntity<>(person, HttpStatus.CREATED); // 201 Created
    }

    // Other endpoints...

    // List all persons
    @GetMapping
    public ResponseEntity<Collection<Person>> getAllPersons() {
        return new ResponseEntity<>(personMap.values(), HttpStatus.OK);
    }

    // Read information on a person
    @GetMapping("/{firstName}")
    public ResponseEntity<Person> getPerson(@PathVariable String firstName) {
        Person person = personMap.get(firstName);
        if (person != null) {
            return new ResponseEntity<>(person, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    // Update information on a person
    @PatchMapping("/{firstName}")
    public ResponseEntity<Person> updatePerson(@PathVariable String firstName, @RequestBody Person updatedPerson) {
        if (!personMap.containsKey(firstName)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found if the person does not exist
        }
        Person person = personMap.get(firstName);
        if (updatedPerson.getFirstName() != null) person.setFirstName(updatedPerson.getFirstName());
        if (updatedPerson.getLastName() != null) person.setLastName(updatedPerson.getLastName());
        if (updatedPerson.getAddress() != null) person.setAddress(updatedPerson.getAddress());
        if (updatedPerson.getTelephone() != null) person.setTelephone(updatedPerson.getTelephone());
        return new ResponseEntity<>(person, HttpStatus.OK); // 200 OK
    }


    // Delete a person record

    @DeleteMapping("/{firstName}")
    public ResponseEntity<Void> deletePerson(@PathVariable String firstName) {
        System.out.println("Deleting firstName: " + firstName);
        try {
            Person person = personMap.get(firstName);
            if (person != null) {
                person.setFirstName(null); // Remove only the firstName field
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
            }
        } catch (Exception e) {
            System.out.println("Error deleting person: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }
   /* @DeleteMapping("/{lastName}")
    public ResponseEntity<Void> deleteLastname(@PathVariable String lastName) {
        if (personMap.remove(lastName) != null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }
*/
    // Endpoint to trigger an exception
    @RequestMapping(method = RequestMethod.GET, path = "/oops")
    public String triggerException() {
        throw new RuntimeException("Check to see what happens when an exception is thrown");
    }

}
