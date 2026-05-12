package com.eventify.controller;

import com.eventify.model.Venue;
import com.eventify.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
@Tag(
        name = "Venues",
        description = "Operations for managing venues"
)
public class VenueController {
    private final VenueService venueService;

    public VenueController (VenueService venueService) {
        this.venueService = venueService;
    }

    @Operation(
            summary = "Create a new venue",
            description = "Creates a venue after validating the business rules"
    )
    @PostMapping
    public ResponseEntity<Venue> create (@RequestBody Venue venue) {
        Venue createdVenue = venueService.create(venue);
        return ResponseEntity.status(HttpStatus.CREATED).body(venue);
    }

    @Operation(
            summary = "Get all venues",
            description = "Returns the complete venue catalog"
    )
    @GetMapping
    public ResponseEntity<List<Venue>> findAll () {
        List<Venue> venues = venueService.findAll();
        return ResponseEntity.ok(venues);
    }

    @Operation(
            summary = "Get venue by ID",
            description = "Returns a single venue by its ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Venue> findById(@PathVariable Long id) {
        Venue venue = venueService.findById(id);
        return ResponseEntity.ok(venue);
    }

    @Operation(
            summary = "Update venue by ID",
            description = "Updates an existing venue if the ID exists"
    )
    @PutMapping("/{id}")
    public ResponseEntity<Venue> update(@PathVariable Long id, @RequestBody Venue venue) {
        Venue updatedVenue = venueService.update(id, venue);
        return ResponseEntity.ok(updatedVenue);
    }

    @Operation(
            summary = "Delete venue by ID",
            description = "Deletes an existing venue if the ID exists"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        venueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
