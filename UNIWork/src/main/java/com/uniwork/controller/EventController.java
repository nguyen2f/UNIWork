package com.uniwork.controller;

import com.uniwork.interceptors.Payload;
import com.uniwork.model.entity.Event;
import com.uniwork.model.request.CreateEventRequest;
import com.uniwork.model.response.PageMetadata;
import com.uniwork.model.response.ResponseFactory;
import com.uniwork.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/event")
@Slf4j
@PreAuthorize("hasAuthority('PERM_MANAGE_EVENTS')")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/all")
    public ResponseEntity getAllEvents(@RequestAttribute(required = false) Payload payload,
                                       @RequestParam(required = false) Long begin,
                                       @RequestParam(required = false) Long end,
                                       Pageable pageable) {
        Page<Event> events = eventService.getAllEvent(payload.getUserId(), begin, end, pageable);
        PageMetadata metadata = PageMetadata.of(events.getNumber(), events.getSize(), events.getTotalElements());
        return ResponseFactory.makePagination(events.getContent(), metadata);
    }

    @PreAuthorize(("hasAuthority('PERM_CREATE_EVENT')"))
    @PostMapping("/create")
    public ResponseEntity createEvent(@RequestBody CreateEventRequest createEventRequest,
                                      @RequestAttribute(required = false) Payload payload,
                                      @RequestParam(required = false) Long begin,
                                      @RequestParam(required = false) Long end) {
        Event event = eventService.createEvent(payload.getUserId(), createEventRequest);
        return ResponseFactory.success(event);
    }
}
