import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../eleve.test-samples';

import { EleveFormService } from './eleve-form.service';

describe('Eleve Form Service', () => {
  let service: EleveFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EleveFormService);
  });

  describe('Service methods', () => {
    describe('createEleveFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createEleveFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            prenom: expect.any(Object),
            dateNaissance: expect.any(Object),
            telephone: expect.any(Object),
            adresse: expect.any(Object),
            email: expect.any(Object),
            serie: expect.any(Object),
            niveauEtude: expect.any(Object),
            lycee: expect.any(Object),
            ville: expect.any(Object),
            password: expect.any(Object),
            passwordHash: expect.any(Object),
            keycloakId: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });

      it('passing IEleve should create a new form with FormGroup', () => {
        const formGroup = service.createEleveFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            prenom: expect.any(Object),
            dateNaissance: expect.any(Object),
            telephone: expect.any(Object),
            adresse: expect.any(Object),
            email: expect.any(Object),
            serie: expect.any(Object),
            niveauEtude: expect.any(Object),
            lycee: expect.any(Object),
            ville: expect.any(Object),
            password: expect.any(Object),
            passwordHash: expect.any(Object),
            keycloakId: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });
    });

    describe('getEleve', () => {
      it('should return NewEleve for default Eleve initial value', () => {
        const formGroup = service.createEleveFormGroup(sampleWithNewData);

        const eleve = service.getEleve(formGroup) as any;

        expect(eleve).toMatchObject(sampleWithNewData);
      });

      it('should return NewEleve for empty Eleve initial value', () => {
        const formGroup = service.createEleveFormGroup();

        const eleve = service.getEleve(formGroup) as any;

        expect(eleve).toMatchObject({});
      });

      it('should return IEleve', () => {
        const formGroup = service.createEleveFormGroup(sampleWithRequiredData);

        const eleve = service.getEleve(formGroup) as any;

        expect(eleve).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IEleve should not enable id FormControl', () => {
        const formGroup = service.createEleveFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewEleve should disable id FormControl', () => {
        const formGroup = service.createEleveFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
