package com.eventify.service;


import com.eventify.exception.ResourceNotFoundException;
import com.eventify.model.Venue;
import com.eventify.repository.VenueRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<Venue> findAll(Pageable pageable) {
        return venueRepository.findAll(pageable);
    }

    public Venue findById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Venue not found with id: " + id));
    }

    public Venue update (Long id, Venue venue) {
        Venue existingVenue = findById(id);

        validateVenue(venue);

        existingVenue.setName(venue.getName());
        existingVenue.setAddress(venue.getAddress());
        existingVenue.setCapacity(venue.getCapacity());
        existingVenue.setCity(venue.getCity());
        return venueRepository.save(existingVenue);
    }

    public void delete (Long id) {
        Venue existingVenue = findById(id);
        venueRepository.delete(existingVenue);
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

        if (venue.getCity() == null || venue.getCity().isBlank()) {
            throw new IllegalArgumentException("Venue city cannot be empty");
        }
    }
}