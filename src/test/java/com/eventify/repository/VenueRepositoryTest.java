package com.eventify.repository;

import com.eventify.model.Venue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.*;
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
        Venue venue = createVenue();

        Venue savedVenue = venueRepository.save(venue);

        assertNotNull(savedVenue.getId());
        assertEquals("Main Auditorium", savedVenue.getName());
        assertEquals("123 Main Street", savedVenue.getAddress());
        assertEquals(500, savedVenue.getCapacity());
        assertEquals("Medellin", savedVenue.getCity());
    }

    @Test
    void shouldFindVenueById() {
        Venue savedVenue = venueRepository.save(createVenue());

        Optional<Venue> result = venueRepository.findById(savedVenue.getId());

        assertTrue(result.isPresent());
        assertEquals("Main Auditorium", result.get().getName());
    }

    @Test
    void shouldFindVenuesByNameContainingIgnoreCase() {
        venueRepository.save(createVenue());

        List<Venue> results = venueRepository.findByNameContainingIgnoreCase("main");

        assertFalse(results.isEmpty());
        assertEquals("Main Auditorium", results.get(0).getName());
    }

    @Test
    void shouldReturnPaginatedVenues() {
        venueRepository.save(new Venue(null, "Main Auditorium", "123 Main Street", 500, "Medellin"));
        venueRepository.save(new Venue(null, "Convention Center", "456 Business Avenue", 1200, "Bogota"));
        venueRepository.save(new Venue(null, "Open Air Theater", "789 Park Road", 800, "Cali"));

        Pageable pageable = PageRequest.of(0, 2);

        Page<Venue> result = venueRepository.findAll(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertTrue(result.isFirst());
    }

    @Test
    void shouldDeleteVenue() {
        Venue savedVenue = venueRepository.save(createVenue());

        venueRepository.deleteById(savedVenue.getId());

        Optional<Venue> result = venueRepository.findById(savedVenue.getId());

        assertTrue(result.isEmpty());
    }

    private Venue createVenue() {
        return new Venue(
                null,
                "Main Auditorium",
                "123 Main Street",
                500,
                "Medellin"
        );
    }
}