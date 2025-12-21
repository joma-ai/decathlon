package com.example.decathlon.service;

import com.example.decathlon.api.dto.PointsRequest;
import com.example.decathlon.api.dto.PointsResponse;
import com.example.decathlon.domain.event.Event;
import com.example.decathlon.domain.event.FieldEvent;
import com.example.decathlon.domain.event.TrackEvent;
import com.example.decathlon.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PointsService {
    private final EventRepository eventRepository;

    public PointsService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional(readOnly = true)
    public PointsResponse calculate(PointsRequest req) {
        Event event = eventRepository.findById(req.eventCode())
                .orElseThrow(() -> new IllegalArgumentException("Unknown eventCode: " + req.eventCode()));

        validateResult(event, req.result());

        double p = normalize(req.result(), event);
        int points = computePoints(event, p);

        return new PointsResponse(
                event.getCode(),
                event.getName(),
                event.getUnit(),
                req.result(),
                points
        );
    }

    private double normalize(double raw, Event event) {
        return raw;
    }

    private int computePoints(Event event, double p) {
        double base;
        if (event instanceof TrackEvent) {
            base = event.getB() - p;
        } else if (event instanceof FieldEvent) {
            base = p - event.getB();
        } else {
            throw new IllegalStateException("Unsupported event subtype: " + event.getClass().getName());
        }

        if (base <= 0) return 0;
        return (int) Math.floor(event.getA() * Math.pow(base, event.getC()));
    }

    private void validateResult(Event event, double result) {
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            throw new IllegalArgumentException("Result must be a finite number");
        }
        if (result < 0) {
            throw new IllegalArgumentException("Result must be >= 0");
        }

        switch (event.getUnit()) {
            case SECONDS -> {
                if (result < 1 || result > 2000) throw new IllegalArgumentException("Result (seconds) out of plausible range");
            }
            case METERS -> {
                if (result < 0.5 || result > 150) throw new IllegalArgumentException("Result (meters) out of plausible range");
            }
            case CENTIMETERS -> {
                if (result < 50 || result > 1200) throw new IllegalArgumentException("Result (centimeters) out of plausible range");
            }
        }
    }
}
