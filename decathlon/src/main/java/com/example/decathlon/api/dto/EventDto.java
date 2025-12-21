package com.example.decathlon.api.dto;

import com.example.decathlon.domain.event.Unit;

public record EventDto(
        String eventCode,
        String eventName,
        Unit unit
) {}
