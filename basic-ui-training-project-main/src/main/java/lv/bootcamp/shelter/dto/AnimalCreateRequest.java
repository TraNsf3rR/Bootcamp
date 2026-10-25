package lv.bootcamp.shelter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lv.bootcamp.shelter.model.AnimalType;

/**
 * JSON request body for creating a new animal via the REST API.
 * Status is not included; all new animals start as AVAILABLE.
 */
public record AnimalCreateRequest(

        @Schema(description = "Name given to the animal; must not be blank.")
        @NotBlank(message = "Name is required")
        String name,

        @Schema(description = "Animal category.")
        @NotNull(message = "Type is required")
        AnimalType type,

        @Schema(description = "Animal breed or variety, if known.")
        String breed,

        @Schema(description = "Animal age in whole years, if known; must be zero or greater.")
        @Min(value = 0, message = "Age cannot be negative")
        Integer age,

        @Schema(description = "Optional short description of the animal's personality or needs.")
        String description,

        @Schema(description = "Optional image filename or URL; blank values use a type-specific fallback.")
        String imageUrl
) {}