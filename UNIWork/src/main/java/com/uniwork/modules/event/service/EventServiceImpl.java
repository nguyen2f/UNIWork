package com.uniwork.modules.event.service;

import com.uniwork.enums.Priority;
import com.uniwork.modules.event.entity.Event;
import com.uniwork.modules.event.request.CreateEventRequest;
import com.uniwork.modules.event.repository.EventRepository;
import com.uniwork.modules.event.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Page<Event> getAllEvent(Long userId, Long begin, Long end, Pageable pageable) {
        return eventRepository.findAllByOrderByCreatedByDesc(pageable);
    }

    public Event createEvent(Long userId, CreateEventRequest createEventRequest) {
        Event event = new Event();
        event.setPriority(Priority.fromCode(createEventRequest.getPriority()));
        event.setTitle(createEventRequest.getTitle());
        event.setType(createEventRequest.getType());
        event.setLocation(createEventRequest.getLocation());
        event.setDate(createEventRequest.getDate());
        event.setCreatedBy(userId);
        return eventRepository.save(event);
    }
}
