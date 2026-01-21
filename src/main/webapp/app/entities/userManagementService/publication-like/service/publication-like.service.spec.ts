import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IPublicationLike } from '../publication-like.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../publication-like.test-samples';

import { PublicationLikeService, RestPublicationLike } from './publication-like.service';

const requireRestSample: RestPublicationLike = {
  ...sampleWithRequiredData,
  likedAt: sampleWithRequiredData.likedAt?.toJSON(),
};

describe('PublicationLike Service', () => {
  let service: PublicationLikeService;
  let httpMock: HttpTestingController;
  let expectedResult: IPublicationLike | IPublicationLike[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PublicationLikeService);
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

    it('should create a PublicationLike', () => {
      const publicationLike = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(publicationLike).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PublicationLike', () => {
      const publicationLike = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(publicationLike).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PublicationLike', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PublicationLike', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PublicationLike', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a PublicationLike', () => {
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

    describe('addPublicationLikeToCollectionIfMissing', () => {
      it('should add a PublicationLike to an empty array', () => {
        const publicationLike: IPublicationLike = sampleWithRequiredData;
        expectedResult = service.addPublicationLikeToCollectionIfMissing([], publicationLike);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(publicationLike);
      });

      it('should not add a PublicationLike to an array that contains it', () => {
        const publicationLike: IPublicationLike = sampleWithRequiredData;
        const publicationLikeCollection: IPublicationLike[] = [
          {
            ...publicationLike,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPublicationLikeToCollectionIfMissing(publicationLikeCollection, publicationLike);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PublicationLike to an array that doesn't contain it", () => {
        const publicationLike: IPublicationLike = sampleWithRequiredData;
        const publicationLikeCollection: IPublicationLike[] = [sampleWithPartialData];
        expectedResult = service.addPublicationLikeToCollectionIfMissing(publicationLikeCollection, publicationLike);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(publicationLike);
      });

      it('should add only unique PublicationLike to an array', () => {
        const publicationLikeArray: IPublicationLike[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const publicationLikeCollection: IPublicationLike[] = [sampleWithRequiredData];
        expectedResult = service.addPublicationLikeToCollectionIfMissing(publicationLikeCollection, ...publicationLikeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const publicationLike: IPublicationLike = sampleWithRequiredData;
        const publicationLike2: IPublicationLike = sampleWithPartialData;
        expectedResult = service.addPublicationLikeToCollectionIfMissing([], publicationLike, publicationLike2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(publicationLike);
        expect(expectedResult).toContain(publicationLike2);
      });

      it('should accept null and undefined values', () => {
        const publicationLike: IPublicationLike = sampleWithRequiredData;
        expectedResult = service.addPublicationLikeToCollectionIfMissing([], null, publicationLike, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(publicationLike);
      });

      it('should return initial array if no PublicationLike is added', () => {
        const publicationLikeCollection: IPublicationLike[] = [sampleWithRequiredData];
        expectedResult = service.addPublicationLikeToCollectionIfMissing(publicationLikeCollection, undefined, null);
        expect(expectedResult).toEqual(publicationLikeCollection);
      });
    });

    describe('comparePublicationLike', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePublicationLike(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.comparePublicationLike(entity1, entity2);
        const compareResult2 = service.comparePublicationLike(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.comparePublicationLike(entity1, entity2);
        const compareResult2 = service.comparePublicationLike(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.comparePublicationLike(entity1, entity2);
        const compareResult2 = service.comparePublicationLike(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
