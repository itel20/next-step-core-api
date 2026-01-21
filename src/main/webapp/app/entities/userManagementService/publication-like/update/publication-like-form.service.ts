import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPublicationLike, NewPublicationLike } from '../publication-like.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPublicationLike for edit and NewPublicationLikeFormGroupInput for create.
 */
type PublicationLikeFormGroupInput = IPublicationLike | PartialWithRequiredKeyOf<NewPublicationLike>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPublicationLike | NewPublicationLike> = Omit<T, 'likedAt'> & {
  likedAt?: string | null;
};

type PublicationLikeFormRawValue = FormValueOf<IPublicationLike>;

type NewPublicationLikeFormRawValue = FormValueOf<NewPublicationLike>;

type PublicationLikeFormDefaults = Pick<NewPublicationLike, 'id' | 'likedAt'>;

type PublicationLikeFormGroupContent = {
  id: FormControl<PublicationLikeFormRawValue['id'] | NewPublicationLike['id']>;
  userId: FormControl<PublicationLikeFormRawValue['userId']>;
  userType: FormControl<PublicationLikeFormRawValue['userType']>;
  likedAt: FormControl<PublicationLikeFormRawValue['likedAt']>;
  publication: FormControl<PublicationLikeFormRawValue['publication']>;
};

export type PublicationLikeFormGroup = FormGroup<PublicationLikeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PublicationLikeFormService {
  createPublicationLikeFormGroup(publicationLike: PublicationLikeFormGroupInput = { id: null }): PublicationLikeFormGroup {
    const publicationLikeRawValue = this.convertPublicationLikeToPublicationLikeRawValue({
      ...this.getFormDefaults(),
      ...publicationLike,
    });
    return new FormGroup<PublicationLikeFormGroupContent>({
      id: new FormControl(
        { value: publicationLikeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      userId: new FormControl(publicationLikeRawValue.userId, {
        validators: [Validators.required],
      }),
      userType: new FormControl(publicationLikeRawValue.userType, {
        validators: [Validators.required],
      }),
      likedAt: new FormControl(publicationLikeRawValue.likedAt, {
        validators: [Validators.required],
      }),
      publication: new FormControl(publicationLikeRawValue.publication),
    });
  }

  getPublicationLike(form: PublicationLikeFormGroup): IPublicationLike | NewPublicationLike {
    return this.convertPublicationLikeRawValueToPublicationLike(
      form.getRawValue() as PublicationLikeFormRawValue | NewPublicationLikeFormRawValue,
    );
  }

  resetForm(form: PublicationLikeFormGroup, publicationLike: PublicationLikeFormGroupInput): void {
    const publicationLikeRawValue = this.convertPublicationLikeToPublicationLikeRawValue({ ...this.getFormDefaults(), ...publicationLike });
    form.reset(
      {
        ...publicationLikeRawValue,
        id: { value: publicationLikeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PublicationLikeFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      likedAt: currentTime,
    };
  }

  private convertPublicationLikeRawValueToPublicationLike(
    rawPublicationLike: PublicationLikeFormRawValue | NewPublicationLikeFormRawValue,
  ): IPublicationLike | NewPublicationLike {
    return {
      ...rawPublicationLike,
      likedAt: dayjs(rawPublicationLike.likedAt, DATE_TIME_FORMAT),
    };
  }

  private convertPublicationLikeToPublicationLikeRawValue(
    publicationLike: IPublicationLike | (Partial<NewPublicationLike> & PublicationLikeFormDefaults),
  ): PublicationLikeFormRawValue | PartialWithRequiredKeyOf<NewPublicationLikeFormRawValue> {
    return {
      ...publicationLike,
      likedAt: publicationLike.likedAt ? publicationLike.likedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
