package com.eventify.service;

import com.eventify.exception.ResourceNotFoundException;
import com.eventify.model.Venue;
import com.eventify.repository.VenueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
        Venue venue = new Venue(
                null,
                "Main Auditorium",
                "123 Main Street",
                500
        );

        Venue savedVenue = new Venue(
                1L,
                "Main Auditorium",
                "123 Main Street",
                500
        );

        when(venueRepository.save(venue)).thenReturn(savedVenue);

        Venue result = venueService.create(venue);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Main Auditorium", result.getName());

        verify(venueRepository, times(1)).save(venue);
    }

    @Test
    void shouldThrowExceptionWhenVenueNameIsEmpty() {
        Venue venue = new Venue(
                null,
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
                null,
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
                null,
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
    void shouldReturnPaginatedVenues() {
        List<Venue> venues = List.of(
                new Venue(1L, "Main Auditorium", "123 Main Street", 500),
                new Venue(2L, "Convention Center", "456 Business Avenue", 1200)
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<Venue> venuePage = new PageImpl<>(venues, pageable, venues.size());

        when(venueRepository.findAll(pageable)).thenReturn(venuePage);

        Page<Venue> result = venueService.findAll(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());

        verify(venueRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldFindVenueByIdWhenVenueExists() {
        Venue venue = new Venue(
                1L,
                "Main Auditorium",
                "123 Main Street",
                500
        );

        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));

        Venue result = venueService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Main Auditorium", result.getName());

        verify(venueRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenVenueDoesNotExistById() {
        when(venueRepository.findById(9999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> venueService.findById(9999L)
        );

        assertEquals("Venue not found with id: 9999", exception.getMessage());

        verify(venueRepository, times(1)).findById(9999L);
    }

    @Test
    void shouldUpdateVenueWhenVenueExists() {
        Venue existingVenue = new Venue(
                1L,
                "Old Venue",
                "Old Address",
                100
        );

        Venue venueToUpdate = new Venue(
                null,
                "Updated Venue",
                "Updated Address",
                800
        );

        Venue updatedVenue = new Venue(
                1L,
                "Updated Venue",
                "Updated Address",
                800
        );

        when(venueRepository.findById(1L)).thenReturn(Optional.of(existingVenue));
        when(venueRepository.save(existingVenue)).thenReturn(updatedVenue);

        Venue result = venueService.update(1L, venueToUpdate);

        assertEquals(1L, result.getId());
        assertEquals("Updated Venue", result.getName());
        assertEquals("Updated Address", result.getAddress());
        assertEquals(800, result.getCapacity());

        verify(venueRepository, times(1)).findById(1L);
        verify(venueRepository, times(1)).save(existingVenue);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingVenue() {
        Venue venueToUpdate = new Venue(
                null,
                "Updated Venue",
                "Updated Address",
                800
        );

        when(venueRepository.findById(9999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> venueService.update(9999L, venueToUpdate)
        );

        assertEquals("Venue not found with id: 9999", exception.getMessage());

        verify(venueRepository, times(1)).findById(9999L);
        verify(venueRepository, never()).save(any(Venue.class));
    }

    @Test
    void shouldDeleteVenueWhenVenueExists() {
        Venue venue = new Venue(
                1L,
                "Main Auditorium",
                "123 Main Street",
                500
        );

        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));

        venueService.delete(1L);

        verify(venueRepository, times(1)).findById(1L);
        verify(venueRepository, times(1)).delete(venue);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingVenue() {
        when(venueRepository.findById(9999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> venueService.delete(9999L)
        );

        assertEquals("Venue not found with id: 9999", exception.getMessage());

        verify(venueRepository, times(1)).findById(9999L);
        verify(venueRepository, never()).delete(any(Venue.class));
    }
}