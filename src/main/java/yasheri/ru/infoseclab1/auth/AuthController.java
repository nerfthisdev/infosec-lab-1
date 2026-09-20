package yasheri.ru.infoseclab1.auth;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import yasheri.ru.infoseclab1.auth.dto.LoginRequest;
import yasheri.ru.infoseclab1.auth.dto.LoginResponse;
import yasheri.ru.infoseclab1.auth.dto.RegisterRequest;
import yasheri.ru.infoseclab1.auth.dto.RegisterResponse;

@RestController
@RequestMapping("/auth")
class AuthController {

    private final AuthService authService;

    AuthController(AuthService authService) {
        this.authService = authService;
    }

    @RequestMapping("/login")
    @PostMapping
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

}
