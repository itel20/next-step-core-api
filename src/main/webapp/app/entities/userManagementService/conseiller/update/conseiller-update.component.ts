import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IConseiller } from '../conseiller.model';
import { ConseillerService } from '../service/conseiller.service';
import { ConseillerFormGroup, ConseillerFormService } from './conseiller-form.service';

@Component({
  standalone: true,
  selector: 'jhi-conseiller-update',
  templateUrl: './conseiller-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ConseillerUpdateComponent implements OnInit {
  isSaving = false;
  conseiller: IConseiller | null = null;

  usersSharedCollection: IUser[] = [];

  protected conseillerService = inject(ConseillerService);
  protected conseillerFormService = inject(ConseillerFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ConseillerFormGroup = this.conseillerFormService.createConseillerFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ conseiller }) => {
      this.conseiller = conseiller;
      if (conseiller) {
        this.updateForm(conseiller);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const conseiller = this.conseillerFormService.getConseiller(this.editForm);
    if (conseiller.id !== null) {
      this.subscribeToSaveResponse(this.conseillerService.update(conseiller));
    } else {
      this.subscribeToSaveResponse(this.conseillerService.create(conseiller));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IConseiller>>): void {
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

  protected updateForm(conseiller: IConseiller): void {
    this.conseiller = conseiller;
    this.conseillerFormService.resetForm(this.editForm, conseiller);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, conseiller.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.conseiller?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
