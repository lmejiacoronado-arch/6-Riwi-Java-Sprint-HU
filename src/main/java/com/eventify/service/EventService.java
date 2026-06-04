package com.eventify.service;

import com.eventify.dto.EventSummaryDTO;
import com.eventify.exception.ResourceNotFoundException;
import com.eventify.model.Event;
import com.eventify.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event create(Event event) {
        validateEvent(event);
        return eventRepository.save(event);
    }

    public Page<Event> findAll(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    public Slice<EventSummaryDTO> findEventSummaries(Pageable pageable) {
        return eventRepository.findEventSummaries(pageable);
    }

    public Slice<EventSummaryDTO> searchEventSummaries(
            String city,
            String category,
            Integer minCapacity,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        validateDateRange(startDate, endDate);

        String cityPattern = toLikePattern(city);
        String categoryPattern = toLikePattern(category);

        return eventRepository.searchEventSummaries(
                cityPattern,
                categoryPattern,
                minCapacity,
                startDate,
                endDate,
                pageable
        );
    }

    public Slice<Event> findAllWithRelations(Pageable pageable) {
        return eventRepository.findAllWithRelations(pageable);
    }

    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
    }

    public Event update(Long id, Event event) {
        Event existingEvent = findById(id);

        validateEvent(event);

        existingEvent.setName(event.getName());
        existingEvent.setEventDate(event.getEventDate());
        existingEvent.setDescription(event.getDescription());
        existingEvent.setVenue(event.getVenue());
        existingEvent.setCategories(event.getCategories());

        return eventRepository.save(existingEvent);
    }

    public void delete(Long id) {
        Event existingEvent = findById(id);
        existingEvent.softDelete();
        eventRepository.save(existingEvent);
    }

    private void validateEvent(Event event) {
        if (event.getName() == null || event.getName().isBlank()) {
            throw new IllegalArgumentException("Event name cannot be empty");
        }

        if (event.getEventDate() == null) {
            throw new IllegalArgumentException("Event date cannot be null");
        }

        if (event.getVenue() == null || event.getVenue().getId() == null) {
            throw new IllegalArgumentException("Event venue is required");
        }

        if (event.getCategories() == null || event.getCategories().isEmpty()) {
            throw new IllegalArgumentException("Event must have at least one category");
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
    }

    private String toLikePattern(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return "%" + value.trim().toLowerCase() + "%";
    }
}