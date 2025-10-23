import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IConseiller } from '../conseiller.model';
import { ConseillerService } from '../service/conseiller.service';

@Component({
  standalone: true,
  templateUrl: './conseiller-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ConseillerDeleteDialogComponent {
  conseiller?: IConseiller;

  protected conseillerService = inject(ConseillerService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.conseillerService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
