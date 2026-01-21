import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../publication.test-samples';

import { PublicationFormService } from './publication-form.service';

describe('Publication Form Service', () => {
  let service: PublicationFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PublicationFormService);
  });

  describe('Service methods', () => {
    describe('createPublicationFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPublicationFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            content: expect.any(Object),
            authorId: expect.any(Object),
            authorType: expect.any(Object),
            createdAt: expect.any(Object),
          }),
        );
      });

      it('passing IPublication should create a new form with FormGroup', () => {
        const formGroup = service.createPublicationFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            content: expect.any(Object),
            authorId: expect.any(Object),
            authorType: expect.any(Object),
            createdAt: expect.any(Object),
          }),
        );
      });
    });

    describe('getPublication', () => {
      it('should return NewPublication for default Publication initial value', () => {
        const formGroup = service.createPublicationFormGroup(sampleWithNewData);

        const publication = service.getPublication(formGroup) as any;

        expect(publication).toMatchObject(sampleWithNewData);
      });

      it('should return NewPublication for empty Publication initial value', () => {
        const formGroup = service.createPublicationFormGroup();

        const publication = service.getPublication(formGroup) as any;

        expect(publication).toMatchObject({});
      });

      it('should return IPublication', () => {
        const formGroup = service.createPublicationFormGroup(sampleWithRequiredData);

        const publication = service.getPublication(formGroup) as any;

        expect(publication).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPublication should not enable id FormControl', () => {
        const formGroup = service.createPublicationFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPublication should disable id FormControl', () => {
        const formGroup = service.createPublicationFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
