package mk12.dto;

import lombok.Data;
import mk12.model.ApartmentOwner;

/**
 * Data Transfer Object (DTO) for login response.
 * Contains the token, name, and email of the logged-in user.
 */
@Data
public class LoginResponse {
    private String token;
    private String name;
    private String email;

    /**
     * Creates a LoginResponse from an ApartmentOwner and a JWT token.
     *
     * @param owner the apartment owner
     * @param jwt the JWT token
     * @return the login response
     */
    public static LoginResponse from(ApartmentOwner owner, String jwt) {
        LoginResponse response = new LoginResponse();
        response.setToken(jwt);
        response.setName(owner.getName());
        response.setEmail(owner.getEmail());
        return response;
    }
}