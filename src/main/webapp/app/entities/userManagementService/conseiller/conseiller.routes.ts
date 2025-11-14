import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ConseillerResolve from './route/conseiller-routing-resolve.service';

const conseillerRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/conseiller.component').then(m => m.ConseillerComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/conseiller-detail.component').then(m => m.ConseillerDetailComponent),
    resolve: {
      conseiller: ConseillerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/conseiller-update.component').then(m => m.ConseillerUpdateComponent),
    resolve: {
      conseiller: ConseillerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/conseiller-update.component').then(m => m.ConseillerUpdateComponent),
    resolve: {
      conseiller: ConseillerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default conseillerRoute;
