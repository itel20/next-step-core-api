import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { ConseillerService } from '../service/conseiller.service';
import { IConseiller } from '../conseiller.model';
import { ConseillerFormService } from './conseiller-form.service';

import { ConseillerUpdateComponent } from './conseiller-update.component';

describe('Conseiller Management Update Component', () => {
  let comp: ConseillerUpdateComponent;
  let fixture: ComponentFixture<ConseillerUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let conseillerFormService: ConseillerFormService;
  let conseillerService: ConseillerService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ConseillerUpdateComponent],
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
      .overrideTemplate(ConseillerUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ConseillerUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    conseillerFormService = TestBed.inject(ConseillerFormService);
    conseillerService = TestBed.inject(ConseillerService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const conseiller: IConseiller = { id: 456 };
      const user: IUser = { id: 'e3ec88f5-591e-4b6a-a965-d20fd853d57f' };
      conseiller.user = user;

      const userCollection: IUser[] = [{ id: '3ed387ca-c863-4ce9-a1ac-1b08792380d7' }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ conseiller });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const conseiller: IConseiller = { id: 456 };
      const user: IUser = { id: 'f6f709d3-3ad0-4915-8a00-febc06f406af' };
      conseiller.user = user;

      activatedRoute.data = of({ conseiller });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.conseiller).toEqual(conseiller);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IConseiller>>();
      const conseiller = { id: 123 };
      jest.spyOn(conseillerFormService, 'getConseiller').mockReturnValue(conseiller);
      jest.spyOn(conseillerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ conseiller });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: conseiller }));
      saveSubject.complete();

      // THEN
      expect(conseillerFormService.getConseiller).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(conseillerService.update).toHaveBeenCalledWith(expect.objectContaining(conseiller));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IConseiller>>();
      const conseiller = { id: 123 };
      jest.spyOn(conseillerFormService, 'getConseiller').mockReturnValue({ id: null });
      jest.spyOn(conseillerService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ conseiller: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: conseiller }));
      saveSubject.complete();

      // THEN
      expect(conseillerFormService.getConseiller).toHaveBeenCalled();
      expect(conseillerService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IConseiller>>();
      const conseiller = { id: 123 };
      jest.spyOn(conseillerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ conseiller });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(conseillerService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 'ABC' };
        const entity2 = { id: 'CBA' };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
