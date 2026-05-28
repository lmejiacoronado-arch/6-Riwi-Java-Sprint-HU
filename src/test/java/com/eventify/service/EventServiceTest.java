package com.eventify.service;

import com.eventify.exception.ResourceNotFoundException;
import com.eventify.model.Category;
import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    void shouldCreateEventWhenDataIsValid() {
        Event event = createValidEvent();

        when(eventRepository.save(event)).thenReturn(event);

        Event result = eventService.create(event);

        assertNotNull(result);
        assertEquals("Java Conference", result.getName());
        assertEquals("Main Auditorium", result.getVenue().getName());
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void shouldThrowExceptionWhenEventNameIsEmpty() {
        Event event = createValidEvent();
        event.setName("");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> eventService.create(event)
        );

        assertEquals("Event name cannot be empty", exception.getMessage());
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void shouldThrowExceptionWhenEventDateIsNull() {
        Event event = createValidEvent();
        event.setEventDate(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> eventService.create(event)
        );

        assertEquals("Event date cannot be null", exception.getMessage());
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void shouldThrowExceptionWhenEventVenueIsMissing() {
        Event event = createValidEvent();
        event.setVenue(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> eventService.create(event)
        );

        assertEquals("Event venue is required", exception.getMessage());
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void shouldReturnAllEventsPaginated() {
        List<Event> events = List.of(
                createValidEvent(),
                new Event(
                        2L,
                        "Music Festival",
                        LocalDate.of(2026, 7, 15),
                        "Music event",
                        true,
                        createVenue(),
                        Set.of(createCategory())
                )
        );

        Pageable pageable = PageRequest.of(0, 10, Sort.by("date").descending());
        Page<Event> eventPage = new PageImpl<>(events, pageable, events.size());

        when(eventRepository.findAll(pageable)).thenReturn(eventPage);

        Page<Event> result = eventService.findAll(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        verify(eventRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldFindEventByIdWhenExists() {
        Event event = createValidEvent();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        Event result = eventService.findById(1L);

        assertNotNull(result);
        assertEquals("Java Conference", result.getName());
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenEventDoesNotExist() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.findById(999L)
        );

        assertEquals("Event not found with id: 999", exception.getMessage());
        verify(eventRepository, times(1)).findById(999L);
    }

    @Test
    void shouldUpdateEventWhenExists() {
        Event existingEvent = createValidEvent();

        Event updatedData = new Event(
                null,
                "Updated Java Conference",
                LocalDate.of(2026, 8, 20),
                "Updated description",
                true,
                createVenue(),
                Set.of(createCategory())
        );

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(existingEvent)).thenReturn(existingEvent);

        Event result = eventService.update(1L, updatedData);

        assertEquals("Updated Java Conference", result.getName());
        assertEquals(LocalDate.of(2026, 8, 20), result.getEventDate());
        assertEquals("Updated description", result.getDescription());
        verify(eventRepository, times(1)).findById(1L);
        verify(eventRepository, times(1)).save(existingEvent);
    }

    @Test
    void shouldSoftDeleteEventWhenExists() {
        Event event = createValidEvent();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(event)).thenReturn(event);

        eventService.delete(1L);

        assertFalse(event.getActive());
        verify(eventRepository, times(1)).findById(1L);
        verify(eventRepository, times(1)).save(event);
        verify(eventRepository, never()).delete(any(Event.class));
    }

    private Event createValidEvent() {
        return new Event(
                1L,
                "Java Conference",
                LocalDate.of(2026, 6, 10),
                "Technology event",
                true,
                createVenue(),
                Set.of(createCategory())
        );
    }

    private Venue createVenue() {
        return new Venue(
                1L,
                "Main Auditorium",
                "123 Main Street",
                500,
                "Medellin"
        );
    }

    private Category createCategory() {
        return new Category(
                1L,
                "Conferences",
                "Technology conferences"
        );
    }
}