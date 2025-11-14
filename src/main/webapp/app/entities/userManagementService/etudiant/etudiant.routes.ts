import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import EtudiantResolve from './route/etudiant-routing-resolve.service';

const etudiantRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/etudiant.component').then(m => m.EtudiantComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/etudiant-detail.component').then(m => m.EtudiantDetailComponent),
    resolve: {
      etudiant: EtudiantResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/etudiant-update.component').then(m => m.EtudiantUpdateComponent),
    resolve: {
      etudiant: EtudiantResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/etudiant-update.component').then(m => m.EtudiantUpdateComponent),
    resolve: {
      etudiant: EtudiantResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default etudiantRoute;
