package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple Hello World Controller to display the string returned
 *
 * @author Vivek Bengre
 */

@RestController
class WelcomeController {

    @GetMapping("/")
    public String welcome() {
        return "Do you ever feel like a plastic bag\n" +
                "Drifting through the wind, wanting to start again?\n" +
                "Do you ever feel, feel so paper thin\n" +
                "Like a house of cards, one blow from cavin' in?\n" +
                "Do you ever feel already buried deep?\n" +
                "Six feet under screams, but no one seems to hear a thing\n" +
                "Do you know that there's still a chance for you?\n" +
                "'Cause there's a spark in you\n" +
                "You just gotta ignite the light\n" +
                "And let it shine\n" +
                "Just own the night\n" +
                "Like the Fourth of July\n" +
                "'Cause baby, you're a firework\n" +
                "Come on, show 'em what you're worth\n" +
                "Make 'em go, \"Oh, oh, oh\"\n" +
                "As you shoot across the sky\n" +
                "Baby, you're a firework\n" +
                "Come on, let your colors burst\n" +
                "Make 'em go, \"Oh, oh, oh\"\n" +
                "You're gonna leave 'em all in awe, awe, awe\n" +
                "You don't have to feel like a waste of space\n" +
                "You're original, cannot be replaced\n" +
                "If you only knew what the future holds\n" +
                "After a hurricane comes a rainbow\n" +
                "Maybe a reason why all the doors are closed\n" +
                "So you could open one that leads you to the perfect road\n" +
                "Like a lightning bolt, your heart will glow\n" +
                "And when it's time, you'll know\n" +
                "You just gotta ignite the light\n" +
                "And let it shine\n" +
                "Just own the night\n" +
                "Like the Fourth of July\n" +
                "'Cause baby, you're a firework\n" +
                "Come on, show 'em what you're worth\n" +
                "Make 'em go, \"Oh, oh, oh\"\n" +
                "As you shoot across the sky\n" +
                "Baby, you're a firework\n" +
                "Come on, let your colors burst\n" +
                "Make 'em go, \"Oh, oh, oh\"\n" +
                "You're gonna leave 'em all in awe, awe, awe\n" +
                "Boom, boom, boom\n" +
                "Even brighter than the moon, moon, moon\n" +
                "It's always been inside of you, you, you\n" +
                "And now it's time to let it through\n" +
                "'Cause baby, you're a firework\n" +
                "Come on, show 'em what you're worth\n" +
                "Make 'em go, \"Oh, oh, oh\"\n" +
                "As you shoot across the sky\n" +
                "Baby, you're a firework\n" +
                "Come on, let your colors burst\n" +
                "Make 'em go, \"Oh, oh, oh\"\n" +
                "You're gonna leave 'em all in awe, awe, awe\n" +
                "Boom, boom, boom\n" +
                "Even brighter than the moon, moon, moon\n" +
                "Boom, boom, boom\n" +
                "Even brighter than the moon, moon, moon";
    }

    @GetMapping("/api/random")
    public String Random()
    {
        return "What happens in Vegas stays in Vegas - Dcreadon";
    }

}


