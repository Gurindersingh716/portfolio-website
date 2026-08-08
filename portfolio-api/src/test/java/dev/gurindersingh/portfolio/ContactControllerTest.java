package dev.gurindersingh.portfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gurindersingh.portfolio.common.RateLimiter;
import dev.gurindersingh.portfolio.contact.ContactRepository;
import dev.gurindersingh.portfolio.contact.ContactRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ContactControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired RateLimiter rateLimiter;
    @Autowired ContactRepository repository;

    @BeforeEach
    void reset() {
        rateLimiter.clear();
        repository.deleteAll();
    }

    private String body(ContactRequest r) throws Exception {
        return json.writeValueAsString(r);
    }

    @Test
    void acceptsValidMessage() throws Exception {
        var request = new ContactRequest("Asha Rao", "asha@example.com", "Hello",
                "I saw your portfolio and would like to talk about a backend role.", null);

        mvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(request)))
                .andExpect(status().isAccepted());

        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void rejectsInvalidEmail() throws Exception {
        var request = new ContactRequest("Asha Rao", "not-an-email", null,
                "This message is definitely long enough to pass validation.", null);

        mvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").exists());

        assertThat(repository.count()).isZero();
    }

    @Test
    void rejectsShortMessage() throws Exception {
        var request = new ContactRequest("Asha Rao", "asha@example.com", null, "hi", null);

        mvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.message").exists());
    }

    @Test
    void silentlyDropsHoneypotSubmissions() throws Exception {
        var bot = new ContactRequest("Bot", "bot@example.com", "Cheap SEO",
                "Buy links from us, we have many links available for you.", "http://spam.example");

        mvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(bot)))
                .andExpect(status().isAccepted());

        assertThat(repository.count()).isZero();
    }

    @Test
    void rateLimitsAfterThreeSubmissions() throws Exception {
        var request = new ContactRequest("Asha Rao", "asha@example.com", null,
                "A perfectly reasonable enquiry about backend engineering work.", null);

        for (int i = 0; i < 3; i++) {
            mvc.perform(post("/api/contact")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body(request)))
                    .andExpect(status().isAccepted());
        }

        mvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(request)))
                .andExpect(status().isTooManyRequests());

        assertThat(repository.count()).isEqualTo(3);
    }
}
