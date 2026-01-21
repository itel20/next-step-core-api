import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPublication } from 'app/entities/userManagementService/publication/publication.model';
import { PublicationService } from 'app/entities/userManagementService/publication/service/publication.service';
import { UserType } from 'app/entities/enumerations/user-type.model';
import { PublicationShareService } from '../service/publication-share.service';
import { IPublicationShare } from '../publication-share.model';
import { PublicationShareFormGroup, PublicationShareFormService } from './publication-share-form.service';

@Component({
  standalone: true,
  selector: 'jhi-publication-share-update',
  templateUrl: './publication-share-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PublicationShareUpdateComponent implements OnInit {
  isSaving = false;
  publicationShare: IPublicationShare | null = null;
  userTypeValues = Object.keys(UserType);

  publicationsSharedCollection: IPublication[] = [];

  protected publicationShareService = inject(PublicationShareService);
  protected publicationShareFormService = inject(PublicationShareFormService);
  protected publicationService = inject(PublicationService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PublicationShareFormGroup = this.publicationShareFormService.createPublicationShareFormGroup();

  comparePublication = (o1: IPublication | null, o2: IPublication | null): boolean => this.publicationService.comparePublication(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ publicationShare }) => {
      this.publicationShare = publicationShare;
      if (publicationShare) {
        this.updateForm(publicationShare);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const publicationShare = this.publicationShareFormService.getPublicationShare(this.editForm);
    if (publicationShare.id !== null) {
      this.subscribeToSaveResponse(this.publicationShareService.update(publicationShare));
    } else {
      this.subscribeToSaveResponse(this.publicationShareService.create(publicationShare));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPublicationShare>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(publicationShare: IPublicationShare): void {
    this.publicationShare = publicationShare;
    this.publicationShareFormService.resetForm(this.editForm, publicationShare);

    this.publicationsSharedCollection = this.publicationService.addPublicationToCollectionIfMissing<IPublication>(
      this.publicationsSharedCollection,
      publicationShare.publication,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.publicationService
      .query()
      .pipe(map((res: HttpResponse<IPublication[]>) => res.body ?? []))
      .pipe(
        map((publications: IPublication[]) =>
          this.publicationService.addPublicationToCollectionIfMissing<IPublication>(publications, this.publicationShare?.publication),
        ),
      )
      .subscribe((publications: IPublication[]) => (this.publicationsSharedCollection = publications));
  }
}
