package lv.bootcamp.shelter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lv.bootcamp.shelter.model.AnimalStatus;
import lv.bootcamp.shelter.model.AnimalType;

/**
 * Response body for a single animal returned by the API.
 * {@code adoptionNote} is populated when the animal has adoption details.
 */
public record AnimalResponse(
        @Schema(description = "Unique identifier assigned to the animal.")
        Long id,
        @Schema(description = "Name given to the animal.")
        String name,
        @Schema(description = "Animal category.")
        AnimalType type,
        @Schema(description = "Animal breed or variety, if known.")
        String breed,
        @Schema(description = "Animal age in whole years, if known.")
        Integer age,
        @Schema(description = "Description of the animal's personality or needs, if provided.")
        String description,
        @Schema(description = "Current adoption status: AVAILABLE or ADOPTED.")
        AnimalStatus status,
        @Schema(description = "Resolved image URL, including a type-specific fallback when no image was supplied.")
        String imageUrl,
        @Schema(description = "Adoption date note when the animal has adoption details; null otherwise.")
        String adoptionNote
) {}
