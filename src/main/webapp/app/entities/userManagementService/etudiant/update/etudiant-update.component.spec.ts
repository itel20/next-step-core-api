import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { EtudiantService } from '../service/etudiant.service';
import { IEtudiant } from '../etudiant.model';
import { EtudiantFormService } from './etudiant-form.service';

import { EtudiantUpdateComponent } from './etudiant-update.component';

describe('Etudiant Management Update Component', () => {
  let comp: EtudiantUpdateComponent;
  let fixture: ComponentFixture<EtudiantUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let etudiantFormService: EtudiantFormService;
  let etudiantService: EtudiantService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [EtudiantUpdateComponent],
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
      .overrideTemplate(EtudiantUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EtudiantUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    etudiantFormService = TestBed.inject(EtudiantFormService);
    etudiantService = TestBed.inject(EtudiantService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const etudiant: IEtudiant = { id: 456 };
      const user: IUser = { id: '1ea050af-0c1f-4651-a31d-5823dd0efad7' };
      etudiant.user = user;

      const userCollection: IUser[] = [{ id: 'bb8b4eb1-1d5f-4bbe-8860-d24a8d23cac5' }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ etudiant });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const etudiant: IEtudiant = { id: 456 };
      const user: IUser = { id: 'bfab96ae-9388-4934-bb41-09fc6680da74' };
      etudiant.user = user;

      activatedRoute.data = of({ etudiant });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.etudiant).toEqual(etudiant);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEtudiant>>();
      const etudiant = { id: 123 };
      jest.spyOn(etudiantFormService, 'getEtudiant').mockReturnValue(etudiant);
      jest.spyOn(etudiantService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ etudiant });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: etudiant }));
      saveSubject.complete();

      // THEN
      expect(etudiantFormService.getEtudiant).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(etudiantService.update).toHaveBeenCalledWith(expect.objectContaining(etudiant));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEtudiant>>();
      const etudiant = { id: 123 };
      jest.spyOn(etudiantFormService, 'getEtudiant').mockReturnValue({ id: null });
      jest.spyOn(etudiantService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ etudiant: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: etudiant }));
      saveSubject.complete();

      // THEN
      expect(etudiantFormService.getEtudiant).toHaveBeenCalled();
      expect(etudiantService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEtudiant>>();
      const etudiant = { id: 123 };
      jest.spyOn(etudiantService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ etudiant });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(etudiantService.update).toHaveBeenCalled();
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
