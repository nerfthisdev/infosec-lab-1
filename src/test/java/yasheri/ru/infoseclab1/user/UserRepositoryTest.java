package yasheri.ru.infoseclab1.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import yasheri.ru.infoseclab1.TestcontainersConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class UserRepositoryTest {

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
