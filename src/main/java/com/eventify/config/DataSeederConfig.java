package com.eventify.config;

import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.repository.EventRepository;
import com.eventify.repository.VenueRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataSeederConfig {

    @Bean
    public String seedData(EventRepository eventRepository, VenueRepository venueRepository) {

        Venue mainAuditorium = new Venue(
                1,
                "Main Auditorium",
                "123 Main Street",
                500
        );

        Venue conventionCenter = new Venue(
                2,
                "Convention Center",
                "456 Business Avenue",
                1200
        );

        venueRepository.save(mainAuditorium);
        venueRepository.save(conventionCenter);

        Event javaConference = new Event(
                1,
                "Java Conference",
                LocalDate.of(2026, 6, 10),
                "Technology event focused on Java and Spring Boot"
        );

        Event musicFestival = new Event(
                2,
                "Music Festival",
                LocalDate.of(2026, 7, 15),
                "Outdoor music festival with local artists"
        );

        eventRepository.save(javaConference);
        eventRepository.save(musicFestival);

        return "Initial data loaded successfully";
    }
}
