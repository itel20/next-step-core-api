import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPublicationShare } from '../publication-share.model';
import { PublicationShareService } from '../service/publication-share.service';

const publicationShareResolve = (route: ActivatedRouteSnapshot): Observable<null | IPublicationShare> => {
  const id = route.params.id;
  if (id) {
    return inject(PublicationShareService)
      .find(id)
      .pipe(
        mergeMap((publicationShare: HttpResponse<IPublicationShare>) => {
          if (publicationShare.body) {
            return of(publicationShare.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default publicationShareResolve;
