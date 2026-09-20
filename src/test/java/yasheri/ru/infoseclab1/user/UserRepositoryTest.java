package yasheri.ru.infoseclab1.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class UserRepositoryTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    private UserRepository userRepository;

    @Test
    void savesAndFindsUser() {
        var name = "bob";
        var password = "password-hash";
        userRepository.saveAndFlush(
                new User(name, password)
        );

        var result = userRepository.findByUsername(name);

        assertTrue(result.isPresent());
        assertEquals(name, result.get().getUsername());
        assertEquals(password, result.get().getPasswordHash());
    }


}