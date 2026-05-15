package com.eventify.controller;

import com.eventify.model.Venue;
import com.eventify.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/venues")
@Tag(name = "Venues", description = "Operations for managing venues")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @Operation(
            summary = "Create a new venue",
            description = "Creates a new venue after validating the business rules"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Venue created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid venue data")
    })
    @PostMapping
    public ResponseEntity<Venue> create(@RequestBody Venue venue) {
        Venue createdVenue = venueService.create(venue);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVenue);
    }

    @Operation(
            summary = "Get paginated venues",
            description = "Returns a paginated and sorted list of venues. Example: /api/venues?page=0&size=10&sort=name,asc"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venues retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<Page<Venue>> findAll(
            @ParameterObject
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<Venue> venues = venueService.findAll(pageable);
        return ResponseEntity.ok(venues);
    }

    @Operation(
            summary = "Get venue by ID",
            description = "Returns a single venue by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venue found successfully"),
            @ApiResponse(responseCode = "404", description = "Venue not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Venue> findById(
            @Parameter(description = "Venue ID", example = "1")
            @PathVariable Long id
    ) {
        Venue venue = venueService.findById(id);
        return ResponseEntity.ok(venue);
    }

    @Operation(
            summary = "Update venue by ID",
            description = "Updates an existing venue if the provided ID exists"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venue updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid venue data"),
            @ApiResponse(responseCode = "404", description = "Venue not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Venue> update(
            @Parameter(description = "Venue ID", example = "1")
            @PathVariable Long id,
            @RequestBody Venue venue
    ) {
        Venue updatedVenue = venueService.update(id, venue);
        return ResponseEntity.ok(updatedVenue);
    }

    @Operation(
            summary = "Delete venue by ID",
            description = "Deletes an existing venue if the provided ID exists"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Venue deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Venue not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Venue ID", example = "1")
            @PathVariable Long id
    ) {
        venueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}