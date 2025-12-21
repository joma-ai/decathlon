package com.example.decathlon.api.dto;

import com.example.decathlon.domain.event.Unit;

public record PointsResponse(
        String eventCode,
        String eventName,
        Unit unit,
        double result,
        int points
) {}
