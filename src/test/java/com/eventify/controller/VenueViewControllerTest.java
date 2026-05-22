package com.eventify.controller;

import com.eventify.model.Venue;
import com.eventify.service.VenueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VenueViewController.class)
class VenueViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VenueService venueService;

    @Test
    void shouldReturnVenuesListView() throws Exception {
        List<Venue> venues = List.of(
                new Venue(
                        1L,
                        "Main Auditorium",
                        "123 Main Street",
                        500
                )
        );

        Page<Venue> venuePage = new PageImpl<>(
                venues,
                PageRequest.of(0, 100),
                venues.size()
        );

        when(venueService.findAll(any())).thenReturn(venuePage);

        mockMvc.perform(get("/admin/venues"))
                .andExpect(status().isOk())
                .andExpect(view().name("venues/list"))
                .andExpect(model().attributeExists("venues"));
    }

    @Test
    void shouldReturnCreateVenueFormView() throws Exception {
        mockMvc.perform(get("/admin/venues/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("venues/form"))
                .andExpect(model().attributeExists("venue"));
    }

    @Test
    void shouldCreateVenueAndRedirectToVenuesList() throws Exception {
        Venue venue = new Venue(
                1L,
                "Convention Center",
                "456 Business Avenue",
                1200
        );

        when(venueService.create(any(Venue.class))).thenReturn(venue);

        mockMvc.perform(post("/admin/venues")
                        .param("name", "Convention Center")
                        .param("address", "456 Business Avenue")
                        .param("capacity", "1200"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/venues"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(venueService).create(any(Venue.class));
    }
}