import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IConseiller, NewConseiller } from '../conseiller.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IConseiller for edit and NewConseillerFormGroupInput for create.
 */
type ConseillerFormGroupInput = IConseiller | PartialWithRequiredKeyOf<NewConseiller>;

type ConseillerFormDefaults = Pick<NewConseiller, 'id' | 'certifie'>;

type ConseillerFormGroupContent = {
  id: FormControl<IConseiller['id'] | NewConseiller['id']>;
  nom: FormControl<IConseiller['nom']>;
  prenom: FormControl<IConseiller['prenom']>;
  specialite: FormControl<IConseiller['specialite']>;
  email: FormControl<IConseiller['email']>;
  description: FormControl<IConseiller['description']>;
  certifie: FormControl<IConseiller['certifie']>;
  password: FormControl<IConseiller['password']>;
  passwordHash: FormControl<IConseiller['passwordHash']>;
  keycloakId: FormControl<IConseiller['keycloakId']>;
  user: FormControl<IConseiller['user']>;
};

export type ConseillerFormGroup = FormGroup<ConseillerFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ConseillerFormService {
  createConseillerFormGroup(conseiller: ConseillerFormGroupInput = { id: null }): ConseillerFormGroup {
    const conseillerRawValue = {
      ...this.getFormDefaults(),
      ...conseiller,
    };
    return new FormGroup<ConseillerFormGroupContent>({
      id: new FormControl(
        { value: conseillerRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nom: new FormControl(conseillerRawValue.nom, {
        validators: [Validators.required],
      }),
      prenom: new FormControl(conseillerRawValue.prenom, {
        validators: [Validators.required],
      }),
      specialite: new FormControl(conseillerRawValue.specialite, {
        validators: [Validators.required],
      }),
      email: new FormControl(conseillerRawValue.email, {
        validators: [Validators.required],
      }),
      description: new FormControl(conseillerRawValue.description),
      certifie: new FormControl(conseillerRawValue.certifie),
      password: new FormControl(conseillerRawValue.password),
      passwordHash: new FormControl(conseillerRawValue.passwordHash, {
        validators: [Validators.required],
      }),
      keycloakId: new FormControl(conseillerRawValue.keycloakId),
      user: new FormControl(conseillerRawValue.user),
    });
  }

  getConseiller(form: ConseillerFormGroup): IConseiller | NewConseiller {
    return form.getRawValue() as IConseiller | NewConseiller;
  }

  resetForm(form: ConseillerFormGroup, conseiller: ConseillerFormGroupInput): void {
    const conseillerRawValue = { ...this.getFormDefaults(), ...conseiller };
    form.reset(
      {
        ...conseillerRawValue,
        id: { value: conseillerRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ConseillerFormDefaults {
    return {
      id: null,
      certifie: false,
    };
  }
}
