package com.eventify.controller;

import com.eventify.model.Event;
import com.eventify.service.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/events")
public class EventViewController {

    private final EventService eventService;

    public EventViewController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public String listEvents(Model model) {
        Page<Event> eventsPage = eventService.findAll(PageRequest.of(0, 100));

        model.addAttribute("events", eventsPage.getContent());

        return "events/list";
    }

    @GetMapping("/new")
    public String showCreateEventForm(Model model) {
        model.addAttribute("event", new Event());

        return "events/form";
    }

    @PostMapping
    public String createEvent(
            @ModelAttribute Event event,
            RedirectAttributes redirectAttributes
    ) {
        eventService.create(event);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Event created successfully"
        );

        return "redirect:/admin/events";
    }
}