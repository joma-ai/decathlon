import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { EventDto, PointsRequest, PointsResponse } from "./models";

@Injectable({ providedIn: "root" })
export class ApiService {
  private readonly baseUrl = "/api";

  constructor(private http: HttpClient) {}

  getEvents(): Observable<EventDto[]> {
    return this.http.get<EventDto[]>(`${this.baseUrl}/events`);
  }

  calculatePoints(req: PointsRequest): Observable<PointsResponse> {
    return this.http.post<PointsResponse>(`${this.baseUrl}/points`, req);
  }
}
