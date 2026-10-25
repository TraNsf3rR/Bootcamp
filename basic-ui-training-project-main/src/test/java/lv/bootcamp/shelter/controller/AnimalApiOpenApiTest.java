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
                .andExpect(jsonPath("$.paths['/api/animals/{id}'].get.parameters[0].description")
                        .value("Unique numeric identifier of the animal."))
                .andExpect(jsonPath("$.paths['/api/animals/adopted'].get.responses['200']").exists())
                .andExpect(jsonPath("$.paths['/api/animals/{id}/adopt'].post.responses['200']").exists())
                .andExpect(jsonPath("$.paths['/api/animals/{id}/adopt'].post.responses['404']").exists())
                .andExpect(jsonPath("$.paths['/api/animals/{id}/adopt'].post.responses['409']").exists())
                .andExpect(jsonPath("$.paths['/api/animals/{id}/adopt'].post.parameters[0].description")
                        .value("Unique numeric identifier of the animal to adopt."))
                .andExpect(jsonPath("$.components.schemas.AnimalCreateRequest.properties.name.description")
                        .value("Name given to the animal; must not be blank."))
                .andExpect(jsonPath("$.components.schemas.AnimalCreateRequest.properties.type.description")
                        .value("Animal category."))
                .andExpect(jsonPath("$.components.schemas.AnimalCreateRequest.properties.breed.description")
                        .value("Animal breed or variety, if known."))
                .andExpect(jsonPath("$.components.schemas.AnimalCreateRequest.properties.age.description")
                        .value("Animal age in whole years, if known; must be zero or greater."))
                .andExpect(jsonPath("$.components.schemas.AnimalCreateRequest.properties.description.description")
                        .value("Optional short description of the animal's personality or needs."))
                .andExpect(jsonPath("$.components.schemas.AnimalCreateRequest.properties.imageUrl.description")
                        .value("Optional image filename or URL; blank values use a type-specific fallback."))
                .andExpect(jsonPath("$.components.schemas.AnimalResponse.properties.id.description")
                        .value("Unique identifier assigned to the animal."))
                .andExpect(jsonPath("$.components.schemas.AnimalResponse.properties.name.description")
                        .value("Name given to the animal."))
                .andExpect(jsonPath("$.components.schemas.AnimalResponse.properties.type.description")
                        .value("Animal category."))
                .andExpect(jsonPath("$.components.schemas.AnimalResponse.properties.breed.description")
                        .value("Animal breed or variety, if known."))
                .andExpect(jsonPath("$.components.schemas.AnimalResponse.properties.age.description")
                        .value("Animal age in whole years, if known."))
                .andExpect(jsonPath("$.components.schemas.AnimalResponse.properties.description.description")
                        .value("Description of the animal's personality or needs, if provided."))
                .andExpect(jsonPath("$.components.schemas.AnimalResponse.properties.status.description")
                        .value("Current adoption status: AVAILABLE or ADOPTED."))
                .andExpect(jsonPath("$.components.schemas.AnimalResponse.properties.imageUrl.description")
                        .value("Resolved image URL, including a type-specific fallback when no image was supplied."))
                .andExpect(jsonPath("$.components.schemas.AnimalResponse.properties.adoptionNote.description")
                        .value("Adoption date note when the animal has adoption details; null otherwise."));
    }
}
