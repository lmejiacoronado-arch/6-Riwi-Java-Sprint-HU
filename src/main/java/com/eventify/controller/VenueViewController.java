package com.eventify.controller;

import com.eventify.model.Venue;
import com.eventify.service.VenueService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/venues")
public class VenueViewController {

    private final VenueService venueService;

    public VenueViewController(VenueService venueService) {
        this.venueService = venueService;
    }

    @GetMapping
    public String listVenues(Model model) {
        Page<Venue> venuesPage = venueService.findAll(PageRequest.of(0, 100));

        model.addAttribute("venues", venuesPage.getContent());

        return "venues/list";
    }

    @GetMapping("/new")
    public String showCreateVenueForm(Model model) {
        model.addAttribute("venue", new Venue());

        return "venues/form";
    }

    @PostMapping
    public String createVenue(
            @ModelAttribute Venue venue,
            RedirectAttributes redirectAttributes
    ) {
        venueService.create(venue);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Venue created successfully"
        );

        return "redirect:/admin/venues";
    }
}