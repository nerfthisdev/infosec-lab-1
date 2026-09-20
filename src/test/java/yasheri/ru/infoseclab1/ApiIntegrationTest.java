package yasheri.ru.infoseclab1;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import yasheri.ru.infoseclab1.data.DataRepository;
import yasheri.ru.infoseclab1.user.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void cleanDatabase() {
        dataRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void registerHashesPasswordAndLoginReturnsValidJwt() throws Exception {
        register("alice", "correct-password")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"));

        var user = userRepository.findByUsername("alice").orElseThrow();
        assertNotEquals("correct-password", user.getPasswordHash());
        assertTrue(passwordEncoder.matches("correct-password", user.getPasswordHash()));

        String token = login("alice", "correct-password");
        assertEquals(3, token.split("\\.").length);
        assertEquals("alice", jwtDecoder.decode(token).getSubject());
    }

    @Test
    void loginRejectsWrongPassword() throws Exception {
        register("alice", "correct-password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"alice","password":"wrong-password"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void parameterizedRepositoryQueryPreventsSqlInjectionLogin() throws Exception {
        register("alice", "correct-password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"' OR '1'='1","password":"correct-password"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registrationValidatesInputAndRejectsDuplicateUsername() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"?","password":"short"}
                                """))
                .andExpect(status().isBadRequest());

        register("alice", "correct-password")
                .andExpect(status().isOk());
        register("alice", "another-password")
                .andExpect(status().isConflict());
    }

    @Test
    void dataEndpointsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/data"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/data")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"secret\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void dataEndpointRejectsInvalidJwt() throws Exception {
        mockMvc.perform(get("/api/data")
                        .header("Authorization", "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void dataEndpointValidatesText() throws Exception {
        register("alice", "correct-password");
        String token = login("alice", "correct-password");

        mockMvc.perform(post("/api/data")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"   \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void authenticatedUserCanCreateAndReadEscapedData() throws Exception {
        register("alice", "correct-password");
        String token = login("alice", "correct-password");

        mockMvc.perform(post("/api/data")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text":"<script>alert(1)</script>"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text")
                        .value("&lt;script&gt;alert(1)&lt;/script&gt;"));

        mockMvc.perform(get("/api/data")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].text")
                        .value("&lt;script&gt;alert(1)&lt;/script&gt;"));
    }

    private org.springframework.test.web.servlet.ResultActions register(
            String username,
            String password
    ) throws Exception {
        return mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"username":"%s","password":"%s"}
                        """.formatted(username, password)));
    }

    private String login(String username, String password) throws Exception {
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"%s"}
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(response, "$.token");
    }
}
