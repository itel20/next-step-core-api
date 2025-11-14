import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import EleveResolve from './route/eleve-routing-resolve.service';

const eleveRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/eleve.component').then(m => m.EleveComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/eleve-detail.component').then(m => m.EleveDetailComponent),
    resolve: {
      eleve: EleveResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/eleve-update.component').then(m => m.EleveUpdateComponent),
    resolve: {
      eleve: EleveResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/eleve-update.component').then(m => m.EleveUpdateComponent),
    resolve: {
      eleve: EleveResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default eleveRoute;
