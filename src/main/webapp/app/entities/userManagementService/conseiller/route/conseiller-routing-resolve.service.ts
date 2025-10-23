import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IConseiller } from '../conseiller.model';
import { ConseillerService } from '../service/conseiller.service';

const conseillerResolve = (route: ActivatedRouteSnapshot): Observable<null | IConseiller> => {
  const id = route.params.id;
  if (id) {
    return inject(ConseillerService)
      .find(id)
      .pipe(
        mergeMap((conseiller: HttpResponse<IConseiller>) => {
          if (conseiller.body) {
            return of(conseiller.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default conseillerResolve;
