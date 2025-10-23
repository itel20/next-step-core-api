import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { EleveService } from '../service/eleve.service';
import { IEleve } from '../eleve.model';
import { EleveFormService } from './eleve-form.service';

import { EleveUpdateComponent } from './eleve-update.component';

describe('Eleve Management Update Component', () => {
  let comp: EleveUpdateComponent;
  let fixture: ComponentFixture<EleveUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let eleveFormService: EleveFormService;
  let eleveService: EleveService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [EleveUpdateComponent],
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
      .overrideTemplate(EleveUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EleveUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    eleveFormService = TestBed.inject(EleveFormService);
    eleveService = TestBed.inject(EleveService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const eleve: IEleve = { id: 456 };
      const user: IUser = { id: 'bff25a21-9b74-4ac0-b5cc-1cbf0bd9cbb2' };
      eleve.user = user;

      const userCollection: IUser[] = [{ id: 'bf70bee8-39e7-4e8a-b556-4b9f6c813aa6' }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ eleve });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const eleve: IEleve = { id: 456 };
      const user: IUser = { id: 'c5a18716-5d51-46b8-83ca-ff21d65bdb3c' };
      eleve.user = user;

      activatedRoute.data = of({ eleve });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.eleve).toEqual(eleve);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEleve>>();
      const eleve = { id: 123 };
      jest.spyOn(eleveFormService, 'getEleve').mockReturnValue(eleve);
      jest.spyOn(eleveService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ eleve });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: eleve }));
      saveSubject.complete();

      // THEN
      expect(eleveFormService.getEleve).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(eleveService.update).toHaveBeenCalledWith(expect.objectContaining(eleve));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEleve>>();
      const eleve = { id: 123 };
      jest.spyOn(eleveFormService, 'getEleve').mockReturnValue({ id: null });
      jest.spyOn(eleveService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ eleve: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: eleve }));
      saveSubject.complete();

      // THEN
      expect(eleveFormService.getEleve).toHaveBeenCalled();
      expect(eleveService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEleve>>();
      const eleve = { id: 123 };
      jest.spyOn(eleveService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ eleve });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(eleveService.update).toHaveBeenCalled();
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
