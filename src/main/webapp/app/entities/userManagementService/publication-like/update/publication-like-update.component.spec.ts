import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IPublication } from 'app/entities/userManagementService/publication/publication.model';
import { PublicationService } from 'app/entities/userManagementService/publication/service/publication.service';
import { PublicationLikeService } from '../service/publication-like.service';
import { IPublicationLike } from '../publication-like.model';
import { PublicationLikeFormService } from './publication-like-form.service';

import { PublicationLikeUpdateComponent } from './publication-like-update.component';

describe('PublicationLike Management Update Component', () => {
  let comp: PublicationLikeUpdateComponent;
  let fixture: ComponentFixture<PublicationLikeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let publicationLikeFormService: PublicationLikeFormService;
  let publicationLikeService: PublicationLikeService;
  let publicationService: PublicationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PublicationLikeUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(PublicationLikeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PublicationLikeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    publicationLikeFormService = TestBed.inject(PublicationLikeFormService);
    publicationLikeService = TestBed.inject(PublicationLikeService);
    publicationService = TestBed.inject(PublicationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Publication query and add missing value', () => {
      const publicationLike: IPublicationLike = { id: 456 };
      const publication: IPublication = { id: 4525 };
      publicationLike.publication = publication;

      const publicationCollection: IPublication[] = [{ id: 1369 }];
      jest.spyOn(publicationService, 'query').mockReturnValue(of(new HttpResponse({ body: publicationCollection })));
      const additionalPublications = [publication];
      const expectedCollection: IPublication[] = [...additionalPublications, ...publicationCollection];
      jest.spyOn(publicationService, 'addPublicationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ publicationLike });
      comp.ngOnInit();

      expect(publicationService.query).toHaveBeenCalled();
      expect(publicationService.addPublicationToCollectionIfMissing).toHaveBeenCalledWith(
        publicationCollection,
        ...additionalPublications.map(expect.objectContaining),
      );
      expect(comp.publicationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const publicationLike: IPublicationLike = { id: 456 };
      const publication: IPublication = { id: 16057 };
      publicationLike.publication = publication;

      activatedRoute.data = of({ publicationLike });
      comp.ngOnInit();

      expect(comp.publicationsSharedCollection).toContain(publication);
      expect(comp.publicationLike).toEqual(publicationLike);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPublicationLike>>();
      const publicationLike = { id: 123 };
      jest.spyOn(publicationLikeFormService, 'getPublicationLike').mockReturnValue(publicationLike);
      jest.spyOn(publicationLikeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ publicationLike });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: publicationLike }));
      saveSubject.complete();

      // THEN
      expect(publicationLikeFormService.getPublicationLike).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(publicationLikeService.update).toHaveBeenCalledWith(expect.objectContaining(publicationLike));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPublicationLike>>();
      const publicationLike = { id: 123 };
      jest.spyOn(publicationLikeFormService, 'getPublicationLike').mockReturnValue({ id: null });
      jest.spyOn(publicationLikeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ publicationLike: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: publicationLike }));
      saveSubject.complete();

      // THEN
      expect(publicationLikeFormService.getPublicationLike).toHaveBeenCalled();
      expect(publicationLikeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPublicationLike>>();
      const publicationLike = { id: 123 };
      jest.spyOn(publicationLikeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ publicationLike });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(publicationLikeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePublication', () => {
      it('Should forward to publicationService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(publicationService, 'comparePublication');
        comp.comparePublication(entity, entity2);
        expect(publicationService.comparePublication).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
