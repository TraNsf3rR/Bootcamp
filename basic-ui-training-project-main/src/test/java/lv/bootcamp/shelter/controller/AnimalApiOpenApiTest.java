package lv.bootcamp.shelter.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AnimalApiOpenApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void swaggerUiLoads() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("swagger-ui")));
    }

    @Test
    void apiSpecificationDocumentsAnimalEndpointsAndResponses() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.info.title").value("Shelter Animal API"))
                .andExpect(jsonPath("$.info.version").value("1.0.0"))
                .andExpect(jsonPath("$.info.description").value(
                        "REST API for listing, creating, and adopting shelter animals."))
                .andExpect(jsonPath("$.paths['/api/animals'].get.tags[0]").value("Animals"))
                .andExpect(jsonPath("$.paths['/api/animals'].get.responses['200']").exists())
                .andExpect(jsonPath("$.paths['/api/animals'].post.responses['201']").exists())
                .andExpect(jsonPath("$.paths['/api/animals'].post.responses['400']").exists())
                .andExpect(jsonPath("$.paths['/api/animals'].post.requestBody.content['application/json'].schema").exists())
                .andExpect(jsonPath("$.paths['/api/animals/{id}'].get.responses['200']").exists())
                .andExpect(jsonPath("$.paths['/api/animals/{id}'].get.responses['404']").exists())
                .andExpect(jsonPath("$.paths['/api/animals/adopted'].get.responses['200']").exists())
                .andExpect(jsonPath("$.paths['/api/animals/{id}/adopt'].post.responses['200']").exists())
                .andExpect(jsonPath("$.paths['/api/animals/{id}/adopt'].post.responses['404']").exists())
                .andExpect(jsonPath("$.paths['/api/animals/{id}/adopt'].post.responses['409']").exists());
    }
}
