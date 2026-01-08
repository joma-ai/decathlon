import { Component, OnInit, ChangeDetectorRef } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { ApiService } from "./api.service";
import { EventDto, PointsResponse, Unit } from "./models";

@Component({
  selector: "app-root",
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.css"]
})
export class AppComponent implements OnInit {
  events: EventDto[] = [];
  selectedEventCode = "";
  selectedEvent: EventDto | null = null;

  resultInput: number | null = null;

  history: PointsResponse[] = [];
  totalPoints = 0;

  error: string | null = null;

  constructor(private api: ApiService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.api.getEvents().subscribe({
      next: events => {
        this.events = events;
        if (events.length > 0) {
          this.selectedEventCode = events[0].eventCode;
          this.onEventChange();
        }
      },
      error: () => (this.error = "Failed to load events")
    });
  }

  onEventChange(): void {
    this.selectedEvent =
      this.events.find(e => e.eventCode === this.selectedEventCode) ?? null;
    this.resultInput = null;
    this.error = null;
  }

  unitLabel(unit: Unit): string {
    switch (unit) {
      case "SECONDS":
        return "seconds";
      case "METERS":
        return "meters";
      case "CENTIMETERS":
        return "centimeters";
    }
  }

  calculate(): void {
    console.log("calculate() clicked", this.selectedEvent, this.resultInput);

    if (!this.selectedEvent || this.resultInput === null) {
      this.error = "Please select an event and enter a result";
      return;
    }

    this.api.calculatePoints({
      eventCode: this.selectedEvent.eventCode,
      result: this.resultInput
    }).subscribe({
      next: res => {
        this.history = [res, ...this.history];
        this.totalPoints = this.history.reduce((s, r) => s + r.points, 0);
        this.error = null;

        this.cdr.detectChanges();
      },
      error: err => {
        const msg =
          err?.error?.message ||
          "Enter a valid result for the selected event/unit.";
        this.error = msg;
      }
    });
  }

  clear(): void {
    this.history = [];
    this.totalPoints = 0;
    this.error = null;
  }
}
