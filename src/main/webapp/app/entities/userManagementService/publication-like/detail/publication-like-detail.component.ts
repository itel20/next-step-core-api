import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IPublicationLike } from '../publication-like.model';

@Component({
  standalone: true,
  selector: 'jhi-publication-like-detail',
  templateUrl: './publication-like-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class PublicationLikeDetailComponent {
  publicationLike = input<IPublicationLike | null>(null);

  previousState(): void {
    window.history.back();
  }
}
