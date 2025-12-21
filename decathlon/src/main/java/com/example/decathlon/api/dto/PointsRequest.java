package com.example.decathlon.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PointsRequest(
        @NotBlank String eventCode,
        @NotNull @Positive Double result
) {}
