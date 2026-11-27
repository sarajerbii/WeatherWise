import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'ww-error-state', standalone: true,
  template: `<div class="ww-glass-panel rounded-3xl p-6"><p>{{ message }}</p><button type="button" (click)="retry.emit()" style="margin-top:12px;padding:8px 14px;border-radius:10px">Réessayer</button></div>`,
})
export class ErrorStateComponent { @Input() message = ''; @Output() retry = new EventEmitter<void>(); }

