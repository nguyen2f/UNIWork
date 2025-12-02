package com.uniwork.controller;

import com.uniwork.model.entity.Event;
import com.uniwork.model.request.CreateEventRequest;
import com.uniwork.model.response.ResponseFactory;
import com.uniwork.interceptors.Payload;
import com.uniwork.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/event")
@Slf4j
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/all")
    public ResponseEntity getAllEvents(@RequestAttribute(required = false) Payload payload,
                                       @RequestParam(required = false) Long begin,
                                       @RequestParam(required = false) Long end) {
        List<Event> events = eventService.getAllEvent(payload.getUserId(), begin, end);
        return ResponseFactory.success(events);
    }

    @PostMapping("/create")
    public ResponseEntity createEvent(@RequestBody CreateEventRequest createEventRequest,
                                      @RequestAttribute(required = false) Payload payload,
                                      @RequestParam(required = false) Long begin,
                                      @RequestParam(required = false) Long end) {
        Event event = eventService.createEvent(payload.getUserId(), createEventRequest);
        return ResponseFactory.success(event);
    }
}
