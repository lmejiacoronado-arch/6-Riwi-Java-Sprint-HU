package com.eventify.repository;

import com.eventify.model.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    void shouldSaveEvent() {
        Event event = new Event(
                null,
                "Java Conference",
                LocalDate.of(2026, 6, 10),
                "Technology event focused on Java and Spring Boot"
        );

        Event savedEvent = eventRepository.save(event);

        assertNotNull(savedEvent.getId());
        assertEquals("Java Conference", savedEvent.getName());
        assertEquals(LocalDate.of(2026, 6, 10), savedEvent.getEventDate());
        assertEquals("Technology event focused on Java and Spring Boot", savedEvent.getDescription());
    }

    @Test
    void shouldFindEventById() {
        Event event = new Event(
                null,
                "Spring Boot Summit",
                LocalDate.of(2026, 7, 20),
                "Backend conference"
        );

        Event savedEvent = eventRepository.save(event);

        Optional<Event> result = eventRepository.findById(savedEvent.getId());

        assertTrue(result.isPresent());
        assertEquals("Spring Boot Summit", result.get().getName());
    }

    @Test
    void shouldFindEventsByNameContainingIgnoreCase() {
        Event event = new Event(
                null,
                "Spring Boot Summit",
                LocalDate.of(2026, 7, 20),
                "Backend conference"
        );

        eventRepository.save(event);

        List<Event> results = eventRepository.findByNameContainingIgnoreCase("spring");

        assertFalse(results.isEmpty());
        assertEquals("Spring Boot Summit", results.get(0).getName());
    }

    @Test
    void shouldReturnPaginatedEvents() {
        eventRepository.save(new Event(
                null,
                "Java Conference",
                LocalDate.of(2026, 6, 10),
                "Technology event"
        ));

        eventRepository.save(new Event(
                null,
                "Music Festival",
                LocalDate.of(2026, 7, 15),
                "Music event"
        ));

        eventRepository.save(new Event(
                null,
                "Spring Boot Summit",
                LocalDate.of(2026, 8, 20),
                "Backend event"
        ));

        Pageable pageable = PageRequest.of(0, 2);

        Page<Event> result = eventRepository.findAll(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertTrue(result.isFirst());
    }

    @Test
    void shouldDeleteEvent() {
        Event event = new Event(
                null,
                "Temporary Event",
                LocalDate.of(2026, 9, 5),
                "Event to be deleted"
        );

        Event savedEvent = eventRepository.save(event);

        eventRepository.deleteById(savedEvent.getId());

        Optional<Event> result = eventRepository.findById(savedEvent.getId());

        assertTrue(result.isEmpty());
    }
}