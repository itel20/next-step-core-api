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
import { PublicationLikeService } from '../service/publication-like.service';
import { IPublicationLike } from '../publication-like.model';
import { PublicationLikeFormGroup, PublicationLikeFormService } from './publication-like-form.service';

@Component({
  standalone: true,
  selector: 'jhi-publication-like-update',
  templateUrl: './publication-like-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PublicationLikeUpdateComponent implements OnInit {
  isSaving = false;
  publicationLike: IPublicationLike | null = null;
  userTypeValues = Object.keys(UserType);

  publicationsSharedCollection: IPublication[] = [];

  protected publicationLikeService = inject(PublicationLikeService);
  protected publicationLikeFormService = inject(PublicationLikeFormService);
  protected publicationService = inject(PublicationService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PublicationLikeFormGroup = this.publicationLikeFormService.createPublicationLikeFormGroup();

  comparePublication = (o1: IPublication | null, o2: IPublication | null): boolean => this.publicationService.comparePublication(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ publicationLike }) => {
      this.publicationLike = publicationLike;
      if (publicationLike) {
        this.updateForm(publicationLike);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const publicationLike = this.publicationLikeFormService.getPublicationLike(this.editForm);
    if (publicationLike.id !== null) {
      this.subscribeToSaveResponse(this.publicationLikeService.update(publicationLike));
    } else {
      this.subscribeToSaveResponse(this.publicationLikeService.create(publicationLike));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPublicationLike>>): void {
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

  protected updateForm(publicationLike: IPublicationLike): void {
    this.publicationLike = publicationLike;
    this.publicationLikeFormService.resetForm(this.editForm, publicationLike);

    this.publicationsSharedCollection = this.publicationService.addPublicationToCollectionIfMissing<IPublication>(
      this.publicationsSharedCollection,
      publicationLike.publication,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.publicationService
      .query()
      .pipe(map((res: HttpResponse<IPublication[]>) => res.body ?? []))
      .pipe(
        map((publications: IPublication[]) =>
          this.publicationService.addPublicationToCollectionIfMissing<IPublication>(publications, this.publicationLike?.publication),
        ),
      )
      .subscribe((publications: IPublication[]) => (this.publicationsSharedCollection = publications));
  }
}
