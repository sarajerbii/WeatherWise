import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Calendar, Compass, MapPin, Pencil, Plus, Trash2 } from 'lucide-angular';
import { LucideAngularModule } from 'lucide-angular';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { NotificationToastService } from '../../core/services/notification-toast.service';
import { City } from '../../models/city.model';
import { TripStatus } from '../../models/enums';
import { Trip, TripStop, TripSummaryResponse } from '../../models/trip.model';
import { CityFavoritesService } from '../../services/city-favorites.service';
import { TripPlannerService } from '../../services/trip-planner.service';
import { EmptyStateComponent } from '../../shared/components/empty-state.component';
import { ErrorStateComponent } from '../../shared/components/error-state.component';
import { LoadingSkeletonComponent } from '../../shared/components/loading-skeleton.component';
import { RiskLabelComponent } from '../../shared/components/risk-label.component';

@Component({
  selector: 'ww-trips',
  standalone: true,
  imports: [
    DatePipe,
    ReactiveFormsModule,
    LucideAngularModule,
    LoadingSkeletonComponent,
    ErrorStateComponent,
    EmptyStateComponent,
    RiskLabelComponent,
  ],
  template: `
    <div class="space-y-6">
      <div>
        <p class="text-xs font-semibold uppercase tracking-wider text-teal-300">Itinéraires & Prévisions</p>
        <h1 class="mt-1 font-display text-2xl font-extrabold text-white sm:text-3xl">Planification des voyages</h1>
        <p class="mt-1 text-sm text-sky-100/80">
          Gestion multi-étapes avec résumé météo, risque et recommandations (<code class="font-mono text-xs text-teal-200">/api/trips</code>)
        </p>
      </div>

      @if (loading()) {
        <ww-loading-skeleton [rows]="4"></ww-loading-skeleton>
      } @else if (errorMessage()) {
        <ww-error-state [message]="errorMessage()!" (retry)="loadTrips()"></ww-error-state>
      } @else {
        <div class="grid gap-6 lg:grid-cols-12">
          <!-- Create Trip Form -->
          <form [formGroup]="tripForm" (ngSubmit)="createTrip()" class="ww-glass-panel h-fit rounded-3xl p-6 lg:col-span-4">
            <h2 class="font-display text-base font-bold text-white">Nouveau voyage</h2>
            <div class="mt-4 space-y-3.5">
              <div>
                <label class="block text-xs font-semibold text-sky-200">Titre</label>
                <input type="text" formControlName="title" placeholder="Ex: Tournée Méditerranée" class="ww-glass-input mt-1 w-full rounded-xl px-3 py-2 text-sm" />
              </div>
              <div>
                <label class="block text-xs font-semibold text-sky-200">Description</label>
                <textarea formControlName="description" rows="2" class="ww-glass-input mt-1 w-full rounded-xl px-3 py-2 text-sm"></textarea>
              </div>
              <div class="grid grid-cols-2 gap-2.5">
                <div>
                  <label class="block text-xs font-semibold text-sky-200">Début</label>
                  <input type="date" formControlName="startDate" class="ww-glass-input mt-1 w-full rounded-xl px-2.5 py-2 text-xs font-mono-tabular" />
                </div>
                <div>
                  <label class="block text-xs font-semibold text-sky-200">Fin</label>
                  <input type="date" formControlName="endDate" class="ww-glass-input mt-1 w-full rounded-xl px-2.5 py-2 text-xs font-mono-tabular" />
                </div>
              </div>
              <div>
                <label class="block text-xs font-semibold text-sky-200">Statut</label>
                <select formControlName="status" class="ww-glass-input mt-1 w-full rounded-xl px-3 py-2 text-sm">
                  @for (st of statuses; track st) {
                    <option [value]="st">{{ st }}</option>
                  }
                </select>
              </div>
              <button
                type="submit"
                [disabled]="tripForm.invalid"
                class="inline-flex w-full items-center justify-center gap-1.5 rounded-xl bg-gradient-to-r from-cyan-400 to-teal-600 px-4 py-2.5 text-sm font-semibold text-white shadow-md transition-all duration-200 hover:-translate-y-0.5 hover:brightness-110 disabled:opacity-50"
              >
                <lucide-icon [img]="PlusIcon" class="h-4 w-4"></lucide-icon>
                Créer le voyage
              </button>
            </div>
          </form>

          <!-- Trips List & Detail -->
          <div class="space-y-6 lg:col-span-8">
            @if (trips().length === 0) {
              <ww-empty-state title="Aucun voyage planifié" description="Créez votre premier voyage à gauche puis ajoutez vos villes d’étape."></ww-empty-state>
            } @else {
              @for (trip of trips(); track trip.id) {
                <div class="ww-glass-panel rounded-3xl p-6 transition-all duration-200">
                  <div class="flex flex-wrap items-start justify-between gap-4 border-b border-white/15 pb-4">
                    <div>
                      <div class="flex items-center gap-2 text-xs text-sky-200/80">
                        <span class="font-bold text-teal-300">{{ trip.status }}</span>
                        <span>·</span>
                        <span class="font-mono-tabular">{{ trip.startDate | date: 'dd/MM/yyyy' }} → {{ trip.endDate | date: 'dd/MM/yyyy' }}</span>
                      </div>
                      <h3 class="mt-1 font-display text-lg font-bold text-white">{{ trip.title }}</h3>
                      @if (trip.description) {
                        <p class="mt-1 text-sm text-sky-100/80">{{ trip.description }}</p>
                      }
                    </div>

                    <div class="flex items-center gap-2">
                      <button
                        type="button"
                        (click)="editTrip(trip)"
                        class="rounded-lg p-2 text-sky-200 hover:bg-sky-500/25 hover:text-sky-100"
                        title="Modifier le voyage"
                      >
                        <lucide-icon [img]="EditIcon" class="h-4 w-4"></lucide-icon>
                      </button>
                      <button
                        type="button"
                        (click)="loadSummary(trip.id)"
                        class="rounded-xl bg-gradient-to-r from-sky-400 to-blue-600 px-3.5 py-2 text-xs font-semibold text-white shadow-sm transition-all hover:brightness-110"
                      >
                        Analyser météo & risques
                      </button>
                      <button
                        type="button"
                        (click)="deleteTrip(trip.id)"
                        class="rounded-lg p-2 text-sky-200 hover:bg-rose-500/25 hover:text-rose-200"
                        title="Supprimer le voyage"
                      >
                        <lucide-icon [img]="TrashIcon" class="h-4 w-4"></lucide-icon>
                      </button>
                    </div>
                  </div>

                  <!-- Stops -->
                  <div class="mt-4 space-y-3">
                    <h4 class="text-xs font-semibold uppercase tracking-wide text-sky-200/75">
                      Étapes ({{ trip.stops.length }})
                    </h4>

                    @for (stop of trip.stops; track stop.id) {
                      <div class="flex flex-col gap-2 rounded-2xl border border-white/15 bg-white/5 p-4 sm:flex-row sm:items-center sm:justify-between">
                        <div>
                          <p class="flex items-center gap-1.5 text-sm font-bold text-white">
                            <lucide-icon [img]="PinIcon" class="h-4 w-4 text-teal-300"></lucide-icon>
                            {{ stop.city?.name || stop.cityName || ('Ville #' + stop.cityId) }}
                          </p>
                          <p class="mt-0.5 font-mono-tabular text-xs text-sky-200/80">
                            {{ stop.arrivalDate }} → {{ stop.departureDate }}
                          </p>
                          @if (stop.weatherSummary) {
                            <p class="mt-1 text-xs text-sky-100/90">{{ stop.weatherSummary }}</p>
                          }
                        </div>
                        <div class="flex items-center gap-3 text-right">
                          <div>
                            <span class="text-xs text-sky-200/80">Risque : </span>
                            <ww-risk-label [level]="stop.riskLevel"></ww-risk-label>
                          </div>
                          <button
                            type="button"
                            (click)="editStop(trip.id, stop)"
                            class="rounded-lg p-1.5 text-sky-200 hover:bg-sky-500/25 hover:text-sky-100"
                            title="Modifier l'étape"
                          >
                            <lucide-icon [img]="EditIcon" class="h-4 w-4"></lucide-icon>
                          </button>
                          <button
                            type="button"
                            (click)="deleteStop(trip.id, stop.id)"
                            class="rounded-lg p-1.5 text-sky-200 hover:bg-rose-500/25 hover:text-rose-200"
                            title="Supprimer l'étape"
                          >
                            <lucide-icon [img]="TrashIcon" class="h-4 w-4"></lucide-icon>
                          </button>
                        </div>
                      </div>
                    }

                    <!-- Add Stop Inline Form -->
                    <form [formGroup]="stopForm" (ngSubmit)="addStop(trip.id)" class="mt-3 flex flex-wrap items-end gap-2 border-t border-white/15 pt-3">
                      <select formControlName="cityId" class="ww-glass-input rounded-xl px-3 py-1.5 text-xs">
                        @for (c of stopCityOptions(); track c.id) {
                          <option [value]="c.id">{{ c.name }} ({{ c.country }})</option>
                        }
                      </select>
                      <input type="date" formControlName="arrivalDate" class="ww-glass-input rounded-xl px-2.5 py-1.5 text-xs font-mono-tabular" />
                      <input type="date" formControlName="departureDate" class="ww-glass-input rounded-xl px-2.5 py-1.5 text-xs font-mono-tabular" />
                      <button
                        type="submit"
                        [disabled]="stopForm.invalid"
                        class="rounded-xl bg-gradient-to-r from-cyan-400 to-teal-600 px-3.5 py-1.5 text-xs font-semibold text-white shadow-sm hover:brightness-110"
                      >
                        + Ajouter étape
                      </button>
                    </form>
                  </div>

                  <!-- Summary Panel -->
                  @if (summaries()[trip.id]; as summary) {
                    <div class="mt-4 rounded-2xl border border-violet-300/30 bg-violet-500/15 p-4">
                      <div class="flex items-center justify-between text-xs font-bold text-violet-200">
                        <span>Synthèse météo & recommandations du voyage</span>
                        <span>Risque global : <ww-risk-label [level]="summary.overallRiskLevel"></ww-risk-label></span>
                      </div>
                      <div class="mt-2 space-y-1.5 text-xs text-sky-100">
                        @for (item of summary.stopsSummary; track $index) {
                          <p>
                            <strong class="text-white">{{ item.stop.city?.name || item.stop.cityName || 'Étape' }} :</strong>
                            {{ item.weatherSummary }}
                          </p>
                        }
                      </div>
                    </div>
                  }
                </div>
              }
            }
          </div>
        </div>
      }
    </div>
  `,
})
export class TripsComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly tripService = inject(TripPlannerService);
  private readonly cityService = inject(CityFavoritesService);
  private readonly toast = inject(NotificationToastService);

  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);
  readonly trips = signal<Trip[]>([]);
  readonly cities = signal<City[]>([]);
  readonly summaries = signal<Record<number, TripSummaryResponse>>({});
  private readonly defaultStopCities: City[] = [
    { id: 1, name: 'Tunis', country: 'Tunisie' },
    { id: 3, name: 'Paris', country: 'France' },
  ];

  readonly statuses = Object.values(TripStatus);

  readonly CompassIcon = Compass;
  readonly CalendarIcon = Calendar;
  readonly PinIcon = MapPin;
  readonly EditIcon = Pencil;
  readonly PlusIcon = Plus;
  readonly TrashIcon = Trash2;

  readonly tripForm = this.fb.nonNullable.group({
    title: ['', [Validators.required, Validators.minLength(2)]],
    description: [''],
    startDate: [new Date().toISOString().slice(0, 10), Validators.required],
    endDate: [new Date(Date.now() + 3 * 86400000).toISOString().slice(0, 10), Validators.required],
    status: [TripStatus.PLANNED, Validators.required],
  });

  readonly stopForm = this.fb.nonNullable.group({
    cityId: [1, Validators.required],
    arrivalDate: [new Date().toISOString().slice(0, 10), Validators.required],
    departureDate: [new Date(Date.now() + 86400000).toISOString().slice(0, 10), Validators.required],
  });

  ngOnInit(): void {
    this.loadTrips();
  }

  loadTrips(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    forkJoin({
      trips: this.tripService.getMyTrips(),
      favorites: this.cityService.getFavorites().pipe(catchError(() => of([]))),
    }).subscribe({
      next: ({ trips, favorites }) => {
        this.trips.set(trips);
        this.cities.set(favorites.map((f) => f.city));
        this.ensureStopCitySelection();
        this.loading.set(false);
      },
      error: (err) => {
        this.trips.set([]);
        this.errorMessage.set(this.describeHttpError(err, 'Impossible de charger les voyages depuis le Trip-Planner-Service.'));
        this.loading.set(false);
      },
    });
  }

  createTrip(): void {
    if (this.tripForm.invalid) return;
    this.tripService.createTrip(this.tripForm.getRawValue()).subscribe({
      next: (created) => {
        this.trips.update((list) => [created, ...list]);
        this.tripForm.patchValue({ title: '', description: '' });
        this.toast.success('Voyage créé', created.title);
      },
      error: (err) => {
        console.error('Échec création voyage', err);
      },
    });
  }

  addStop(tripId: number): void {
    if (this.stopForm.invalid) return;
    const raw = this.stopForm.getRawValue();
    const cityId = Number(raw.cityId);
    const city = this.stopCityOptions().find((c) => c.id === cityId);
    if (!city) {
      console.error('Ville non trouvée pour cityId', cityId);
      return;
    }

    this.tripService
      .addStop(tripId, {
        cityId,
        cityName: city.name,
        country: city.country,
        arrivalDate: raw.arrivalDate,
        departureDate: raw.departureDate,
      })
      .subscribe({
        next: (stop) => {
          this.trips.update((list) =>
            list.map((t) => (t.id === tripId ? { ...t, stops: [...(t.stops || []), stop] } : t))
          );
          this.toast.success('Étape ajoutée au voyage');
        },
        error: (err) => {
          console.error('Échec ajout étape', err);
        },
      });
  }

  editTrip(trip: Trip): void {
    const title = window.prompt('Titre du voyage', trip.title)?.trim();
    if (!title) {
      return;
    }
    const descriptionInput = window.prompt('Description du voyage', trip.description ?? '');
    if (descriptionInput === null) {
      return;
    }

    this.tripService
      .updateTrip(trip.id, {
        title,
        description: descriptionInput,
        startDate: trip.startDate,
        endDate: trip.endDate,
        status: trip.status,
      })
      .subscribe({
        next: (updated) => {
          this.trips.update((list) => list.map((t) => (t.id === trip.id ? updated : t)));
          this.toast.success('Voyage modifié', updated.title);
        },
        error: (err) => {
          console.error('Échec modification voyage', err);
        },
      });
  }

  editStop(tripId: number, stop: TripStop): void {
    const currentCityName = stop.cityName || stop.city?.name || '';
    const cityName = window.prompt('Nom de la ville', currentCityName)?.trim();
    if (!cityName) {
      return;
    }

    const countryInput = window.prompt('Pays', stop.country ?? '');
    if (countryInput === null) {
      return;
    }

    this.tripService
      .updateStop(tripId, stop.id, {
        cityId: stop.cityId ?? 0,
        cityName,
        country: countryInput,
        arrivalDate: stop.arrivalDate,
        departureDate: stop.departureDate,
      })
      .subscribe({
        next: (updated) => {
          this.trips.update((list) =>
            list.map((t) =>
              t.id === tripId ? { ...t, stops: t.stops.map((s) => (s.id === stop.id ? updated : s)) } : t
            )
          );
          this.toast.success('Étape modifiée');
        },
        error: (err) => {
          console.error('Échec modification étape', err);
        },
      });
  }

  loadSummary(tripId: number): void {
    this.tripService.getTripSummary(tripId).subscribe({
      next: (summary) => {
        this.summaries.update((m) => ({ ...m, [tripId]: summary }));
      },
      error: (err) => {
        console.error('Échec chargement synthèse voyage', err);
      },
    });
  }

  deleteStop(tripId: number, stopId: number): void {
    this.tripService.deleteStop(tripId, stopId).subscribe({
      next: () => {
        this.trips.update((list) =>
          list.map((t) => (t.id === tripId ? { ...t, stops: t.stops.filter((s) => s.id !== stopId) } : t))
        );
        this.toast.success('Étape supprimée');
      },
      error: (err) => {
        console.error('Échec suppression étape', err);
      },
    });
  }

  deleteTrip(id: number): void {
    this.tripService.deleteTrip(id).subscribe({
      next: () => {
        this.trips.update((list) => list.filter((t) => t.id !== id));
        this.toast.success('Voyage supprimé');
      },
      error: (err) => {
        console.error('Échec suppression voyage', err);
      },
    });
  }

  stopCityOptions(): City[] {
    return this.cities().length > 0 ? this.cities() : this.defaultStopCities;
  }

  private ensureStopCitySelection(): void {
    const options = this.stopCityOptions();
    if (options.length === 0) {
      return;
    }
    const selected = Number(this.stopForm.controls.cityId.value);
    if (!options.some((city) => city.id === selected)) {
      this.stopForm.patchValue({ cityId: options[0].id });
    }
  }

  private describeHttpError(error: unknown, fallback: string): string {
    if (error instanceof HttpErrorResponse) {
      const details = typeof error.error === 'string'
        ? error.error
        : error.error?.message || error.message;
      return `${fallback} (HTTP ${error.status || 0}${details ? ` - ${details}` : ''})`;
    }
    return fallback;
  }
}
