package lv.bootcamp.shelter.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lv.bootcamp.shelter.dto.AnimalCreateRequest;
import lv.bootcamp.shelter.dto.AnimalResponse;
import lv.bootcamp.shelter.service.AnimalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for shelter animal endpoints.
 * Returns JSON — does not render HTML pages.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/animals")
@Tag(name = "Animals", description = "Shelter animal management endpoints")
public class AnimalApiController {

    private final AnimalService animalService;

    @GetMapping
    @Operation(
            summary = "List all animals",
            description = "Returns all animals, including available and adopted animals, sorted by ID."
    )
    @ApiResponse(
            responseCode = "200",
            description = "The animal list was returned.",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = AnimalResponse.class))
            )
    )
    public List<AnimalResponse> findAll() {
        return animalService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an animal by ID", description = "Returns the animal with the requested ID.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The animal was found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AnimalResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "No animal exists with the requested ID.")
    })
    public ResponseEntity<AnimalResponse> findById(
            @Parameter(description = "Unique numeric identifier of the animal.", example = "1")
            @PathVariable Long id
    ) {
        return animalService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Lists adopted animals.
     */
    @GetMapping("/adopted")
    @Operation(
            summary = "List adopted animals",
            description = "Returns adopted animals sorted by ID."
    )
    @ApiResponse(
            responseCode = "200",
            description = "The adopted animal list was returned.",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = AnimalResponse.class))
            )
    )
    public List<AnimalResponse> findAdopted() {
        return animalService.findAdopted();
    }

    /**
     * Creates a new animal.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create an animal",
            description = "Creates an animal with AVAILABLE status. A type-specific fallback image is used when imageUrl is blank."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "The animal was created.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AnimalResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "The request failed validation.")
    })
    public AnimalResponse create(@RequestBody @Valid AnimalCreateRequest request) {
        return animalService.create(request);
    }

    /**
     * Adopts an animal
     */
    @PostMapping("/{id}/adopt")
    @Operation(
            summary = "Adopt an animal",
            description = "Marks the animal as adopted and records the current adoption date."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The animal was adopted.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AnimalResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "No animal exists with the requested ID."),
            @ApiResponse(
                    responseCode = "409",
                    description = "The animal is already adopted.",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
            )
    })
    public ResponseEntity<AnimalResponse> adopt(
            @Parameter(description = "Unique numeric identifier of the animal to adopt.", example = "1")
            @PathVariable Long id
    ) {
        return animalService.adopt(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleAlreadyAdopted(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
