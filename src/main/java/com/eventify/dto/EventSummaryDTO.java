package com.eventify.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(
        name = "EventSummaryDTO",
        description = "Lightweight record used for massive event catalog listings. It flattens Event and Venue data without exposing heavy entity graphs."
)
public record EventSummaryDTO(
        @Schema(description = "Event identifier", example = "1")
        Long id,

        @Schema(description = "Event name", example = "Concierto de ROCK")
        String eventName,

        @Schema(description = "Event date. All catalog listings are ordered by this field descending.", example = "2026-07-20")
        LocalDate eventDate,

        @Schema(description = "Name of the assigned venue", example = "Auditorio Principal")
        String venueName,

        @Schema(description = "City of the assigned venue, used for relational filtering", example = "Bogotá")
        String city
) {
}