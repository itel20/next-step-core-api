import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPublicationLike } from '../publication-like.model';
import { PublicationLikeService } from '../service/publication-like.service';

const publicationLikeResolve = (route: ActivatedRouteSnapshot): Observable<null | IPublicationLike> => {
  const id = route.params.id;
  if (id) {
    return inject(PublicationLikeService)
      .find(id)
      .pipe(
        mergeMap((publicationLike: HttpResponse<IPublicationLike>) => {
          if (publicationLike.body) {
            return of(publicationLike.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default publicationLikeResolve;
