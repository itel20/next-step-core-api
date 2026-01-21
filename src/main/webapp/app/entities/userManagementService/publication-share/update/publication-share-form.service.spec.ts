import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../publication-share.test-samples';

import { PublicationShareFormService } from './publication-share-form.service';

describe('PublicationShare Form Service', () => {
  let service: PublicationShareFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PublicationShareFormService);
  });

  describe('Service methods', () => {
    describe('createPublicationShareFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPublicationShareFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            userId: expect.any(Object),
            userType: expect.any(Object),
            sharedAt: expect.any(Object),
            publication: expect.any(Object),
          }),
        );
      });

      it('passing IPublicationShare should create a new form with FormGroup', () => {
        const formGroup = service.createPublicationShareFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            userId: expect.any(Object),
            userType: expect.any(Object),
            sharedAt: expect.any(Object),
            publication: expect.any(Object),
          }),
        );
      });
    });

    describe('getPublicationShare', () => {
      it('should return NewPublicationShare for default PublicationShare initial value', () => {
        const formGroup = service.createPublicationShareFormGroup(sampleWithNewData);

        const publicationShare = service.getPublicationShare(formGroup) as any;

        expect(publicationShare).toMatchObject(sampleWithNewData);
      });

      it('should return NewPublicationShare for empty PublicationShare initial value', () => {
        const formGroup = service.createPublicationShareFormGroup();

        const publicationShare = service.getPublicationShare(formGroup) as any;

        expect(publicationShare).toMatchObject({});
      });

      it('should return IPublicationShare', () => {
        const formGroup = service.createPublicationShareFormGroup(sampleWithRequiredData);

        const publicationShare = service.getPublicationShare(formGroup) as any;

        expect(publicationShare).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPublicationShare should not enable id FormControl', () => {
        const formGroup = service.createPublicationShareFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPublicationShare should disable id FormControl', () => {
        const formGroup = service.createPublicationShareFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
