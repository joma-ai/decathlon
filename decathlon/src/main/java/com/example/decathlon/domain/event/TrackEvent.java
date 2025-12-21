package com.example.decathlon.domain.event;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TRACK")
public class TrackEvent extends Event {

    protected TrackEvent() {
    }

    protected TrackEvent(String code, String name, Unit unit, double a, double b, double c) {
        super(code, name, unit, a, b, c);
    }
}
