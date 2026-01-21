import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPublicationShare } from '../publication-share.model';
import { PublicationShareService } from '../service/publication-share.service';

@Component({
  standalone: true,
  templateUrl: './publication-share-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PublicationShareDeleteDialogComponent {
  publicationShare?: IPublicationShare;

  protected publicationShareService = inject(PublicationShareService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.publicationShareService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
