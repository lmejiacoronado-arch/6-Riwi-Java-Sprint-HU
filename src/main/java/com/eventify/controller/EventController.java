package com.eventify.controller;

import com.eventify.model.Event;
import com.eventify.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@Tag(
        name = "events",
        description = "Operations for managing events"
)
public class EventController {
    private final EventService eventService;

    public EventController (EventService eventService) {
        this.eventService = eventService;
    }

    @Operation (
            summary = "Create a new event",
            description = "Creates an event after validating the business rules"
    )
    @PostMapping
    public ResponseEntity<Event> create (@RequestBody Event event) {
        Event createdEvent = eventService.create(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @Operation(
            summary = "Get all events",
            description = "Returns the complete event catalog"
    )
    @GetMapping
    public ResponseEntity<List<Event>> findAll () {
        List<Event> events = eventService.findAll();
        return ResponseEntity.ok(events);
    }
}
