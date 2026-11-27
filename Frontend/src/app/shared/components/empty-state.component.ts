import { Component, Input } from '@angular/core';

@Component({ selector: 'ww-empty-state', standalone: true, template: `<div class="ww-glass-panel rounded-3xl p-6"><h2 style="margin:0">{{ title }}</h2><p>{{ description }}</p></div>` })
export class EmptyStateComponent { @Input() title = ''; @Input() description = ''; }

