package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
class WelcomeController {

    @GetMapping("/billOfRights")
    public String welcome() {
        return "First Amendment [Religion, Speech, Press, Assembly, Petition (1791)] (see explanation)\n" +
                "Second Amendment [Right to Bear Arms (1791)] (see explanation)\n" +
                "Third Amendment [Quartering of Troops (1791)] (see explanation)\n" +
                "Fourth Amendment [Search and Seizure (1791)] (see explanation)\n" +
                "Fifth Amendment [Grand Jury, Double Jeopardy, Self-Incrimination, Due Process (1791)] (see explanation)\n" +
                "Sixth Amendment [Criminal Prosecutions - Jury Trial, Right to Confront and to Counsel (1791)] (see explanation)\n" +
                "Seventh Amendment [Common Law Suits - Jury Trial (1791)] (see explanation)\n" +
                "Eighth Amendment [Excess Bail or Fines, Cruel and Unusual Punishment (1791)] (see explanation)\n" +
                "Ninth Amendment [Non-Enumerated Rights (1791)] (see explanation)\n" +
                "Tenth Amendment [Rights Reserved to States or People (1791)] (see explanation)";
    }

    @GetMapping("/{name}")
    public String welcome(@PathVariable String name) {
        return "Hello and welcome to COMS 309: " + name;
    }
}

