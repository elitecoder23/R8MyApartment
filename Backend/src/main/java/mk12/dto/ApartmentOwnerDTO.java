package mk12.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) for Apartment Owner.
 * Contains the email, password, and name of the apartment owner.
 */
@Data
public class ApartmentOwnerDTO {
    private String email;
    private String password;
    private String name;
}