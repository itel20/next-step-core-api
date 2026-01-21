import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, map, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IPublication, NewPublication } from '../publication.model';

export type PartialUpdatePublication = Partial<IPublication> & Pick<IPublication, 'id'>;

type RestOf<T extends IPublication | NewPublication> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

export type RestPublication = RestOf<IPublication>;

export type NewRestPublication = RestOf<NewPublication>;

export type PartialUpdateRestPublication = RestOf<PartialUpdatePublication>;

export type EntityResponseType = HttpResponse<IPublication>;
export type EntityArrayResponseType = HttpResponse<IPublication[]>;

@Injectable({ providedIn: 'root' })
export class PublicationService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/publications', 'usermanagementservice');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/publications/_search', 'usermanagementservice');

  create(publication: NewPublication): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(publication);
    return this.http
      .post<RestPublication>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(publication: IPublication): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(publication);
    return this.http
      .put<RestPublication>(`${this.resourceUrl}/${this.getPublicationIdentifier(publication)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(publication: PartialUpdatePublication): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(publication);
    return this.http
      .patch<RestPublication>(`${this.resourceUrl}/${this.getPublicationIdentifier(publication)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestPublication>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPublication[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestPublication[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),

      catchError(() => scheduled([new HttpResponse<IPublication[]>()], asapScheduler)),
    );
  }

  getPublicationIdentifier(publication: Pick<IPublication, 'id'>): number {
    return publication.id;
  }

  comparePublication(o1: Pick<IPublication, 'id'> | null, o2: Pick<IPublication, 'id'> | null): boolean {
    return o1 && o2 ? this.getPublicationIdentifier(o1) === this.getPublicationIdentifier(o2) : o1 === o2;
  }

  addPublicationToCollectionIfMissing<Type extends Pick<IPublication, 'id'>>(
    publicationCollection: Type[],
    ...publicationsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const publications: Type[] = publicationsToCheck.filter(isPresent);
    if (publications.length > 0) {
      const publicationCollectionIdentifiers = publicationCollection.map(publicationItem => this.getPublicationIdentifier(publicationItem));
      const publicationsToAdd = publications.filter(publicationItem => {
        const publicationIdentifier = this.getPublicationIdentifier(publicationItem);
        if (publicationCollectionIdentifiers.includes(publicationIdentifier)) {
          return false;
        }
        publicationCollectionIdentifiers.push(publicationIdentifier);
        return true;
      });
      return [...publicationsToAdd, ...publicationCollection];
    }
    return publicationCollection;
  }

  protected convertDateFromClient<T extends IPublication | NewPublication | PartialUpdatePublication>(publication: T): RestOf<T> {
    return {
      ...publication,
      createdAt: publication.createdAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restPublication: RestPublication): IPublication {
    return {
      ...restPublication,
      createdAt: restPublication.createdAt ? dayjs(restPublication.createdAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestPublication>): HttpResponse<IPublication> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPublication[]>): HttpResponse<IPublication[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
