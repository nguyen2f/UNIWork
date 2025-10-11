package com.uniwork.controller;

import com.uniwork.entity.request.CreateEventRequest;
import com.uniwork.interceptors.Payload;
import com.uniwork.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/event")
@Slf4j
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/get-all")
    public ResponseEntity getAllEvents(@RequestAttribute Payload payload,
                                       @RequestParam(required = false) Long begin,
                                       @RequestParam(required = false) Long end) {
        return eventService.getAllEvent(payload.getUserId(), begin, end );
    }

    @PostMapping("/create-event")
    public ResponseEntity createEvent(@RequestBody CreateEventRequest createEventRequest,
                                      @RequestAttribute Payload payload,
                                      @RequestParam(required = false) Long begin,
                                      @RequestParam(required = false) Long end) {
        return eventService.createEvent(payload.getUserId(), createEventRequest);
    }
}
