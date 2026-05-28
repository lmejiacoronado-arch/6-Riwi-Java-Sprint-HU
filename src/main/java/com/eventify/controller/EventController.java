package com.eventify.controller;

import com.eventify.dto.EventSummaryDTO;
import com.eventify.model.Event;
import com.eventify.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
            description = "Creates a new event after validating the business rules. The event must be associated with a venue."
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
            summary = "Get event summaries",
            description = "Returns a lightweight Slice of event summaries ordered by event date descending. " +
                    "This endpoint uses EventSummaryDTO to avoid loading heavy entities and improve performance for large catalogs. " +
                    "Soft deleted events are automatically excluded."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event summaries retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<Slice<EventSummaryDTO>> findAll(
            @ParameterObject
            @PageableDefault(size = 10, sort = "eventDate", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Slice<EventSummaryDTO> events = eventService.findEventSummaries(pageable);
        return ResponseEntity.ok(events);
    }

    @Operation(
            summary = "Get event by ID",
            description = "Returns a single active event by its ID. Soft deleted events are automatically excluded."
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
            description = "Updates an existing active event if the provided ID exists. The event must keep a valid venue relationship."
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
            summary = "Soft delete event by ID",
            description = "Performs a logical deletion by setting the event as inactive. " +
                    "The record remains in the database but is automatically excluded from normal queries."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Event soft deleted successfully"),
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