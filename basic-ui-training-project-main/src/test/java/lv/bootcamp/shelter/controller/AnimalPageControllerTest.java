package lv.bootcamp.shelter.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import lv.bootcamp.shelter.config.SecurityLoginInfoLogger;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.containsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest(properties = {
        "spring.security.user.password=test-password",
        "spring.security.user.name=test-user"
})
@AutoConfigureMockMvc
class AnimalPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityProperties securityProperties;

    @Test
    void homePageRendersNavigation() throws Exception {
        mockMvc.perform(get("/").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().string(containsString("href=\"/css/site.css\"")))
                .andExpect(content().string(containsString("href=\"/animals\"")))
                .andExpect(content().string(containsString("href=\"/animals/new\"")));
    }

    @Test
    void animalListRendersLiveAnimalsWithConsistentImages() throws Exception {
        mockMvc.perform(get("/animals").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(view().name("animals"))
                .andExpect(content().string(containsString("Luna")))
                .andExpect(content().string(containsString("Diora")))
                .andExpect(content().string(containsString("href=\"/css/site.css\"")))
                .andExpect(content().string(containsString("src=\"/images/animals/Luna.jpeg\"")))
                .andExpect(content().string(containsString("width=\"300\"")))
                .andExpect(content().string(containsString("height=\"200\"")))
                .andExpect(content().string(containsString("href=\"/swagger-ui/index.html\"")))
                .andExpect(content().string(containsString("class=\"cta\">Swagger UI</a>")))
                .andExpect(content().string(containsString("href=\"/animals/new\"")));
    }

    @Test
    void newAnimalPageRendersBoundForm() throws Exception {
        mockMvc.perform(get("/animals/new").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(view().name("animals-new"))
                .andExpect(content().string(containsString("href=\"/css/site.css\"")))
                .andExpect(content().string(containsString("action=\"/animals\"")))
                .andExpect(content().string(containsString("name=\"name\"")))
                .andExpect(content().string(containsString("name=\"type\"")))
                .andExpect(content().string(containsString("href=\"/animals\"")));
    }

    @Test
    void sharedStylesheetIsServed() throws Exception {
        mockMvc.perform(get("/css/site.css"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/css"))
                .andExpect(content().string(containsString("max-width: 760px")))
                .andExpect(content().string(containsString(".cta")))
                .andExpect(content().string(containsString("li img")));
    }

    @Test
    void submittingFormCreatesAnimalAndRedirectsToList() throws Exception {
        mockMvc.perform(post("/animals")
                        .with(user("user"))
                        .with(csrf())
                        .param("name", "Milo Stage One")
                        .param("type", "OTHER")
                        .param("breed", "Rabbit")
                        .param("age", "3")
                        .param("description", "Friendly and curious.")
                        .param("imageUrl", "milo.jpg"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/animals"));

        mockMvc.perform(get("/animals").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Milo Stage One")))
                .andExpect(content().string(containsString("/images/animals/milo.jpg")));
    }

    @Test
    void blankNameShowsAccessibleInlineValidationError() throws Exception {
        mockMvc.perform(post("/animals")
                        .with(user("user"))
                        .with(csrf())
                        .param("name", " ")
                        .param("type", "CAT")
                        .param("age", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("animals-new"))
                .andExpect(content().string(containsString("Name is required")))
                .andExpect(content().string(containsString("id=\"name-error\"")))
                .andExpect(content().string(containsString("aria-invalid=\"true\"")))
                .andExpect(content().string(containsString("aria-describedby=\"name-error\"")))
                .andExpect(content().string(containsString("role=\"alert\"")));
    }

    @Test
    void typeAndAgeValidationErrorsAreShownInline() throws Exception {
        mockMvc.perform(post("/animals")
                        .with(user("user"))
                        .with(csrf())
                        .param("name", "Milo Validation")
                        .param("type", "")
                        .param("age", "-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("animals-new"))
                .andExpect(content().string(containsString("Type is required")))
                .andExpect(content().string(containsString("Age cannot be negative")))
                .andExpect(content().string(containsString("aria-describedby=\"type-error\"")))
                .andExpect(content().string(containsString("aria-describedby=\"age-error\"")));
    }

    @Test
    void unauthenticatedUiRequestRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/animals"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", endsWith("/login")));
    }

    @Test
    void customLoginPageAcceptsTheConfiguredUser() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().string(containsString("Sign in to the Shelter")))
                .andExpect(content().string(containsString("name=\"username\"")))
                .andExpect(content().string(containsString("name=\"password\"")))
                .andExpect(content().string(containsString("name=\"_csrf\"")));

        MvcResult loginResult = mockMvc.perform(post("/login")
                        .param("username", "test-user")
                        .param("password", "test-password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
        assertNotNull(session);
        mockMvc.perform(get("/animals").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("animals"));
    }

    @Test
    void loginInfoLoggerUsesTheConfiguredUsername(CapturedOutput output) {
        new SecurityLoginInfoLogger(securityProperties).run(new DefaultApplicationArguments());

        assertThat(output).contains("Shelter UI sign-in username: test-user");
    }
}
