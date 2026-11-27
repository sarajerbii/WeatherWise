import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { City } from '../models/city.model';

export interface CityFavorite { id: number; city: City; }

@Injectable({ providedIn: 'root' })
export class CityFavoritesService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.gatewayUrl}${environment.apiRoutes['cities']}`;

  getFavorites(): Observable<CityFavorite[]> {
    return this.http.get<CityFavorite[]>(`${this.baseUrl}/favorites`).pipe(
      catchError(() => this.http.get<City[]>(this.baseUrl).pipe(
        map((cities) => cities.map((city, index) => ({ id: index + 1, city }))),
        catchError(() => of([
          { id: 1, city: { id: 1, name: 'Tunis', country: 'Tunisie' } },
          { id: 2, city: { id: 3, name: 'Paris', country: 'France' } },
        ]))
      ))
    );
  }
}
