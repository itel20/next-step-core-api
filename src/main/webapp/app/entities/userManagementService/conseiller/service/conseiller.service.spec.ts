import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IConseiller } from '../conseiller.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../conseiller.test-samples';

import { ConseillerService } from './conseiller.service';

const requireRestSample: IConseiller = {
  ...sampleWithRequiredData,
};

describe('Conseiller Service', () => {
  let service: ConseillerService;
  let httpMock: HttpTestingController;
  let expectedResult: IConseiller | IConseiller[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ConseillerService);
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

    it('should create a Conseiller', () => {
      const conseiller = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(conseiller).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Conseiller', () => {
      const conseiller = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(conseiller).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Conseiller', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Conseiller', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Conseiller', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Conseiller', () => {
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

    describe('addConseillerToCollectionIfMissing', () => {
      it('should add a Conseiller to an empty array', () => {
        const conseiller: IConseiller = sampleWithRequiredData;
        expectedResult = service.addConseillerToCollectionIfMissing([], conseiller);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(conseiller);
      });

      it('should not add a Conseiller to an array that contains it', () => {
        const conseiller: IConseiller = sampleWithRequiredData;
        const conseillerCollection: IConseiller[] = [
          {
            ...conseiller,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addConseillerToCollectionIfMissing(conseillerCollection, conseiller);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Conseiller to an array that doesn't contain it", () => {
        const conseiller: IConseiller = sampleWithRequiredData;
        const conseillerCollection: IConseiller[] = [sampleWithPartialData];
        expectedResult = service.addConseillerToCollectionIfMissing(conseillerCollection, conseiller);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(conseiller);
      });

      it('should add only unique Conseiller to an array', () => {
        const conseillerArray: IConseiller[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const conseillerCollection: IConseiller[] = [sampleWithRequiredData];
        expectedResult = service.addConseillerToCollectionIfMissing(conseillerCollection, ...conseillerArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const conseiller: IConseiller = sampleWithRequiredData;
        const conseiller2: IConseiller = sampleWithPartialData;
        expectedResult = service.addConseillerToCollectionIfMissing([], conseiller, conseiller2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(conseiller);
        expect(expectedResult).toContain(conseiller2);
      });

      it('should accept null and undefined values', () => {
        const conseiller: IConseiller = sampleWithRequiredData;
        expectedResult = service.addConseillerToCollectionIfMissing([], null, conseiller, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(conseiller);
      });

      it('should return initial array if no Conseiller is added', () => {
        const conseillerCollection: IConseiller[] = [sampleWithRequiredData];
        expectedResult = service.addConseillerToCollectionIfMissing(conseillerCollection, undefined, null);
        expect(expectedResult).toEqual(conseillerCollection);
      });
    });

    describe('compareConseiller', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareConseiller(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareConseiller(entity1, entity2);
        const compareResult2 = service.compareConseiller(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareConseiller(entity1, entity2);
        const compareResult2 = service.compareConseiller(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareConseiller(entity1, entity2);
        const compareResult2 = service.compareConseiller(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
