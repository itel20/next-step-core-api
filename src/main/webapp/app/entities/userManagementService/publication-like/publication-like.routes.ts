import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import PublicationLikeResolve from './route/publication-like-routing-resolve.service';

const publicationLikeRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/publication-like.component').then(m => m.PublicationLikeComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/publication-like-detail.component').then(m => m.PublicationLikeDetailComponent),
    resolve: {
      publicationLike: PublicationLikeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/publication-like-update.component').then(m => m.PublicationLikeUpdateComponent),
    resolve: {
      publicationLike: PublicationLikeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/publication-like-update.component').then(m => m.PublicationLikeUpdateComponent),
    resolve: {
      publicationLike: PublicationLikeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default publicationLikeRoute;
