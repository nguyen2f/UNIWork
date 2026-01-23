package com.uniwork.service;

import com.uniwork.model.entity.Event;
import com.uniwork.model.request.CreateEventRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {

    Page<Event> getAllEvent(Long userId, Long begin, Long end, Pageable pageable);

    Event createEvent(Long userId, CreateEventRequest createEventRequest);
}
