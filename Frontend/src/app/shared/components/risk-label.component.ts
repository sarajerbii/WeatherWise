import { Component, Input } from '@angular/core';
import { RiskLevel } from '../../models/enums';

@Component({
  selector: 'ww-risk-label', standalone: true,
  template: `<span style="display:inline-block;padding:2px 8px;border-radius:999px;background:#334155;color:white;font-size:11px">{{ level || 'INCONNU' }}</span>`,
})
export class RiskLabelComponent { @Input() level?: RiskLevel; }

