package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/search/people")
public class SearchPeople {

    // Endpoint to search people by age range
    @GetMapping("/age")
    public List<People.Person> searchByAgeRange(
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge) {

        // Stream the allPeople list and filter based on the provided age range
        return People.allPeople.stream()
                .filter(person -> (minAge == null || person.getAge() >= minAge) &&
                        (maxAge == null || person.getAge() <= maxAge))
                .collect(Collectors.toList());
    }

    // Endpoint to search people by location
    @GetMapping("/location")
    public List<People.Person> searchByLocation(@RequestParam String location) {
        // Stream the allPeople list and filter based on the provided location
        return People.allPeople.stream()
                .filter(person -> person.getLocation().equalsIgnoreCase(location))
                .collect(Collectors.toList());
    }
}
