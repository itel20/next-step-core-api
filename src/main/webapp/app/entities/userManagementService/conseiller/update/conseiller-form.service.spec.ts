import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../conseiller.test-samples';

import { ConseillerFormService } from './conseiller-form.service';

describe('Conseiller Form Service', () => {
  let service: ConseillerFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ConseillerFormService);
  });

  describe('Service methods', () => {
    describe('createConseillerFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createConseillerFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            prenom: expect.any(Object),
            specialite: expect.any(Object),
            email: expect.any(Object),
            description: expect.any(Object),
            certifie: expect.any(Object),
            password: expect.any(Object),
            passwordHash: expect.any(Object),
            keycloakId: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });

      it('passing IConseiller should create a new form with FormGroup', () => {
        const formGroup = service.createConseillerFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            prenom: expect.any(Object),
            specialite: expect.any(Object),
            email: expect.any(Object),
            description: expect.any(Object),
            certifie: expect.any(Object),
            password: expect.any(Object),
            passwordHash: expect.any(Object),
            keycloakId: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });
    });

    describe('getConseiller', () => {
      it('should return NewConseiller for default Conseiller initial value', () => {
        const formGroup = service.createConseillerFormGroup(sampleWithNewData);

        const conseiller = service.getConseiller(formGroup) as any;

        expect(conseiller).toMatchObject(sampleWithNewData);
      });

      it('should return NewConseiller for empty Conseiller initial value', () => {
        const formGroup = service.createConseillerFormGroup();

        const conseiller = service.getConseiller(formGroup) as any;

        expect(conseiller).toMatchObject({});
      });

      it('should return IConseiller', () => {
        const formGroup = service.createConseillerFormGroup(sampleWithRequiredData);

        const conseiller = service.getConseiller(formGroup) as any;

        expect(conseiller).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IConseiller should not enable id FormControl', () => {
        const formGroup = service.createConseillerFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewConseiller should disable id FormControl', () => {
        const formGroup = service.createConseillerFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
