package com.uniwork.modules.event.controller;

import com.uniwork.modules.auth.dto.Payload;
import com.uniwork.modules.event.entity.Event;
import com.uniwork.modules.event.request.CreateEventRequest;
import com.uniwork.common.response.PageMetadata;
import com.uniwork.common.response.ResponseFactory;
import com.uniwork.modules.event.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
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
