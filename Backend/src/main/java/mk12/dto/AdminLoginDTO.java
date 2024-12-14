package mk12.dto;

import lombok.Data;

@Data
public class AdminLoginDTO {
    private String email;
    private String password;
    private String name;
}