import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IEleve } from '../eleve.model';

@Component({
  standalone: true,
  selector: 'jhi-eleve-detail',
  templateUrl: './eleve-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class EleveDetailComponent {
  eleve = input<IEleve | null>(null);

  previousState(): void {
    window.history.back();
  }
}
