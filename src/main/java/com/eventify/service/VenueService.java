package com.eventify.service;


import com.eventify.model.Venue;
import com.eventify.repository.VenueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueService {
    private final VenueRepository venueRepository;

    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public Venue create(Venue venue) {
        validateVenue(venue);
        return venueRepository.save(venue);
    }

    public List<Venue> findAll() {
        return venueRepository.findAll();
    }

    private void validateVenue(Venue venue) {
        if (venue.getName() == null || venue.getName().isBlank()) {
            throw new IllegalArgumentException("Venue name cannot be empty");
        }

        if (venue.getAddress() == null || venue.getAddress().isBlank()) {
            throw new IllegalArgumentException("Venue address cannot be empty");
        }

        if (venue.getCapacity() == null || venue.getCapacity() <= 0) {
            throw new IllegalArgumentException("Venue capacity must be greater than zero");
        }
    }
}