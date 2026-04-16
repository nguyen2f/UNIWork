package com.uniwork.modules.event.service;

import com.uniwork.modules.event.entity.Event;
import com.uniwork.modules.event.request.CreateEventRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {

    Page<Event> getAllEvent(Long userId, Long begin, Long end, Pageable pageable);

    Event createEvent(Long userId, CreateEventRequest createEventRequest);
}
