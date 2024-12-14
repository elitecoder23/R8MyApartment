package mk12.service;

import mk12.model.ApartmentOwner;
import mk12.dto.ApartmentOwnerDTO;
import mk12.dto.LoginDTO;
import mk12.dto.LoginResponse;
import mk12.exception.AuthenticationException;
import mk12.repository.IApartmentOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class for managing apartment owners.
 * Provides methods for registering and logging in apartment owners.
 */
@Service
public class ApartmentOwnerService {
    @Autowired
    private IApartmentOwnerRepository ownerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    /**
     * Registers a new apartment owner.
     *
     * @param ownerDTO the data transfer object containing the owner's details
     * @return a LoginResponse containing the registered owner's details and a JWT token
     * @throws AuthenticationException if the email is already registered
     */
    public LoginResponse registerOwner(ApartmentOwnerDTO ownerDTO) {
        if (ownerRepository.findByEmail(ownerDTO.getEmail()).isPresent()) {
            throw new AuthenticationException("Email already registered");
        }

        ApartmentOwner owner = new ApartmentOwner();
        owner.setEmail(ownerDTO.getEmail());
        owner.setPassword(passwordEncoder.encode(ownerDTO.getPassword()));
        owner.setName(ownerDTO.getName());

        owner = ownerRepository.save(owner);
        String token = jwtService.generateToken(owner.getEmail());

        return LoginResponse.from(owner, token);
    }

    /**
     * Logs in an apartment owner.
     *
     * @param loginDTO the data transfer object containing the login details
     * @return a LoginResponse containing the logged-in owner's details and a JWT token
     * @throws AuthenticationException if the credentials are invalid
     */
    public LoginResponse loginOwner(LoginDTO loginDTO) {
        ApartmentOwner owner = ownerRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), owner.getPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }

        String token = jwtService.generateToken(owner.getEmail());
        return LoginResponse.from(owner, token);
    }
}