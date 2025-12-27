package com.uniwork.service;

import com.uniwork.model.enumuration.Priority;
import com.uniwork.model.entity.Event;
import com.uniwork.model.request.CreateEventRequest;
import com.uniwork.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
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
        event.setDate(LocalDateTime.now());
        event.setCreatedBy(userId);
        return eventRepository.save(event);
    }
}
