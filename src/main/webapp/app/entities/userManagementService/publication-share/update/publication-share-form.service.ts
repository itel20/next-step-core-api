import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPublicationShare, NewPublicationShare } from '../publication-share.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPublicationShare for edit and NewPublicationShareFormGroupInput for create.
 */
type PublicationShareFormGroupInput = IPublicationShare | PartialWithRequiredKeyOf<NewPublicationShare>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPublicationShare | NewPublicationShare> = Omit<T, 'sharedAt'> & {
  sharedAt?: string | null;
};

type PublicationShareFormRawValue = FormValueOf<IPublicationShare>;

type NewPublicationShareFormRawValue = FormValueOf<NewPublicationShare>;

type PublicationShareFormDefaults = Pick<NewPublicationShare, 'id' | 'sharedAt'>;

type PublicationShareFormGroupContent = {
  id: FormControl<PublicationShareFormRawValue['id'] | NewPublicationShare['id']>;
  userId: FormControl<PublicationShareFormRawValue['userId']>;
  userType: FormControl<PublicationShareFormRawValue['userType']>;
  sharedAt: FormControl<PublicationShareFormRawValue['sharedAt']>;
  publication: FormControl<PublicationShareFormRawValue['publication']>;
};

export type PublicationShareFormGroup = FormGroup<PublicationShareFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PublicationShareFormService {
  createPublicationShareFormGroup(publicationShare: PublicationShareFormGroupInput = { id: null }): PublicationShareFormGroup {
    const publicationShareRawValue = this.convertPublicationShareToPublicationShareRawValue({
      ...this.getFormDefaults(),
      ...publicationShare,
    });
    return new FormGroup<PublicationShareFormGroupContent>({
      id: new FormControl(
        { value: publicationShareRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      userId: new FormControl(publicationShareRawValue.userId, {
        validators: [Validators.required],
      }),
      userType: new FormControl(publicationShareRawValue.userType, {
        validators: [Validators.required],
      }),
      sharedAt: new FormControl(publicationShareRawValue.sharedAt, {
        validators: [Validators.required],
      }),
      publication: new FormControl(publicationShareRawValue.publication),
    });
  }

  getPublicationShare(form: PublicationShareFormGroup): IPublicationShare | NewPublicationShare {
    return this.convertPublicationShareRawValueToPublicationShare(
      form.getRawValue() as PublicationShareFormRawValue | NewPublicationShareFormRawValue,
    );
  }

  resetForm(form: PublicationShareFormGroup, publicationShare: PublicationShareFormGroupInput): void {
    const publicationShareRawValue = this.convertPublicationShareToPublicationShareRawValue({
      ...this.getFormDefaults(),
      ...publicationShare,
    });
    form.reset(
      {
        ...publicationShareRawValue,
        id: { value: publicationShareRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PublicationShareFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      sharedAt: currentTime,
    };
  }

  private convertPublicationShareRawValueToPublicationShare(
    rawPublicationShare: PublicationShareFormRawValue | NewPublicationShareFormRawValue,
  ): IPublicationShare | NewPublicationShare {
    return {
      ...rawPublicationShare,
      sharedAt: dayjs(rawPublicationShare.sharedAt, DATE_TIME_FORMAT),
    };
  }

  private convertPublicationShareToPublicationShareRawValue(
    publicationShare: IPublicationShare | (Partial<NewPublicationShare> & PublicationShareFormDefaults),
  ): PublicationShareFormRawValue | PartialWithRequiredKeyOf<NewPublicationShareFormRawValue> {
    return {
      ...publicationShare,
      sharedAt: publicationShare.sharedAt ? publicationShare.sharedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
