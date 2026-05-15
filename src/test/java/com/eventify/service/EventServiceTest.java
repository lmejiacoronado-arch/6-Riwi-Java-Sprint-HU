package com.eventify.service;

import com.eventify.exception.ResourceNotFoundException;
import com.eventify.model.Event;
import com.eventify.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
        Event event = new Event(
                null,
                "Java Conference",
                LocalDate.of(2026, 6, 10),
                "Technology event"
        );

        Event savedEvent = new Event(
                1L,
                "Java Conference",
                LocalDate.of(2026, 6, 10),
                "Technology event"
        );

        when(eventRepository.save(event)).thenReturn(savedEvent);

        Event result = eventService.create(event);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Java Conference", result.getName());

        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void shouldThrowExceptionWhenEventNameIsEmpty() {
        Event event = new Event(
                null,
                "",
                LocalDate.of(2026, 6, 10),
                "Technology event"
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> eventService.create(event)
        );

        assertEquals("Event name cannot be empty", exception.getMessage());

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void shouldThrowExceptionWhenEventDateIsNull() {
        Event event = new Event(
                null,
                "Java Conference",
                null,
                "Technology event"
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> eventService.create(event)
        );

        assertEquals("Event date cannot be null", exception.getMessage());

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void shouldReturnPaginatedEvents() {
        List<Event> events = List.of(
                new Event(1L, "Java Conference", LocalDate.of(2026, 6, 10), "Technology event"),
                new Event(2L, "Music Festival", LocalDate.of(2026, 7, 15), "Music event")
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> eventPage = new PageImpl<>(events, pageable, events.size());

        when(eventRepository.findAll(pageable)).thenReturn(eventPage);

        Page<Event> result = eventService.findAll(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());

        verify(eventRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldFindEventByIdWhenEventExists() {
        Event event = new Event(
                1L,
                "Java Conference",
                LocalDate.of(2026, 6, 10),
                "Technology event"
        );

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        Event result = eventService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Java Conference", result.getName());

        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenEventDoesNotExistById() {
        when(eventRepository.findById(9999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.findById(9999L)
        );

        assertEquals("Event not found with id: 9999", exception.getMessage());

        verify(eventRepository, times(1)).findById(9999L);
    }

    @Test
    void shouldUpdateEventWhenEventExists() {
        Event existingEvent = new Event(
                1L,
                "Old Event",
                LocalDate.of(2026, 5, 10),
                "Old description"
        );

        Event eventToUpdate = new Event(
                null,
                "Updated Event",
                LocalDate.of(2026, 6, 20),
                "Updated description"
        );

        Event updatedEvent = new Event(
                1L,
                "Updated Event",
                LocalDate.of(2026, 6, 20),
                "Updated description"
        );

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(existingEvent)).thenReturn(updatedEvent);

        Event result = eventService.update(1L, eventToUpdate);

        assertEquals(1L, result.getId());
        assertEquals("Updated Event", result.getName());
        assertEquals(LocalDate.of(2026, 6, 20), result.getEventDate());
        assertEquals("Updated description", result.getDescription());

        verify(eventRepository, times(1)).findById(1L);
        verify(eventRepository, times(1)).save(existingEvent);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingEvent() {
        Event eventToUpdate = new Event(
                null,
                "Updated Event",
                LocalDate.of(2026, 6, 20),
                "Updated description"
        );

        when(eventRepository.findById(9999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.update(9999L, eventToUpdate)
        );

        assertEquals("Event not found with id: 9999", exception.getMessage());

        verify(eventRepository, times(1)).findById(9999L);
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void shouldDeleteEventWhenEventExists() {
        Event event = new Event(
                1L,
                "Java Conference",
                LocalDate.of(2026, 6, 10),
                "Technology event"
        );

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        eventService.delete(1L);

        verify(eventRepository, times(1)).findById(1L);
        verify(eventRepository, times(1)).delete(event);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingEvent() {
        when(eventRepository.findById(9999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.delete(9999L)
        );

        assertEquals("Event not found with id: 9999", exception.getMessage());

        verify(eventRepository, times(1)).findById(9999L);
        verify(eventRepository, never()).delete(any(Event.class));
    }
}