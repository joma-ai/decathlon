export type Unit = "SECONDS" | "METERS" | "CENTIMETERS";

export interface EventDto {
  eventCode: string;
  eventName: string;
  unit: Unit;
}

export interface PointsRequest {
  eventCode: string;
  result: number;
}

export interface PointsResponse {
  eventCode: string;
  eventName: string;
  unit: Unit;
  result: number;
  points: number;
}
