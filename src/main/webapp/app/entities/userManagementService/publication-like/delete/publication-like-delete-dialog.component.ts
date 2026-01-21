import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPublicationLike } from '../publication-like.model';
import { PublicationLikeService } from '../service/publication-like.service';

@Component({
  standalone: true,
  templateUrl: './publication-like-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PublicationLikeDeleteDialogComponent {
  publicationLike?: IPublicationLike;

  protected publicationLikeService = inject(PublicationLikeService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.publicationLikeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
