import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { PublicationService } from '../service/publication.service';
import { IPublication } from '../publication.model';
import { PublicationFormService } from './publication-form.service';

import { PublicationUpdateComponent } from './publication-update.component';

describe('Publication Management Update Component', () => {
  let comp: PublicationUpdateComponent;
  let fixture: ComponentFixture<PublicationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let publicationFormService: PublicationFormService;
  let publicationService: PublicationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PublicationUpdateComponent],
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
      .overrideTemplate(PublicationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PublicationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    publicationFormService = TestBed.inject(PublicationFormService);
    publicationService = TestBed.inject(PublicationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const publication: IPublication = { id: 456 };

      activatedRoute.data = of({ publication });
      comp.ngOnInit();

      expect(comp.publication).toEqual(publication);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPublication>>();
      const publication = { id: 123 };
      jest.spyOn(publicationFormService, 'getPublication').mockReturnValue(publication);
      jest.spyOn(publicationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ publication });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: publication }));
      saveSubject.complete();

      // THEN
      expect(publicationFormService.getPublication).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(publicationService.update).toHaveBeenCalledWith(expect.objectContaining(publication));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPublication>>();
      const publication = { id: 123 };
      jest.spyOn(publicationFormService, 'getPublication').mockReturnValue({ id: null });
      jest.spyOn(publicationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ publication: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: publication }));
      saveSubject.complete();

      // THEN
      expect(publicationFormService.getPublication).toHaveBeenCalled();
      expect(publicationService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPublication>>();
      const publication = { id: 123 };
      jest.spyOn(publicationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ publication });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(publicationService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
