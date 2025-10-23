import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IEleve } from '../eleve.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../eleve.test-samples';

import { EleveService, RestEleve } from './eleve.service';

const requireRestSample: RestEleve = {
  ...sampleWithRequiredData,
  dateNaissance: sampleWithRequiredData.dateNaissance?.format(DATE_FORMAT),
};

describe('Eleve Service', () => {
  let service: EleveService;
  let httpMock: HttpTestingController;
  let expectedResult: IEleve | IEleve[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(EleveService);
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

    it('should create a Eleve', () => {
      const eleve = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(eleve).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Eleve', () => {
      const eleve = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(eleve).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Eleve', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Eleve', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Eleve', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Eleve', () => {
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

    describe('addEleveToCollectionIfMissing', () => {
      it('should add a Eleve to an empty array', () => {
        const eleve: IEleve = sampleWithRequiredData;
        expectedResult = service.addEleveToCollectionIfMissing([], eleve);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(eleve);
      });

      it('should not add a Eleve to an array that contains it', () => {
        const eleve: IEleve = sampleWithRequiredData;
        const eleveCollection: IEleve[] = [
          {
            ...eleve,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addEleveToCollectionIfMissing(eleveCollection, eleve);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Eleve to an array that doesn't contain it", () => {
        const eleve: IEleve = sampleWithRequiredData;
        const eleveCollection: IEleve[] = [sampleWithPartialData];
        expectedResult = service.addEleveToCollectionIfMissing(eleveCollection, eleve);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(eleve);
      });

      it('should add only unique Eleve to an array', () => {
        const eleveArray: IEleve[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const eleveCollection: IEleve[] = [sampleWithRequiredData];
        expectedResult = service.addEleveToCollectionIfMissing(eleveCollection, ...eleveArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const eleve: IEleve = sampleWithRequiredData;
        const eleve2: IEleve = sampleWithPartialData;
        expectedResult = service.addEleveToCollectionIfMissing([], eleve, eleve2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(eleve);
        expect(expectedResult).toContain(eleve2);
      });

      it('should accept null and undefined values', () => {
        const eleve: IEleve = sampleWithRequiredData;
        expectedResult = service.addEleveToCollectionIfMissing([], null, eleve, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(eleve);
      });

      it('should return initial array if no Eleve is added', () => {
        const eleveCollection: IEleve[] = [sampleWithRequiredData];
        expectedResult = service.addEleveToCollectionIfMissing(eleveCollection, undefined, null);
        expect(expectedResult).toEqual(eleveCollection);
      });
    });

    describe('compareEleve', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareEleve(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareEleve(entity1, entity2);
        const compareResult2 = service.compareEleve(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareEleve(entity1, entity2);
        const compareResult2 = service.compareEleve(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareEleve(entity1, entity2);
        const compareResult2 = service.compareEleve(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
