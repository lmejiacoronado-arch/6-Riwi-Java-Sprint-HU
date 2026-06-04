package com.eventify.service;

import com.eventify.exception.ResourceNotFoundException;
import com.eventify.model.Venue;
import com.eventify.repository.VenueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

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
        Venue venue = createValidVenue();

        when(venueRepository.save(venue)).thenReturn(venue);

        Venue result = venueService.create(venue);

        assertNotNull(result);
        assertEquals("Main Auditorium", result.getName());
        assertEquals("Medellin", result.getCity());
        verify(venueRepository, times(1)).save(venue);
    }

    @Test
    void shouldThrowExceptionWhenVenueNameIsEmpty() {
        Venue venue = createValidVenue();
        venue.setName("");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> venueService.create(venue)
        );

        assertEquals("Venue name cannot be empty", exception.getMessage());
        verify(venueRepository, never()).save(any(Venue.class));
    }

    @Test
    void shouldThrowExceptionWhenVenueAddressIsEmpty() {
        Venue venue = createValidVenue();
        venue.setAddress("");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> venueService.create(venue)
        );

        assertEquals("Venue address cannot be empty", exception.getMessage());
        verify(venueRepository, never()).save(any(Venue.class));
    }

    @Test
    void shouldThrowExceptionWhenVenueCapacityIsInvalid() {
        Venue venue = createValidVenue();
        venue.setCapacity(0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> venueService.create(venue)
        );

        assertEquals("Venue capacity must be greater than zero", exception.getMessage());
        verify(venueRepository, never()).save(any(Venue.class));
    }

    @Test
    void shouldThrowExceptionWhenVenueCityIsEmpty() {
        Venue venue = createValidVenue();
        venue.setCity("");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> venueService.create(venue)
        );

        assertEquals("Venue city cannot be empty", exception.getMessage());
        verify(venueRepository, never()).save(any(Venue.class));
    }

    @Test
    void shouldReturnAllVenuesPaginated() {
        List<Venue> venues = List.of(
                createValidVenue(),
                new Venue(
                        2L,
                        "Convention Center",
                        "456 Business Avenue",
                        1200,
                        "Bogota"
                )
        );

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Venue> venuePage = new PageImpl<>(venues, pageable, venues.size());

        when(venueRepository.findAll(pageable)).thenReturn(venuePage);

        Page<Venue> result = venueService.findAll(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        verify(venueRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldFindVenueByIdWhenExists() {
        Venue venue = createValidVenue();

        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));

        Venue result = venueService.findById(1L);

        assertNotNull(result);
        assertEquals("Main Auditorium", result.getName());
        verify(venueRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenVenueDoesNotExist() {
        when(venueRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> venueService.findById(999L)
        );

        assertEquals("Venue not found with id: 999", exception.getMessage());
        verify(venueRepository, times(1)).findById(999L);
    }

    @Test
    void shouldUpdateVenueWhenExists() {
        Venue existingVenue = createValidVenue();

        Venue updatedData = new Venue(
                null,
                "Updated Auditorium",
                "Updated Street",
                700,
                "Cali"
        );

        when(venueRepository.findById(1L)).thenReturn(Optional.of(existingVenue));
        when(venueRepository.save(existingVenue)).thenReturn(existingVenue);

        Venue result = venueService.update(1L, updatedData);

        assertEquals("Updated Auditorium", result.getName());
        assertEquals("Updated Street", result.getAddress());
        assertEquals(700, result.getCapacity());
        assertEquals("Cali", result.getCity());

        verify(venueRepository, times(1)).findById(1L);
        verify(venueRepository, times(1)).save(existingVenue);
    }

    @Test
    void shouldDeleteVenueWhenExists() {
        Venue venue = createValidVenue();

        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));

        venueService.delete(1L);

        verify(venueRepository, times(1)).findById(1L);
        verify(venueRepository, times(1)).delete(venue);
    }

    private Venue createValidVenue() {
        return new Venue(
                1L,
                "Main Auditorium",
                "123 Main Street",
                500,
                "Medellin"
        );
    }
}