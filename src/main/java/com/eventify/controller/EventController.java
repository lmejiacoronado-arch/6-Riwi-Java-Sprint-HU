package com.eventify.controller;

import com.eventify.dto.EventSummaryDTO;
import com.eventify.model.Event;
import com.eventify.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Operations for managing events and optimized event catalogs")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(
            summary = "Create a new event",
            description = "Creates a new event after validating the business rules. The event must be associated with a venue. " +
                    "Category links are persisted through the explicit events_categories join table."
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
            summary = "Search optimized event summaries",
            description = "Returns a lightweight Slice of EventSummaryDTO records ordered by event date descending. " +
                    "The response uses record-based projections instead of heavy Event entities. " +
                    "Relational filters are optional and can be combined: city performs a partial, case-insensitive search over Venue.city; " +
                    "category performs a partial, case-insensitive search over Category.name; minCapacity filters venues with capacity greater than or equal to the value; " +
                    "startDate and endDate constrain the event date range. Soft deleted events are excluded automatically by the global SQL restriction."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Event summaries retrieved successfully as a Slice of EventSummaryDTO records",
                    content = @Content(schema = @Schema(implementation = EventSummaryDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid filter values, for example a startDate after endDate")
    })
    @GetMapping
    public ResponseEntity<Slice<EventSummaryDTO>> findAll(
            @Parameter(
                    description = "Partial and case-insensitive filter by venue city. Example: 'bog' matches Bogotá.",
                    example = "bog"
            )
            @RequestParam(required = false) String city,

            @Parameter(
                    description = "Partial and case-insensitive filter by category name. Example: 'rock' matches Rock.",
                    example = "rock"
            )
            @RequestParam(required = false) String category,

            @Parameter(
                    description = "Minimum venue capacity. Only events assigned to venues with capacity greater than or equal to this value are returned.",
                    example = "200"
            )
            @RequestParam(required = false) Integer minCapacity,

            @Parameter(
                    description = "Start date for the event date range filter, inclusive. Format: yyyy-MM-dd.",
                    example = "2026-06-01"
            )
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @Parameter(
                    description = "End date for the event date range filter, inclusive. Format: yyyy-MM-dd.",
                    example = "2026-12-31"
            )
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @ParameterObject
            @PageableDefault(size = 10, sort = "eventDate", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Slice<EventSummaryDTO> events = eventService.searchEventSummaries(
                city,
                category,
                minCapacity,
                startDate,
                endDate,
                pageable
        );

        return ResponseEntity.ok(events);
    }

    @Operation(
            summary = "Get event by ID",
            description = "Returns a single active event by its ID. Soft deleted events are automatically excluded by the global SQL restriction."
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
            description = "Updates an existing active event if the provided ID exists. The event must keep a valid venue relationship. " +
                    "Category assignments can be replaced through the many-to-many relationship."
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
            description = "Performs a logical deletion by setting the event as inactive instead of physically deleting it. " +
                    "The record remains in the database for auditability, and normal catalog/search queries exclude it automatically through @SQLRestriction."
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