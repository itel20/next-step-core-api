import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IConseiller } from '../conseiller.model';

@Component({
  standalone: true,
  selector: 'jhi-conseiller-detail',
  templateUrl: './conseiller-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class ConseillerDetailComponent {
  conseiller = input<IConseiller | null>(null);

  previousState(): void {
    window.history.back();
  }
}
