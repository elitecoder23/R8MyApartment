package mk12.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) for user login.
 * Contains the email and password of the user.
 */
@Data
public class LoginDTO {
    private String email;
    private String password;
}