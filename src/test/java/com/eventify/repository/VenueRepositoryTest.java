package com.eventify.repository;

import com.eventify.model.Venue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class VenueRepositoryTest {

    @Autowired
    private VenueRepository venueRepository;

    @Test
    void shouldSaveVenue() {
        Venue venue = new Venue(
                null,
                "Main Auditorium",
                "123 Main Street",
                500
        );

        Venue savedVenue = venueRepository.save(venue);

        assertNotNull(savedVenue.getId());
        assertEquals("Main Auditorium", savedVenue.getName());
        assertEquals("123 Main Street", savedVenue.getAddress());
        assertEquals(500, savedVenue.getCapacity());
    }

    @Test
    void shouldFindVenueById() {
        Venue venue = new Venue(
                null,
                "Convention Center",
                "456 Business Avenue",
                1200
        );

        Venue savedVenue = venueRepository.save(venue);

        Optional<Venue> result = venueRepository.findById(savedVenue.getId());

        assertTrue(result.isPresent());
        assertEquals("Convention Center", result.get().getName());
    }

    @Test
    void shouldFindVenuesByNameContainingIgnoreCase() {
        Venue venue = new Venue(
                null,
                "Convention Center",
                "456 Business Avenue",
                1200
        );

        venueRepository.save(venue);

        List<Venue> results = venueRepository.findByNameContainingIgnoreCase("convention");

        assertFalse(results.isEmpty());
        assertEquals("Convention Center", results.get(0).getName());
    }

    @Test
    void shouldReturnPaginatedVenues() {
        venueRepository.save(new Venue(
                null,
                "Main Auditorium",
                "123 Main Street",
                500
        ));

        venueRepository.save(new Venue(
                null,
                "Convention Center",
                "456 Business Avenue",
                1200
        ));

        venueRepository.save(new Venue(
                null,
                "Open Air Theater",
                "789 Park Road",
                800
        ));

        Pageable pageable = PageRequest.of(0, 2);

        Page<Venue> result = venueRepository.findAll(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertTrue(result.isFirst());
    }

    @Test
    void shouldDeleteVenue() {
        Venue venue = new Venue(
                null,
                "Temporary Venue",
                "999 Test Street",
                100
        );

        Venue savedVenue = venueRepository.save(venue);

        venueRepository.deleteById(savedVenue.getId());

        Optional<Venue> result = venueRepository.findById(savedVenue.getId());

        assertTrue(result.isEmpty());
    }
}
