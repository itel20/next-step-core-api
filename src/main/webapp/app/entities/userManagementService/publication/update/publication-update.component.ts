import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { UserType } from 'app/entities/enumerations/user-type.model';
import { PublicationService } from '../service/publication.service';
import { IPublication } from '../publication.model';
import { PublicationFormGroup, PublicationFormService } from './publication-form.service';

@Component({
  standalone: true,
  selector: 'jhi-publication-update',
  templateUrl: './publication-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PublicationUpdateComponent implements OnInit {
  isSaving = false;
  publication: IPublication | null = null;
  userTypeValues = Object.keys(UserType);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected publicationService = inject(PublicationService);
  protected publicationFormService = inject(PublicationFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PublicationFormGroup = this.publicationFormService.createPublicationFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ publication }) => {
      this.publication = publication;
      if (publication) {
        this.updateForm(publication);
      }
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(
          new EventWithContent<AlertError>('userManagementServiceApp.error', { ...err, key: `error.file.${err.key}` }),
        ),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const publication = this.publicationFormService.getPublication(this.editForm);
    if (publication.id !== null) {
      this.subscribeToSaveResponse(this.publicationService.update(publication));
    } else {
      this.subscribeToSaveResponse(this.publicationService.create(publication));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPublication>>): void {
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

  protected updateForm(publication: IPublication): void {
    this.publication = publication;
    this.publicationFormService.resetForm(this.editForm, publication);
  }
}
