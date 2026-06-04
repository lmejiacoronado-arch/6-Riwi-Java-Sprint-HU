package com.eventify.repository;

import com.eventify.dto.EventSummaryDTO;
import com.eventify.model.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("""
            SELECT e
            FROM Event e
            WHERE LOWER(e.name) LIKE :namePattern
            ORDER BY e.eventDate DESC
            """)
    List<Event> findByNameContainingIgnoreCase(@Param("namePattern") String namePattern);

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

    @Query("""
            SELECT DISTINCT new com.eventify.dto.EventSummaryDTO(
                e.id,
                e.name,
                e.eventDate,
                v.name,
                v.city
            )
            FROM Event e
            JOIN e.venue v
            LEFT JOIN e.categories c
            WHERE (:cityPattern IS NULL OR LOWER(v.city) LIKE :cityPattern)
              AND (:categoryPattern IS NULL OR LOWER(c.name) LIKE :categoryPattern)
              AND (:minCapacity IS NULL OR v.capacity >= :minCapacity)
              AND (:startDate IS NULL OR e.eventDate >= :startDate)
              AND (:endDate IS NULL OR e.eventDate <= :endDate)
            ORDER BY e.eventDate DESC
            """)
    Slice<EventSummaryDTO> searchEventSummaries(
            @Param("cityPattern") String cityPattern,
            @Param("categoryPattern") String categoryPattern,
            @Param("minCapacity") Integer minCapacity,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

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
            WHERE LOWER(v.city) LIKE :cityPattern
            ORDER BY e.eventDate DESC
            """)
    Slice<EventSummaryDTO> findByCity(
            @Param("cityPattern") String cityPattern,
            Pageable pageable
    );

    @Query("""
            SELECT DISTINCT new com.eventify.dto.EventSummaryDTO(
                e.id,
                e.name,
                e.eventDate,
                v.name,
                v.city
            )
            FROM Event e
            JOIN e.venue v
            JOIN e.categories c
            WHERE LOWER(c.name) LIKE :categoryPattern
            ORDER BY e.eventDate DESC
            """)
    Slice<EventSummaryDTO> findByCategory(
            @Param("categoryPattern") String categoryPattern,
            Pageable pageable
    );

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
            WHERE v.capacity >= :minCapacity
            ORDER BY e.eventDate DESC
            """)
    Slice<EventSummaryDTO> findByCapacityGreaterThanEqual(
            @Param("minCapacity") Integer minCapacity,
            Pageable pageable
    );

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
            WHERE e.eventDate BETWEEN :startDate AND :endDate
            ORDER BY e.eventDate DESC
            """)
    Slice<EventSummaryDTO> findByDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"venue", "categories"})
    @Query("""
            SELECT DISTINCT e
            FROM Event e
            ORDER BY e.eventDate DESC
            """)
    Slice<Event> findAllWithRelations(Pageable pageable);
}