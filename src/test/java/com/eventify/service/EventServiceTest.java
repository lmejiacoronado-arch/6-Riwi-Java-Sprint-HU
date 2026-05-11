package com.eventify.service;

import com.eventify.model.Event;
import com.eventify.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

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
                1L,
                "Java Conference",
                LocalDate.of(2026, 6, 10),
                "Technology event"
        );

        when(eventRepository.save(event)).thenReturn(event);

        Event result = eventService.create(event);

        assertNotNull(result);
        assertEquals("Java Conference", result.getName());
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void shouldThrowExceptionWhenEventNameIsEmpty() {
        Event event = new Event(
                1L,
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
                1L,
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
    void shouldReturnAllEvents() {
        List<Event> events = List.of(
                new Event(1L, "Java Conference", LocalDate.of(2026, 6, 10), "Technology event"),
                new Event(2L, "Music Festival", LocalDate.of(2026, 7, 15), "Music event")
        );

        when(eventRepository.findAll()).thenReturn(events);

        List<Event> result = eventService.findAll();

        assertEquals(2, result.size());
        verify(eventRepository, times(1)).findAll();
    }
}
