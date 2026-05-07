package com.eventify.service;

import com.eventify.model.Venue;
import com.eventify.repository.VenueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VenueServiceTest {

    @Mock
    private VenueRepository venueRepository;

    @InjectMocks
    private VenueService venueService;

    @Test
    void shouldCreateVenueWhenDataIsValid() {
        Venue venue = new Venue(
                1,
                "Main Auditorium",
                "123 Main Street",
                500
        );

        when(venueRepository.save(venue)).thenReturn(venue);

        Venue result = venueService.create(venue);

        assertNotNull(result);
        assertEquals("Main Auditorium", result.getName());
        verify(venueRepository, times(1)).save(venue);
    }

    @Test
    void shouldThrowExceptionWhenVenueNameIsEmpty() {
        Venue venue = new Venue(
                1,
                "",
                "123 Main Street",
                500
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> venueService.create(venue)
        );

        assertEquals("Venue name cannot be empty", exception.getMessage());
        verify(venueRepository, never()).save(any(Venue.class));
    }

    @Test
    void shouldThrowExceptionWhenVenueAddressIsEmpty() {
        Venue venue = new Venue(
                1,
                "Main Auditorium",
                "",
                500
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> venueService.create(venue)
        );

        assertEquals("Venue address cannot be empty", exception.getMessage());
        verify(venueRepository, never()).save(any(Venue.class));
    }

    @Test
    void shouldThrowExceptionWhenVenueCapacityIsInvalid() {
        Venue venue = new Venue(
                1,
                "Main Auditorium",
                "123 Main Street",
                0
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> venueService.create(venue)
        );

        assertEquals("Venue capacity must be greater than zero", exception.getMessage());
        verify(venueRepository, never()).save(any(Venue.class));
    }

    @Test
    void shouldReturnAllVenues() {
        List<Venue> venues = List.of(
                new Venue(1, "Main Auditorium", "123 Main Street", 500),
                new Venue(2, "Convention Center", "456 Business Avenue", 1200)
        );

        when(venueRepository.findAll()).thenReturn(venues);

        List<Venue> result = venueService.findAll();

        assertEquals(2, result.size());
        verify(venueRepository, times(1)).findAll();
    }
}
