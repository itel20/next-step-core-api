import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IEtudiant, NewEtudiant } from '../etudiant.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEtudiant for edit and NewEtudiantFormGroupInput for create.
 */
type EtudiantFormGroupInput = IEtudiant | PartialWithRequiredKeyOf<NewEtudiant>;

type EtudiantFormDefaults = Pick<NewEtudiant, 'id'>;

type EtudiantFormGroupContent = {
  id: FormControl<IEtudiant['id'] | NewEtudiant['id']>;
  nom: FormControl<IEtudiant['nom']>;
  prenom: FormControl<IEtudiant['prenom']>;
  dateNaissance: FormControl<IEtudiant['dateNaissance']>;
  telephone: FormControl<IEtudiant['telephone']>;
  adresse: FormControl<IEtudiant['adresse']>;
  email: FormControl<IEtudiant['email']>;
  typeBac: FormControl<IEtudiant['typeBac']>;
  anneeBac: FormControl<IEtudiant['anneeBac']>;
  niveauDetudes: FormControl<IEtudiant['niveauDetudes']>;
  universiteSouhaitee: FormControl<IEtudiant['universiteSouhaitee']>;
  specialiteSouhaitee: FormControl<IEtudiant['specialiteSouhaitee']>;
  password: FormControl<IEtudiant['password']>;
  passwordHash: FormControl<IEtudiant['passwordHash']>;
  keycloakId: FormControl<IEtudiant['keycloakId']>;
  user: FormControl<IEtudiant['user']>;
};

export type EtudiantFormGroup = FormGroup<EtudiantFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EtudiantFormService {
  createEtudiantFormGroup(etudiant: EtudiantFormGroupInput = { id: null }): EtudiantFormGroup {
    const etudiantRawValue = {
      ...this.getFormDefaults(),
      ...etudiant,
    };
    return new FormGroup<EtudiantFormGroupContent>({
      id: new FormControl(
        { value: etudiantRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nom: new FormControl(etudiantRawValue.nom, {
        validators: [Validators.required],
      }),
      prenom: new FormControl(etudiantRawValue.prenom, {
        validators: [Validators.required],
      }),
      dateNaissance: new FormControl(etudiantRawValue.dateNaissance),
      telephone: new FormControl(etudiantRawValue.telephone),
      adresse: new FormControl(etudiantRawValue.adresse),
      email: new FormControl(etudiantRawValue.email, {
        validators: [Validators.required],
      }),
      typeBac: new FormControl(etudiantRawValue.typeBac, {
        validators: [Validators.required],
      }),
      anneeBac: new FormControl(etudiantRawValue.anneeBac),
      niveauDetudes: new FormControl(etudiantRawValue.niveauDetudes),
      universiteSouhaitee: new FormControl(etudiantRawValue.universiteSouhaitee),
      specialiteSouhaitee: new FormControl(etudiantRawValue.specialiteSouhaitee),
      password: new FormControl(etudiantRawValue.password),
      passwordHash: new FormControl(etudiantRawValue.passwordHash, {
        validators: [Validators.required],
      }),
      keycloakId: new FormControl(etudiantRawValue.keycloakId),
      user: new FormControl(etudiantRawValue.user),
    });
  }

  getEtudiant(form: EtudiantFormGroup): IEtudiant | NewEtudiant {
    return form.getRawValue() as IEtudiant | NewEtudiant;
  }

  resetForm(form: EtudiantFormGroup, etudiant: EtudiantFormGroupInput): void {
    const etudiantRawValue = { ...this.getFormDefaults(), ...etudiant };
    form.reset(
      {
        ...etudiantRawValue,
        id: { value: etudiantRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): EtudiantFormDefaults {
    return {
      id: null,
    };
  }
}
