package com.eventify.repository;

import com.eventify.dto.EventSummaryDTO;
import com.eventify.model.Category;
import com.eventify.model.Event;
import com.eventify.model.Venue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void shouldSaveEventWithVenueAndCategories() {
        Venue venue = venueRepository.save(createVenue());
        Category category = categoryRepository.save(createCategory());

        Event event = new Event(
                null,
                "Java Conference",
                LocalDate.of(2026, 6, 10),
                "Technology event",
                true,
                venue,
                Set.of(category)
        );

        Event savedEvent = eventRepository.save(event);

        assertNotNull(savedEvent.getId());
        assertEquals("Java Conference", savedEvent.getName());
        assertEquals(LocalDate.of(2026, 6, 10), savedEvent.getEventDate());
        assertEquals("Main Auditorium", savedEvent.getVenue().getName());
        assertEquals(1, savedEvent.getCategories().size());
    }

    @Test
    void shouldFindEventById() {
        Venue venue = venueRepository.save(createVenue());
        Category category = categoryRepository.save(createCategory());

        Event savedEvent = eventRepository.save(new Event(
                null,
                "Spring Boot Summit",
                LocalDate.of(2026, 7, 20),
                "Backend conference",
                true,
                venue,
                Set.of(category)
        ));

        var result = eventRepository.findById(savedEvent.getId());

        assertTrue(result.isPresent());
        assertEquals("Spring Boot Summit", result.get().getName());
        assertEquals(LocalDate.of(2026, 7, 20), result.get().getEventDate());
    }

    @Test
    void shouldFindEventsByNameContainingIgnoreCase() {
        Venue venue = venueRepository.save(createVenue());
        Category category = categoryRepository.save(createCategory());

        eventRepository.save(new Event(
                null,
                "Spring Boot Summit",
                LocalDate.of(2026, 7, 20),
                "Backend conference",
                true,
                venue,
                Set.of(category)
        ));

        List<Event> results = eventRepository.findByNameContainingIgnoreCase("spring");

        assertFalse(results.isEmpty());
        assertEquals("Spring Boot Summit", results.get(0).getName());
    }

    @Test
    void shouldReturnPaginatedEvents() {
        Venue venue = venueRepository.save(createVenue());
        Category category = categoryRepository.save(createCategory());

        eventRepository.save(new Event(
                null,
                "Java Conference",
                LocalDate.of(2026, 6, 10),
                "Technology event",
                true,
                venue,
                Set.of(category)
        ));

        eventRepository.save(new Event(
                null,
                "Music Festival",
                LocalDate.of(2026, 7, 15),
                "Music event",
                true,
                venue,
                Set.of(category)
        ));

        eventRepository.save(new Event(
                null,
                "Spring Boot Summit",
                LocalDate.of(2026, 8, 20),
                "Backend event",
                true,
                venue,
                Set.of(category)
        ));

        Pageable pageable = PageRequest.of(0, 2);

        Page<Event> result = eventRepository.findAll(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertTrue(result.isFirst());
    }

    @Test
    void shouldNotReturnInactiveEventsBecauseOfSqlRestriction() {
        Venue venue = venueRepository.save(createVenue());
        Category category = categoryRepository.save(createCategory());

        Event activeEvent = new Event(
                null,
                "Active Event",
                LocalDate.of(2026, 8, 20),
                "Visible event",
                true,
                venue,
                Set.of(category)
        );

        Event inactiveEvent = new Event(
                null,
                "Inactive Event",
                LocalDate.of(2026, 9, 20),
                "Hidden event",
                false,
                venue,
                Set.of(category)
        );

        eventRepository.save(activeEvent);
        eventRepository.save(inactiveEvent);

        List<Event> events = eventRepository.findAll();

        assertEquals(1, events.size());
        assertEquals("Active Event", events.get(0).getName());
    }

    @Test
    void shouldReturnEventSummariesAsSlice() {
        Venue venue = venueRepository.save(createVenue());
        Category category = categoryRepository.save(createCategory());

        eventRepository.save(new Event(
                null,
                "Java Conference",
                LocalDate.of(2026, 6, 10),
                "Technology event",
                true,
                venue,
                Set.of(category)
        ));

        Pageable pageable = PageRequest.of(0, 10);

        Slice<EventSummaryDTO> result = eventRepository.findEventSummaries(pageable);

        assertFalse(result.getContent().isEmpty());
        assertEquals("Java Conference", result.getContent().get(0).eventName());
        assertEquals(LocalDate.of(2026, 6, 10), result.getContent().get(0).eventDate());
        assertEquals("Main Auditorium", result.getContent().get(0).venueName());
        assertEquals("Medellin", result.getContent().get(0).city());
    }

    @Test
    void shouldReturnEventsWithRelationsUsingEntityGraph() {
        Venue venue = venueRepository.save(createVenue());
        Category category = categoryRepository.save(createCategory());

        eventRepository.save(new Event(
                null,
                "Java Conference",
                LocalDate.of(2026, 6, 10),
                "Technology event",
                true,
                venue,
                Set.of(category)
        ));

        Pageable pageable = PageRequest.of(0, 10);

        Slice<Event> result = eventRepository.findAllWithRelations(pageable);

        assertFalse(result.getContent().isEmpty());

        Event event = result.getContent().get(0);

        assertNotNull(event.getVenue());
        assertEquals("Main Auditorium", event.getVenue().getName());
        assertFalse(event.getCategories().isEmpty());
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

    private Category createCategory() {
        return new Category(
                null,
                "Conferences",
                "Technology conferences"
        );
    }
}