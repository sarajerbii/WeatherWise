import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  Trip,
  TripRequest,
  TripStop,
  TripStopRequest,
  TripSummaryResponse,
} from '../models/trip.model';

@Injectable({ providedIn: 'root' })
export class TripPlannerService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiRoutes['trips'];

  /** POST /api/trips */
  createTrip(trip: TripRequest): Observable<Trip> {
    return this.http.post<Trip>(this.baseUrl, trip);
  }

  /** GET /api/trips */
  getMyTrips(): Observable<Trip[]> {
    return this.http.get<Trip[]>(this.baseUrl);
  }

  /** GET /api/trips/{id} */
  getTripById(id: number): Observable<Trip> {
    return this.http.get<Trip>(`${this.baseUrl}/${id}`);
  }

  /** PUT /api/trips/{id} */
  updateTrip(id: number, trip: TripRequest): Observable<Trip> {
    return this.http.put<Trip>(`${this.baseUrl}/${id}`, trip);
  }

  /** DELETE /api/trips/{id} */
  deleteTrip(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  /** POST /api/trips/{id}/stops */
  addStop(tripId: number, stop: TripStopRequest): Observable<TripStop> {
    return this.http.post<TripStop>(`${this.baseUrl}/${tripId}/stops`, stop);
  }

  /** PUT /api/trips/{id}/stops/{stopId} */
  updateStop(tripId: number, stopId: number, stop: TripStopRequest): Observable<TripStop> {
    return this.http.put<TripStop>(`${this.baseUrl}/${tripId}/stops/${stopId}`, stop);
  }

  /** DELETE /api/trips/{id}/stops/{stopId} */
  deleteStop(tripId: number, stopId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${tripId}/stops/${stopId}`);
  }

  /** GET /api/trips/{id}/summary */
  getTripSummary(tripId: number): Observable<TripSummaryResponse> {
    return this.http.get<TripSummaryResponse>(`${this.baseUrl}/${tripId}/summary`);
  }
}
