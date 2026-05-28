package com.eventify.controller;

import com.eventify.model.Category;
import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventViewController.class)
class EventViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @Test
    void shouldReturnEventsListView() throws Exception {
        List<Event> events = List.of(createEvent());

        Page<Event> eventPage = new PageImpl<>(
                events,
                PageRequest.of(0, 100),
                events.size()
        );

        when(eventService.findAll(any())).thenReturn(eventPage);

        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("events/list"))
                .andExpect(model().attributeExists("events"));
    }

    @Test
    void shouldReturnCreateEventFormView() throws Exception {
        mockMvc.perform(get("/admin/events/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("events/form"))
                .andExpect(model().attributeExists("event"));
    }

    @Test
    void shouldCreateEventAndRedirectToEventsList() throws Exception {
        when(eventService.create(any(Event.class))).thenReturn(createEvent());

        mockMvc.perform(post("/admin/events")
                        .param("name", "Spring Boot Summit")
                        .param("date", "2026-07-20")
                        .param("description", "Backend conference")
                        .param("venue.id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/events"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(eventService).create(any(Event.class));
    }

    private Event createEvent() {
        return new Event(
                1L,
                "Java Conference",
                LocalDate.of(2026, 6, 10),
                "Technology event",
                true,
                new Venue(1L, "Main Auditorium", "123 Main Street", 500, "Medellin"),
                Set.of(new Category(1L, "Conferences", "Technology conferences"))
        );
    }
}