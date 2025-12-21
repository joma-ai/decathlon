package com.example.decathlon.domain.event;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FIELD")
public class FieldEvent extends Event {

    protected FieldEvent() {
    }

    protected FieldEvent(String code, String name, Unit unit, double a, double b, double c) {
        super(code, name, unit, a, b, c);
    }
}
