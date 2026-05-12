package com.eventify.service;

import com.eventify.exception.ResourceNotFoundException;
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

    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " +id));
    }

    public Event update(Long id, Event event) {
        Event existingEvent = findById(id);

        validateEvent(event);

        existingEvent.setName(event.getName());
        existingEvent.setEventDate(event.getEventDate());
        existingEvent.setDescription(event.getDescription());

        return eventRepository.save(existingEvent);
    }

    public void delete (Long id) {
        Event existingEvent = findById(id);
        eventRepository.delete(existingEvent);
    }

    private void validateEvent(Event event) {
        if (event.getName() == null || event.getName().isBlank()) {
            throw new IllegalArgumentException("Event name cannot be empty");
        }

        if (event.getEventDate() == null) {
            throw new IllegalArgumentException("Event date cannot be null");
        }
    }
}
