import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPublication, NewPublication } from '../publication.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPublication for edit and NewPublicationFormGroupInput for create.
 */
type PublicationFormGroupInput = IPublication | PartialWithRequiredKeyOf<NewPublication>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPublication | NewPublication> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

type PublicationFormRawValue = FormValueOf<IPublication>;

type NewPublicationFormRawValue = FormValueOf<NewPublication>;

type PublicationFormDefaults = Pick<NewPublication, 'id' | 'createdAt'>;

type PublicationFormGroupContent = {
  id: FormControl<PublicationFormRawValue['id'] | NewPublication['id']>;
  content: FormControl<PublicationFormRawValue['content']>;
  authorId: FormControl<PublicationFormRawValue['authorId']>;
  authorType: FormControl<PublicationFormRawValue['authorType']>;
  createdAt: FormControl<PublicationFormRawValue['createdAt']>;
};

export type PublicationFormGroup = FormGroup<PublicationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PublicationFormService {
  createPublicationFormGroup(publication: PublicationFormGroupInput = { id: null }): PublicationFormGroup {
    const publicationRawValue = this.convertPublicationToPublicationRawValue({
      ...this.getFormDefaults(),
      ...publication,
    });
    return new FormGroup<PublicationFormGroupContent>({
      id: new FormControl(
        { value: publicationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      content: new FormControl(publicationRawValue.content, {
        validators: [Validators.required],
      }),
      authorId: new FormControl(publicationRawValue.authorId, {
        validators: [Validators.required],
      }),
      authorType: new FormControl(publicationRawValue.authorType, {
        validators: [Validators.required],
      }),
      createdAt: new FormControl(publicationRawValue.createdAt, {
        validators: [Validators.required],
      }),
    });
  }

  getPublication(form: PublicationFormGroup): IPublication | NewPublication {
    return this.convertPublicationRawValueToPublication(form.getRawValue() as PublicationFormRawValue | NewPublicationFormRawValue);
  }

  resetForm(form: PublicationFormGroup, publication: PublicationFormGroupInput): void {
    const publicationRawValue = this.convertPublicationToPublicationRawValue({ ...this.getFormDefaults(), ...publication });
    form.reset(
      {
        ...publicationRawValue,
        id: { value: publicationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PublicationFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
    };
  }

  private convertPublicationRawValueToPublication(
    rawPublication: PublicationFormRawValue | NewPublicationFormRawValue,
  ): IPublication | NewPublication {
    return {
      ...rawPublication,
      createdAt: dayjs(rawPublication.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertPublicationToPublicationRawValue(
    publication: IPublication | (Partial<NewPublication> & PublicationFormDefaults),
  ): PublicationFormRawValue | PartialWithRequiredKeyOf<NewPublicationFormRawValue> {
    return {
      ...publication,
      createdAt: publication.createdAt ? publication.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
