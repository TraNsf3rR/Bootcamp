package lv.bootcamp.shelter.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class AnimalPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void homePageRendersNavigation() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().string(containsString("href=\"/animals\"")))
                .andExpect(content().string(containsString("href=\"/animals/new\"")));
    }

    @Test
    void animalListRendersLiveAnimalsWithConsistentImages() throws Exception {
        mockMvc.perform(get("/animals"))
                .andExpect(status().isOk())
                .andExpect(view().name("animals"))
                .andExpect(content().string(containsString("Luna")))
                .andExpect(content().string(containsString("Diora")))
                .andExpect(content().string(containsString("max-width: 760px")))
                .andExpect(content().string(containsString("src=\"/images/animals/Luna.jpeg\"")))
                .andExpect(content().string(containsString("width=\"300\"")))
                .andExpect(content().string(containsString("height=\"200\"")))
                .andExpect(content().string(containsString("href=\"/animals/new\"")));
    }

    @Test
    void newAnimalPageRendersBoundForm() throws Exception {
        mockMvc.perform(get("/animals/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("animals-new"))
                .andExpect(content().string(containsString("action=\"/animals\"")))
                .andExpect(content().string(containsString("name=\"name\"")))
                .andExpect(content().string(containsString("name=\"type\"")))
                .andExpect(content().string(containsString("href=\"/animals\"")));
    }

    @Test
    void submittingFormCreatesAnimalAndRedirectsToList() throws Exception {
        mockMvc.perform(post("/animals")
                        .param("name", "Milo Stage One")
                        .param("type", "OTHER")
                        .param("breed", "Rabbit")
                        .param("age", "3")
                        .param("description", "Friendly and curious.")
                        .param("imageUrl", "milo.jpg"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/animals"));

        mockMvc.perform(get("/animals"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Milo Stage One")))
                .andExpect(content().string(containsString("/images/animals/milo.jpg")));
    }
}
