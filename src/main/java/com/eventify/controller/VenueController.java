package com.eventify.controller;

import com.eventify.model.Venue;
import com.eventify.service.VenueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
public class VenueController {
    private final VenueService venueService;

    public VenueController (VenueService venueService) {
        this.venueService = venueService;
    }

    @PostMapping
    public ResponseEntity<Venue> create (@RequestBody Venue venue) {
        Venue createdVenue = venueService.create(venue);
        return ResponseEntity.status(HttpStatus.CREATED).body(venue);
    }

    @GetMapping
    public ResponseEntity<List<Venue>> findAll () {
        List<Venue> venues = venueService.findAll();
        return ResponseEntity.ok(venues);
    }
}
