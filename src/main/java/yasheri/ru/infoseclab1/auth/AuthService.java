package yasheri.ru.infoseclab1.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import yasheri.ru.infoseclab1.auth.dto.LoginRequest;
import yasheri.ru.infoseclab1.auth.dto.LoginResponse;
import yasheri.ru.infoseclab1.auth.dto.RegisterRequest;
import yasheri.ru.infoseclab1.auth.dto.RegisterResponse;
import yasheri.ru.infoseclab1.security.JwtService;
import yasheri.ru.infoseclab1.user.User;
import yasheri.ru.infoseclab1.user.UserRepository;

@Service
class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository repo;

    AuthService(PasswordEncoder passwordEncoder, JwtService jwtService, UserRepository repo) {
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.repo = repo;
    }

    public LoginResponse login(LoginRequest req) {
        User user = repo.findByUsername(req.username())
                .orElseThrow(this::invalidCredentials);
        boolean passwordMathes = passwordEncoder.matches(
                req.password(),
                user.getPasswordHash()
        );

        if (!passwordMathes) {
            throw invalidCredentials();
        }

        String token = jwtService.createToken(user.getUsername());

        return new LoginResponse(token);
    }

    public RegisterResponse register(RegisterRequest req) {
        if (repo.findByUsername(req.username()).isPresent()) {
            throw userAlreadyExists();
        }

        String passwordHash =
                passwordEncoder.encode(req.password());
        User user = repo.save(new User(req.username(), passwordHash));

        return new RegisterResponse(user.getUsername());
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid username or password"
        );
    }

    private ResponseStatusException userAlreadyExists() {
        return new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Username already exists");
    }
}
