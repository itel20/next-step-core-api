import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IPublicationShare } from '../publication-share.model';

@Component({
  standalone: true,
  selector: 'jhi-publication-share-detail',
  templateUrl: './publication-share-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class PublicationShareDetailComponent {
  publicationShare = input<IPublicationShare | null>(null);

  previousState(): void {
    window.history.back();
  }
}
