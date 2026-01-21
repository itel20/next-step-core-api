import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import PublicationShareResolve from './route/publication-share-routing-resolve.service';

const publicationShareRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/publication-share.component').then(m => m.PublicationShareComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/publication-share-detail.component').then(m => m.PublicationShareDetailComponent),
    resolve: {
      publicationShare: PublicationShareResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/publication-share-update.component').then(m => m.PublicationShareUpdateComponent),
    resolve: {
      publicationShare: PublicationShareResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/publication-share-update.component').then(m => m.PublicationShareUpdateComponent),
    resolve: {
      publicationShare: PublicationShareResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default publicationShareRoute;
