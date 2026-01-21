import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../publication-like.test-samples';

import { PublicationLikeFormService } from './publication-like-form.service';

describe('PublicationLike Form Service', () => {
  let service: PublicationLikeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PublicationLikeFormService);
  });

  describe('Service methods', () => {
    describe('createPublicationLikeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPublicationLikeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            userId: expect.any(Object),
            userType: expect.any(Object),
            likedAt: expect.any(Object),
            publication: expect.any(Object),
          }),
        );
      });

      it('passing IPublicationLike should create a new form with FormGroup', () => {
        const formGroup = service.createPublicationLikeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            userId: expect.any(Object),
            userType: expect.any(Object),
            likedAt: expect.any(Object),
            publication: expect.any(Object),
          }),
        );
      });
    });

    describe('getPublicationLike', () => {
      it('should return NewPublicationLike for default PublicationLike initial value', () => {
        const formGroup = service.createPublicationLikeFormGroup(sampleWithNewData);

        const publicationLike = service.getPublicationLike(formGroup) as any;

        expect(publicationLike).toMatchObject(sampleWithNewData);
      });

      it('should return NewPublicationLike for empty PublicationLike initial value', () => {
        const formGroup = service.createPublicationLikeFormGroup();

        const publicationLike = service.getPublicationLike(formGroup) as any;

        expect(publicationLike).toMatchObject({});
      });

      it('should return IPublicationLike', () => {
        const formGroup = service.createPublicationLikeFormGroup(sampleWithRequiredData);

        const publicationLike = service.getPublicationLike(formGroup) as any;

        expect(publicationLike).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPublicationLike should not enable id FormControl', () => {
        const formGroup = service.createPublicationLikeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPublicationLike should disable id FormControl', () => {
        const formGroup = service.createPublicationLikeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
