package com.example.decathlon.service;

import com.example.decathlon.api.dto.EventDto;
import com.example.decathlon.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventQueryService {

    private final EventRepository eventRepository;

    public EventQueryService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional(readOnly = true)
    public List<EventDto> listEvents() {
        return eventRepository.findAll().stream()
                .map(e -> new EventDto(e.getCode(), e.getName(), e.getUnit()))
                .toList();
    }
}
