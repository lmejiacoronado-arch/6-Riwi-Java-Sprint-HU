package com.eventify.config;

import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.repository.EventRepository;
import com.eventify.repository.VenueRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataSeederConfig {

    @Bean
    public CommandLineRunner seedData(
            EventRepository eventRepository,
            VenueRepository venueRepository
    ) {
        return args -> {
            if (venueRepository.count() == 0) {
                Venue mainAuditorium = new Venue(
                        null,
                        "Main Auditorium",
                        "123 Main Street",
                        500
                );

                Venue conventionCenter = new Venue(
                        null,
                        "Convention Center",
                        "456 Business Avenue",
                        1200
                );

                venueRepository.save(mainAuditorium);
                venueRepository.save(conventionCenter);
            }

            if (eventRepository.count() == 0) {
                Event javaConference = new Event(
                        null,
                        "Java Conference",
                        LocalDate.of(2026, 6, 10),
                        "Technology event focused on Java and Spring Boot"
                );

                Event musicFestival = new Event(
                        null,
                        "Music Festival",
                        LocalDate.of(2026, 7, 15),
                        "Outdoor music festival with local artists"
                );

                eventRepository.save(javaConference);
                eventRepository.save(musicFestival);
            }
        };
    }
}