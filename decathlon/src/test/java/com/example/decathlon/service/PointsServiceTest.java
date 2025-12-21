package com.example.decathlon.service;

import com.example.decathlon.api.dto.PointsRequest;
import com.example.decathlon.domain.event.FieldEvent;
import com.example.decathlon.domain.event.TrackEvent;
import com.example.decathlon.domain.event.Unit;
import com.example.decathlon.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PointsServiceEdgeCasesTest {

    record Case(String code, boolean track, Unit unit, double a, double b, double c, double result, int expectedPoints) {}

    private static Stream<Case> normalCases() {
        return Stream.of(
                new Case("M100",  true,  Unit.SECONDS,      25.4347, 18.0,  1.81,  11.00, 861),
                new Case("LJ",    false, Unit.CENTIMETERS,  0.14354, 220.0, 1.40, 700.00, 814),
                new Case("SP",    false, Unit.METERS,       51.39,   1.5,   1.05,  14.00, 728),
                new Case("HJ",    false, Unit.CENTIMETERS,  0.8465,  75.0,  1.42, 200.00, 803),
                new Case("M400",  true,  Unit.SECONDS,      1.53775, 82.0,  1.81,  49.00, 861),
                new Case("M110H", true,  Unit.SECONDS,      5.74352, 28.5,  1.92,  15.00, 850),
                new Case("DT",    false, Unit.METERS,       12.91,   4.0,   1.10,  45.00, 767),
                new Case("PV",    false, Unit.CENTIMETERS,  0.2797,  100.0, 1.35, 500.00, 910),
                new Case("JT",    false, Unit.METERS,       10.14,   7.0,   1.08,  60.00, 738),
                new Case("M1500", true,  Unit.SECONDS,      0.03768, 480.0, 1.85, 270.00, 745)
        );
    }

    private static Stream<Case> zeroPointCases() {
        return Stream.of(
                new Case("M100",  true,  Unit.SECONDS,      25.4347, 18.0,  1.81,  18.00, 0),  // B - P = 0
                new Case("LJ",    false, Unit.CENTIMETERS,  0.14354, 220.0, 1.40, 220.00, 0),  // P - B = 0
                new Case("SP",    false, Unit.METERS,       51.39,   1.5,   1.05,   1.50, 0),
                new Case("M1500", true,  Unit.SECONDS,      0.03768, 480.0, 1.85, 480.00, 0)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("normalCases")
    @DisplayName("Normal values: assert exact points for each event (verifies coefficients + formula)")
    void normalValues_assertExactPoints(Case tc) {
        EventRepository repo = mock(EventRepository.class);

        if (tc.track) {
            TrackEvent e = mock(TrackEvent.class);
            stubCommon(e, tc);
            when(repo.findById(tc.code)).thenReturn(Optional.of(e));
        } else {
            FieldEvent e = mock(FieldEvent.class);
            stubCommon(e, tc);
            when(repo.findById(tc.code)).thenReturn(Optional.of(e));
        }

        PointsService service = new PointsService(repo);

        var res = service.calculate(new PointsRequest(tc.code, tc.result));
        assertEquals(tc.expectedPoints, res.points(), "Points mismatch for " + tc.code);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("zeroPointCases")
    @DisplayName("Base <= 0 yields 0 points")
    void baseNonPositive_yieldsZero(Case tc) {
        EventRepository repo = mock(EventRepository.class);

        if (tc.track) {
            TrackEvent e = mock(TrackEvent.class);
            stubCommon(e, tc);
            when(repo.findById(tc.code)).thenReturn(Optional.of(e));
        } else {
            FieldEvent e = mock(FieldEvent.class);
            stubCommon(e, tc);
            when(repo.findById(tc.code)).thenReturn(Optional.of(e));
        }

        PointsService service = new PointsService(repo);

        var res = service.calculate(new PointsRequest(tc.code, tc.result));
        assertEquals(0, res.points());
    }

    @Test
    @DisplayName("Superhuman performance returns a large finite integer (no overflow/NaN)")
    void superhuman_returnsLargeFinite() {
        EventRepository repo = mock(EventRepository.class);

        TrackEvent m100 = mock(TrackEvent.class);
        stubCommon(m100, new Case("M100", true, Unit.SECONDS, 25.4347, 18.0, 1.81, 9.00, 0));
        when(repo.findById("M100")).thenReturn(Optional.of(m100));

        PointsService service = new PointsService(repo);

        var res = service.calculate(new PointsRequest("M100", 9.00));
        assertTrue(res.points() > 1200, "Expected very high points for 9.00s");
    }

    @Test
    @DisplayName("Wrong unit: LJ entered as meters (7.20) should be rejected by plausibility validation")
    void wrongUnitRejected() {
        EventRepository repo = mock(EventRepository.class);

        FieldEvent lj = mock(FieldEvent.class);
        stubCommon(lj, new Case("LJ", false, Unit.CENTIMETERS, 0.14354, 220.0, 1.40, 7.20, 0));
        when(repo.findById("LJ")).thenReturn(Optional.of(lj));

        PointsService service = new PointsService(repo);

        assertThrows(IllegalArgumentException.class,
                () -> service.calculate(new PointsRequest("LJ", 7.20)));
    }

    @Test
    @DisplayName("Negative result is rejected (even if controller validation exists)")
    void negativeRejected() {
        EventRepository repo = mock(EventRepository.class);

        TrackEvent m400 = mock(TrackEvent.class);
        stubCommon(m400, new Case("M400", true, Unit.SECONDS, 1.53775, 82.0, 1.81, -1.0, 0));
        when(repo.findById("M400")).thenReturn(Optional.of(m400));

        PointsService service = new PointsService(repo);

        assertThrows(IllegalArgumentException.class,
                () -> service.calculate(new PointsRequest("M400", -1.0)));
    }

    private static void stubCommon(TrackEvent e, Case tc) {
        when(e.getCode()).thenReturn(tc.code);
        when(e.getName()).thenReturn(tc.code);
        when(e.getUnit()).thenReturn(tc.unit);
        when(e.getA()).thenReturn(tc.a);
        when(e.getB()).thenReturn(tc.b);
        when(e.getC()).thenReturn(tc.c);
    }

    private static void stubCommon(FieldEvent e, Case tc) {
        when(e.getCode()).thenReturn(tc.code);
        when(e.getName()).thenReturn(tc.code);
        when(e.getUnit()).thenReturn(tc.unit);
        when(e.getA()).thenReturn(tc.a);
        when(e.getB()).thenReturn(tc.b);
        when(e.getC()).thenReturn(tc.c);
    }
}
