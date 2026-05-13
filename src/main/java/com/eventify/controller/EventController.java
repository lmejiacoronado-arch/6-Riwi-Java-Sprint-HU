package com.eventify.controller;

import com.eventify.model.Event;
import com.eventify.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Operations for managing events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(
            summary = "Create a new event",
            description = "Creates a new event after validating the business rules"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid event data")
    })
    @PostMapping
    public ResponseEntity<Event> create(@RequestBody Event event) {
        Event createdEvent = eventService.create(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @Operation(
            summary = "Get paginated events",
            description = "Returns a paginated and sorted list of events. Example: /api/events?page=0&size=10&sort=name,asc"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<Page<Event>> findAll(
            @ParameterObject
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<Event> events = eventService.findAll(pageable);
        return ResponseEntity.ok(events);
    }

    @Operation(
            summary = "Get event by ID",
            description = "Returns a single event by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event found successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Event> findById(
            @Parameter(description = "Event ID", example = "1")
            @PathVariable Long id
    ) {
        Event event = eventService.findById(id);
        return ResponseEntity.ok(event);
    }

    @Operation(
            summary = "Update event by ID",
            description = "Updates an existing event if the provided ID exists"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid event data"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Event> update(
            @Parameter(description = "Event ID", example = "1")
            @PathVariable Long id,
            @RequestBody Event event
    ) {
        Event updatedEvent = eventService.update(id, event);
        return ResponseEntity.ok(updatedEvent);
    }

    @Operation(
            summary = "Delete event by ID",
            description = "Deletes an existing event if the provided ID exists"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Event deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Event ID", example = "1")
            @PathVariable Long id
    ) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}