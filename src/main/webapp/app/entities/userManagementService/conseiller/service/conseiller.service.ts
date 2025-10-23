import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IConseiller, NewConseiller } from '../conseiller.model';

export type PartialUpdateConseiller = Partial<IConseiller> & Pick<IConseiller, 'id'>;

export type EntityResponseType = HttpResponse<IConseiller>;
export type EntityArrayResponseType = HttpResponse<IConseiller[]>;

@Injectable({ providedIn: 'root' })
export class ConseillerService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/conseillers', 'usermanagementservice');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/conseillers/_search', 'usermanagementservice');

  create(conseiller: NewConseiller): Observable<EntityResponseType> {
    return this.http.post<IConseiller>(this.resourceUrl, conseiller, { observe: 'response' });
  }

  update(conseiller: IConseiller): Observable<EntityResponseType> {
    return this.http.put<IConseiller>(`${this.resourceUrl}/${this.getConseillerIdentifier(conseiller)}`, conseiller, {
      observe: 'response',
    });
  }

  partialUpdate(conseiller: PartialUpdateConseiller): Observable<EntityResponseType> {
    return this.http.patch<IConseiller>(`${this.resourceUrl}/${this.getConseillerIdentifier(conseiller)}`, conseiller, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IConseiller>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IConseiller[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IConseiller[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IConseiller[]>()], asapScheduler)));
  }

  getConseillerIdentifier(conseiller: Pick<IConseiller, 'id'>): number {
    return conseiller.id;
  }

  compareConseiller(o1: Pick<IConseiller, 'id'> | null, o2: Pick<IConseiller, 'id'> | null): boolean {
    return o1 && o2 ? this.getConseillerIdentifier(o1) === this.getConseillerIdentifier(o2) : o1 === o2;
  }

  addConseillerToCollectionIfMissing<Type extends Pick<IConseiller, 'id'>>(
    conseillerCollection: Type[],
    ...conseillersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const conseillers: Type[] = conseillersToCheck.filter(isPresent);
    if (conseillers.length > 0) {
      const conseillerCollectionIdentifiers = conseillerCollection.map(conseillerItem => this.getConseillerIdentifier(conseillerItem));
      const conseillersToAdd = conseillers.filter(conseillerItem => {
        const conseillerIdentifier = this.getConseillerIdentifier(conseillerItem);
        if (conseillerCollectionIdentifiers.includes(conseillerIdentifier)) {
          return false;
        }
        conseillerCollectionIdentifiers.push(conseillerIdentifier);
        return true;
      });
      return [...conseillersToAdd, ...conseillerCollection];
    }
    return conseillerCollection;
  }
}
