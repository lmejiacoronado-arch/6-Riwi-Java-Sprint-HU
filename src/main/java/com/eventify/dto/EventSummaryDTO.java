package com.eventify.dto;

import java.time.LocalDate;

public record EventSummaryDTO(
        Long id,
        String eventName,
        LocalDate eventDate,
        String venueName,
        String city
) {
}