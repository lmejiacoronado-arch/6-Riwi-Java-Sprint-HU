package com.eventify.controller;

import com.eventify.dto.EventSummaryDTO;
import com.eventify.model.Category;
import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.repository.CategoryRepository;
import com.eventify.repository.VenueRepository;
import com.eventify.service.EventService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin/events")
public class EventViewController {

    private final EventService eventService;
    private final VenueRepository venueRepository;
    private final CategoryRepository categoryRepository;

    public EventViewController(
            EventService eventService,
            VenueRepository venueRepository,
            CategoryRepository categoryRepository
    ) {
        this.eventService = eventService;
        this.venueRepository = venueRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String listEvents(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);

        Slice<EventSummaryDTO> eventsSlice = eventService.searchEventSummaries(
                city,
                category,
                minCapacity,
                startDate,
                endDate,
                PageRequest.of(safePage, safeSize)
        );

        model.addAttribute("events", eventsSlice.getContent());
        model.addAttribute("eventsSlice", eventsSlice);

        model.addAttribute("city", city);
        model.addAttribute("category", category);
        model.addAttribute("minCapacity", minCapacity);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        model.addAttribute("currentPage", safePage);
        model.addAttribute("previousPage", safePage - 1);
        model.addAttribute("nextPage", safePage + 1);
        model.addAttribute("size", safeSize);
        model.addAttribute("hasPrevious", safePage > 0);
        model.addAttribute("hasNext", eventsSlice.hasNext());

        return "events/list";
    }

    @GetMapping("/new")
    public String showCreateEventForm(Model model) {
        model.addAttribute("event", new Event());
        loadFormOptions(model);

        return "events/form";
    }

    @PostMapping
    public String createEvent(
            @ModelAttribute Event event,
            @RequestParam Long venueId,
            @RequestParam(required = false) List<Long> categoryIds,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Venue venue = venueRepository.findById(venueId)
                    .orElseThrow(() -> new IllegalArgumentException("Selected venue does not exist"));

            Set<Category> categories = new HashSet<>();

            if (categoryIds != null && !categoryIds.isEmpty()) {
                categories.addAll(categoryRepository.findAllById(categoryIds));
            }

            event.setVenue(venue);
            event.setCategories(categories);
            event.setActive(true);

            eventService.create(event);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Event created successfully"
            );

            return "redirect:/admin/events";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("event", event);
            loadFormOptions(model);

            return "events/form";
        }
    }

    private void loadFormOptions(Model model) {
        model.addAttribute("venues", venueRepository.findAll(Sort.by("name")));
        model.addAttribute("categories", categoryRepository.findAll(Sort.by("name")));
    }
}