package com.uniwork.service;

import com.uniwork.entity.enumuration.Priority;
import com.uniwork.entity.model.Event;
import com.uniwork.entity.request.CreateEventRequest;
import com.uniwork.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAllEvent(Long userId, Long begin, Long end) {
        return eventRepository.findAll();
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
