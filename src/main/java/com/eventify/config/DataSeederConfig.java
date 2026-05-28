package com.eventify.config;

import com.eventify.model.Category;
import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.repository.CategoryRepository;
import com.eventify.repository.EventRepository;
import com.eventify.repository.VenueRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Set;

@Configuration
public class DataSeederConfig {

    @Bean
    public CommandLineRunner seedData(
            VenueRepository venueRepository,
            CategoryRepository categoryRepository,
            EventRepository eventRepository
    ) {
        return args -> {
            if (venueRepository.count() > 0 || categoryRepository.count() > 0 || eventRepository.count() > 0) {
                return;
            }

            Venue mainAuditorium = venueRepository.save(new Venue(
                    null,
                    "Main Auditorium",
                    "123 Main Street",
                    500,
                    "Medellin"
            ));

            Venue conventionCenter = venueRepository.save(new Venue(
                    null,
                    "Convention Center",
                    "456 Business Avenue",
                    1200,
                    "Bogota"
            ));

            Category conferences = categoryRepository.save(new Category(
                    null,
                    "Conferences",
                    "Technology and professional conferences"
            ));

            Category concerts = categoryRepository.save(new Category(
                    null,
                    "Concerts",
                    "Live music and concert events"
            ));

            Category workshops = categoryRepository.save(new Category(
                    null,
                    "Workshops",
                    "Practical learning sessions"
            ));

            Event javaConference = new Event(
                    null,
                    "Java Conference",
                    LocalDate.of(2026, 6, 10),
                    "Technology event focused on Java and Spring Boot",
                    true,
                    mainAuditorium,
                    Set.of(conferences, workshops)
            );

            Event musicFestival = new Event(
                    null,
                    "Music Festival",
                    LocalDate.of(2026, 7, 15),
                    "Outdoor music festival with local artists",
                    true,
                    conventionCenter,
                    Set.of(concerts)
            );

            eventRepository.save(javaConference);
            eventRepository.save(musicFestival);
        };
    }
}