import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IPublication } from 'app/entities/userManagementService/publication/publication.model';
import { PublicationService } from 'app/entities/userManagementService/publication/service/publication.service';
import { PublicationShareService } from '../service/publication-share.service';
import { IPublicationShare } from '../publication-share.model';
import { PublicationShareFormService } from './publication-share-form.service';

import { PublicationShareUpdateComponent } from './publication-share-update.component';

describe('PublicationShare Management Update Component', () => {
  let comp: PublicationShareUpdateComponent;
  let fixture: ComponentFixture<PublicationShareUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let publicationShareFormService: PublicationShareFormService;
  let publicationShareService: PublicationShareService;
  let publicationService: PublicationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PublicationShareUpdateComponent],
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
      .overrideTemplate(PublicationShareUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PublicationShareUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    publicationShareFormService = TestBed.inject(PublicationShareFormService);
    publicationShareService = TestBed.inject(PublicationShareService);
    publicationService = TestBed.inject(PublicationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Publication query and add missing value', () => {
      const publicationShare: IPublicationShare = { id: 456 };
      const publication: IPublication = { id: 25719 };
      publicationShare.publication = publication;

      const publicationCollection: IPublication[] = [{ id: 6910 }];
      jest.spyOn(publicationService, 'query').mockReturnValue(of(new HttpResponse({ body: publicationCollection })));
      const additionalPublications = [publication];
      const expectedCollection: IPublication[] = [...additionalPublications, ...publicationCollection];
      jest.spyOn(publicationService, 'addPublicationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ publicationShare });
      comp.ngOnInit();

      expect(publicationService.query).toHaveBeenCalled();
      expect(publicationService.addPublicationToCollectionIfMissing).toHaveBeenCalledWith(
        publicationCollection,
        ...additionalPublications.map(expect.objectContaining),
      );
      expect(comp.publicationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const publicationShare: IPublicationShare = { id: 456 };
      const publication: IPublication = { id: 16093 };
      publicationShare.publication = publication;

      activatedRoute.data = of({ publicationShare });
      comp.ngOnInit();

      expect(comp.publicationsSharedCollection).toContain(publication);
      expect(comp.publicationShare).toEqual(publicationShare);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPublicationShare>>();
      const publicationShare = { id: 123 };
      jest.spyOn(publicationShareFormService, 'getPublicationShare').mockReturnValue(publicationShare);
      jest.spyOn(publicationShareService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ publicationShare });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: publicationShare }));
      saveSubject.complete();

      // THEN
      expect(publicationShareFormService.getPublicationShare).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(publicationShareService.update).toHaveBeenCalledWith(expect.objectContaining(publicationShare));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPublicationShare>>();
      const publicationShare = { id: 123 };
      jest.spyOn(publicationShareFormService, 'getPublicationShare').mockReturnValue({ id: null });
      jest.spyOn(publicationShareService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ publicationShare: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: publicationShare }));
      saveSubject.complete();

      // THEN
      expect(publicationShareFormService.getPublicationShare).toHaveBeenCalled();
      expect(publicationShareService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPublicationShare>>();
      const publicationShare = { id: 123 };
      jest.spyOn(publicationShareService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ publicationShare });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(publicationShareService.update).toHaveBeenCalled();
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
