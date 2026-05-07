package com.eventify.service;

import com.eventify.model.Event;
import com.eventify.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    private void validateEvent(Event event) {
        if (event.getName() == null || event.getName().isBlank()) {
            throw new IllegalArgumentException("Event name cannot be empty");
        }

        if (event.getDate() == null) {
            throw new IllegalArgumentException("Event date cannot be null");
        }
    }
}
