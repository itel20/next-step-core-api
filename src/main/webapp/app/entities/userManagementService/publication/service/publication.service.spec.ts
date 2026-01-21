import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IPublication } from '../publication.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../publication.test-samples';

import { PublicationService, RestPublication } from './publication.service';

const requireRestSample: RestPublication = {
  ...sampleWithRequiredData,
  createdAt: sampleWithRequiredData.createdAt?.toJSON(),
};

describe('Publication Service', () => {
  let service: PublicationService;
  let httpMock: HttpTestingController;
  let expectedResult: IPublication | IPublication[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PublicationService);
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

    it('should create a Publication', () => {
      const publication = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(publication).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Publication', () => {
      const publication = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(publication).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Publication', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Publication', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Publication', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Publication', () => {
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

    describe('addPublicationToCollectionIfMissing', () => {
      it('should add a Publication to an empty array', () => {
        const publication: IPublication = sampleWithRequiredData;
        expectedResult = service.addPublicationToCollectionIfMissing([], publication);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(publication);
      });

      it('should not add a Publication to an array that contains it', () => {
        const publication: IPublication = sampleWithRequiredData;
        const publicationCollection: IPublication[] = [
          {
            ...publication,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPublicationToCollectionIfMissing(publicationCollection, publication);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Publication to an array that doesn't contain it", () => {
        const publication: IPublication = sampleWithRequiredData;
        const publicationCollection: IPublication[] = [sampleWithPartialData];
        expectedResult = service.addPublicationToCollectionIfMissing(publicationCollection, publication);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(publication);
      });

      it('should add only unique Publication to an array', () => {
        const publicationArray: IPublication[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const publicationCollection: IPublication[] = [sampleWithRequiredData];
        expectedResult = service.addPublicationToCollectionIfMissing(publicationCollection, ...publicationArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const publication: IPublication = sampleWithRequiredData;
        const publication2: IPublication = sampleWithPartialData;
        expectedResult = service.addPublicationToCollectionIfMissing([], publication, publication2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(publication);
        expect(expectedResult).toContain(publication2);
      });

      it('should accept null and undefined values', () => {
        const publication: IPublication = sampleWithRequiredData;
        expectedResult = service.addPublicationToCollectionIfMissing([], null, publication, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(publication);
      });

      it('should return initial array if no Publication is added', () => {
        const publicationCollection: IPublication[] = [sampleWithRequiredData];
        expectedResult = service.addPublicationToCollectionIfMissing(publicationCollection, undefined, null);
        expect(expectedResult).toEqual(publicationCollection);
      });
    });

    describe('comparePublication', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePublication(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.comparePublication(entity1, entity2);
        const compareResult2 = service.comparePublication(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.comparePublication(entity1, entity2);
        const compareResult2 = service.comparePublication(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.comparePublication(entity1, entity2);
        const compareResult2 = service.comparePublication(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
