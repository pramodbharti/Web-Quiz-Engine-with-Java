package engine.presentation;

import engine.business.AppUser;
import engine.business.RegistrationRequest;
import engine.persisence.AppUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import javax.validation.Valid;
import java.util.Optional;

@RestController
public class SecurityController {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public SecurityController(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/api/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest request) {
        if (!isValidEmail(request.getEmail()) || !isValidPassword(request.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid email or password format.");  // 400 Bad Request
        }
        Optional<AppUser> existingUser = appUserRepository.findAppUserByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Already exist");
        }
        var user = new AppUser();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        appUserRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 5 && !password.isBlank();
    }
}
