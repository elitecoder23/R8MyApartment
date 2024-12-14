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

// This class represents a collection of people
public class People {
    // Inner static class representing an individual person
    public static class Person {
        // Private fields to store person's details
        private String name = "";
        private int age = 0;
        private String location = "";

        // Constructor to initialize a Person object
        public Person(String name, int age, String location) {
            this.name = name;
            this.age = age;
            this.location = location;
        }

        // Getter method for name
        public String getName() {
            return name;
        }

        // Getter method for age
        public int getAge() {
            return age;
        }

        // Getter method for location
        public String getLocation() {
            return location;
        }
    }

    // Static ArrayList to store all Person objects
    public static List<Person> allPeople = new ArrayList<>();
}

//[
//        {
//        "name": "Declan",
//        "age": 23,
//        "location": "Saudi Arabia"
//        },
//        {
//        "name": "Anish Nag",
//        "age": 26,
//        "location": "Kansas"
//        },
//        {
//        "name": "Vini",
//        "age": 32,
//        "location": "Iowa"
//        }
//        ]
