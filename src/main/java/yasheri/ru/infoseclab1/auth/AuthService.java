package yasheri.ru.infoseclab1.auth;

import org.springframework.stereotype.Service;
import yasheri.ru.infoseclab1.auth.dto.LoginRequest;
import yasheri.ru.infoseclab1.auth.dto.LoginResponse;
import yasheri.ru.infoseclab1.user.UserRepository;

@Service
class AuthService {

    private final UserRepository repo;

    AuthService(UserRepository repo) {
        this.repo = repo;
    }

    public LoginResponse login(LoginRequest req) {
        var user = repo.findByUsername(req.username());
        // check hash and get jwt
        return new LoginResponse("sometoken");

    }
}
