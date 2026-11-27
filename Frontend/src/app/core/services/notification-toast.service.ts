import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class NotificationToastService {
  success(title: string, detail?: string): void {
    console.info(detail ? `${title}: ${detail}` : title);
  }
}

