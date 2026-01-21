import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import PublicationResolve from './route/publication-routing-resolve.service';

const publicationRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/publication.component').then(m => m.PublicationComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/publication-detail.component').then(m => m.PublicationDetailComponent),
    resolve: {
      publication: PublicationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/publication-update.component').then(m => m.PublicationUpdateComponent),
    resolve: {
      publication: PublicationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/publication-update.component').then(m => m.PublicationUpdateComponent),
    resolve: {
      publication: PublicationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default publicationRoute;
