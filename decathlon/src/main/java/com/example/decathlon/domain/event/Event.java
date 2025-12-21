package com.example.decathlon.domain.event;

import jakarta.persistence.*;

@Entity
@Table(name = "event")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Event {

    @Id
    @Column(name = "event_code", length = 16)
    protected String code;

    @Column(name = "event_name", nullable = false, length = 64)
    protected String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false, length = 16)
    protected Unit unit;

    @Column(name = "a", nullable = false)
    protected double a;

    @Column(name = "b", nullable = false)
    protected double b;

    @Column(name = "c", nullable = false)
    protected double c;

    protected Event() {}

    protected Event(String code, String name, Unit unit, double a, double b, double c) {
        this.code = code;
        this.name = name;
        this.unit = unit;
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Unit getUnit() {
        return unit;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }
}
