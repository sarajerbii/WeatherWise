import { Component } from '@angular/core';
import { TripsComponent } from './features/trips/trips.component';

@Component({
  selector: 'ww-app',
  standalone: true,
  imports: [TripsComponent],
  template: `
    <main class="app-shell">
      <ww-trips></ww-trips>
    </main>
  `,
})
export class AppComponent {}

