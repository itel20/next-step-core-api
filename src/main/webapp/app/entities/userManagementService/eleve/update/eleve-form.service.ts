import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IEleve, NewEleve } from '../eleve.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEleve for edit and NewEleveFormGroupInput for create.
 */
type EleveFormGroupInput = IEleve | PartialWithRequiredKeyOf<NewEleve>;

type EleveFormDefaults = Pick<NewEleve, 'id'>;

type EleveFormGroupContent = {
  id: FormControl<IEleve['id'] | NewEleve['id']>;
  nom: FormControl<IEleve['nom']>;
  prenom: FormControl<IEleve['prenom']>;
  dateNaissance: FormControl<IEleve['dateNaissance']>;
  telephone: FormControl<IEleve['telephone']>;
  adresse: FormControl<IEleve['adresse']>;
  email: FormControl<IEleve['email']>;
  serie: FormControl<IEleve['serie']>;
  niveauEtude: FormControl<IEleve['niveauEtude']>;
  lycee: FormControl<IEleve['lycee']>;
  ville: FormControl<IEleve['ville']>;
  password: FormControl<IEleve['password']>;
  passwordHash: FormControl<IEleve['passwordHash']>;
  keycloakId: FormControl<IEleve['keycloakId']>;
  user: FormControl<IEleve['user']>;
};

export type EleveFormGroup = FormGroup<EleveFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EleveFormService {
  createEleveFormGroup(eleve: EleveFormGroupInput = { id: null }): EleveFormGroup {
    const eleveRawValue = {
      ...this.getFormDefaults(),
      ...eleve,
    };
    return new FormGroup<EleveFormGroupContent>({
      id: new FormControl(
        { value: eleveRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nom: new FormControl(eleveRawValue.nom, {
        validators: [Validators.required],
      }),
      prenom: new FormControl(eleveRawValue.prenom, {
        validators: [Validators.required],
      }),
      dateNaissance: new FormControl(eleveRawValue.dateNaissance),
      telephone: new FormControl(eleveRawValue.telephone),
      adresse: new FormControl(eleveRawValue.adresse),
      email: new FormControl(eleveRawValue.email, {
        validators: [Validators.required],
      }),
      serie: new FormControl(eleveRawValue.serie, {
        validators: [Validators.required],
      }),
      niveauEtude: new FormControl(eleveRawValue.niveauEtude, {
        validators: [Validators.required],
      }),
      lycee: new FormControl(eleveRawValue.lycee),
      ville: new FormControl(eleveRawValue.ville),
      password: new FormControl(eleveRawValue.password),
      passwordHash: new FormControl(eleveRawValue.passwordHash, {
        validators: [Validators.required],
      }),
      keycloakId: new FormControl(eleveRawValue.keycloakId),
      user: new FormControl(eleveRawValue.user),
    });
  }

  getEleve(form: EleveFormGroup): IEleve | NewEleve {
    return form.getRawValue() as IEleve | NewEleve;
  }

  resetForm(form: EleveFormGroup, eleve: EleveFormGroupInput): void {
    const eleveRawValue = { ...this.getFormDefaults(), ...eleve };
    form.reset(
      {
        ...eleveRawValue,
        id: { value: eleveRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): EleveFormDefaults {
    return {
      id: null,
    };
  }
}
