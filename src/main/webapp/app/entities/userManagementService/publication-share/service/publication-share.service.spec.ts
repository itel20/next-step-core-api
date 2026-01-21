import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IPublicationShare } from '../publication-share.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../publication-share.test-samples';

import { PublicationShareService, RestPublicationShare } from './publication-share.service';

const requireRestSample: RestPublicationShare = {
  ...sampleWithRequiredData,
  sharedAt: sampleWithRequiredData.sharedAt?.toJSON(),
};

describe('PublicationShare Service', () => {
  let service: PublicationShareService;
  let httpMock: HttpTestingController;
  let expectedResult: IPublicationShare | IPublicationShare[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PublicationShareService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a PublicationShare', () => {
      const publicationShare = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(publicationShare).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PublicationShare', () => {
      const publicationShare = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(publicationShare).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PublicationShare', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PublicationShare', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PublicationShare', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a PublicationShare', () => {
      const queryObject: any = {
        page: 0,
        size: 20,
        query: '',
        sort: [],
      };
      service.search(queryObject).subscribe(() => expectedResult);

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(null, { status: 500, statusText: 'Internal Server Error' });
      expect(expectedResult).toBe(null);
    });

    describe('addPublicationShareToCollectionIfMissing', () => {
      it('should add a PublicationShare to an empty array', () => {
        const publicationShare: IPublicationShare = sampleWithRequiredData;
        expectedResult = service.addPublicationShareToCollectionIfMissing([], publicationShare);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(publicationShare);
      });

      it('should not add a PublicationShare to an array that contains it', () => {
        const publicationShare: IPublicationShare = sampleWithRequiredData;
        const publicationShareCollection: IPublicationShare[] = [
          {
            ...publicationShare,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPublicationShareToCollectionIfMissing(publicationShareCollection, publicationShare);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PublicationShare to an array that doesn't contain it", () => {
        const publicationShare: IPublicationShare = sampleWithRequiredData;
        const publicationShareCollection: IPublicationShare[] = [sampleWithPartialData];
        expectedResult = service.addPublicationShareToCollectionIfMissing(publicationShareCollection, publicationShare);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(publicationShare);
      });

      it('should add only unique PublicationShare to an array', () => {
        const publicationShareArray: IPublicationShare[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const publicationShareCollection: IPublicationShare[] = [sampleWithRequiredData];
        expectedResult = service.addPublicationShareToCollectionIfMissing(publicationShareCollection, ...publicationShareArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const publicationShare: IPublicationShare = sampleWithRequiredData;
        const publicationShare2: IPublicationShare = sampleWithPartialData;
        expectedResult = service.addPublicationShareToCollectionIfMissing([], publicationShare, publicationShare2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(publicationShare);
        expect(expectedResult).toContain(publicationShare2);
      });

      it('should accept null and undefined values', () => {
        const publicationShare: IPublicationShare = sampleWithRequiredData;
        expectedResult = service.addPublicationShareToCollectionIfMissing([], null, publicationShare, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(publicationShare);
      });

      it('should return initial array if no PublicationShare is added', () => {
        const publicationShareCollection: IPublicationShare[] = [sampleWithRequiredData];
        expectedResult = service.addPublicationShareToCollectionIfMissing(publicationShareCollection, undefined, null);
        expect(expectedResult).toEqual(publicationShareCollection);
      });
    });

    describe('comparePublicationShare', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePublicationShare(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.comparePublicationShare(entity1, entity2);
        const compareResult2 = service.comparePublicationShare(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.comparePublicationShare(entity1, entity2);
        const compareResult2 = service.comparePublicationShare(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.comparePublicationShare(entity1, entity2);
        const compareResult2 = service.comparePublicationShare(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
