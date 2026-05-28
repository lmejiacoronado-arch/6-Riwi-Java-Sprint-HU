package com.eventify.repository;

import com.eventify.dto.EventSummaryDTO;
import com.eventify.model.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByNameContainingIgnoreCase(String name);

    @Query("""
            SELECT new com.eventify.dto.EventSummaryDTO(
                e.id,
                e.name,
                e.eventDate,
                v.name,
                v.city
            )
            FROM Event e
            JOIN e.venue v
            ORDER BY e.eventDate DESC
            """)
    Slice<EventSummaryDTO> findEventSummaries(Pageable pageable);

    @EntityGraph(attributePaths = {"venue", "categories"})
    @Query("""
            SELECT DISTINCT e
            FROM Event e
            ORDER BY e.eventDate DESC
            """)
    Slice<Event> findAllWithRelations(Pageable pageable);
}