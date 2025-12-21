package com.example.decathlon.api;

import com.example.decathlon.api.dto.EventDto;
import com.example.decathlon.api.dto.PointsRequest;
import com.example.decathlon.api.dto.PointsResponse;
import com.example.decathlon.service.EventQueryService;
import com.example.decathlon.service.PointsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PointsController {

    private final PointsService pointsService;
    private final EventQueryService eventQueryService;

    public PointsController(PointsService pointsService, EventQueryService eventQueryService) {
        this.pointsService = pointsService;
        this.eventQueryService = eventQueryService;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventDto>> events() {
        return ResponseEntity.ok(eventQueryService.listEvents());
    }

    @PostMapping("/points")
    public ResponseEntity<PointsResponse> points(@Valid @RequestBody PointsRequest req) {
        return ResponseEntity.ok(pointsService.calculate(req));
    }
}
