import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPublication } from '../publication.model';
import { PublicationService } from '../service/publication.service';

const publicationResolve = (route: ActivatedRouteSnapshot): Observable<null | IPublication> => {
  const id = route.params.id;
  if (id) {
    return inject(PublicationService)
      .find(id)
      .pipe(
        mergeMap((publication: HttpResponse<IPublication>) => {
          if (publication.body) {
            return of(publication.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default publicationResolve;
