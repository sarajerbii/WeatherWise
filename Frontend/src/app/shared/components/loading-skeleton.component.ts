import { Component, Input } from '@angular/core';

@Component({ selector: 'ww-loading-skeleton', standalone: true, template: `<div class="ww-glass-panel rounded-3xl p-6"><p>Chargement des voyages…</p></div>` })
export class LoadingSkeletonComponent { @Input() rows = 4; }

