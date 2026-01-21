import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, map, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IPublicationShare, NewPublicationShare } from '../publication-share.model';

export type PartialUpdatePublicationShare = Partial<IPublicationShare> & Pick<IPublicationShare, 'id'>;

type RestOf<T extends IPublicationShare | NewPublicationShare> = Omit<T, 'sharedAt'> & {
  sharedAt?: string | null;
};

export type RestPublicationShare = RestOf<IPublicationShare>;

export type NewRestPublicationShare = RestOf<NewPublicationShare>;

export type PartialUpdateRestPublicationShare = RestOf<PartialUpdatePublicationShare>;

export type EntityResponseType = HttpResponse<IPublicationShare>;
export type EntityArrayResponseType = HttpResponse<IPublicationShare[]>;

@Injectable({ providedIn: 'root' })
export class PublicationShareService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/publication-shares', 'usermanagementservice');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/publication-shares/_search', 'usermanagementservice');

  create(publicationShare: NewPublicationShare): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(publicationShare);
    return this.http
      .post<RestPublicationShare>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(publicationShare: IPublicationShare): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(publicationShare);
    return this.http
      .put<RestPublicationShare>(`${this.resourceUrl}/${this.getPublicationShareIdentifier(publicationShare)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(publicationShare: PartialUpdatePublicationShare): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(publicationShare);
    return this.http
      .patch<RestPublicationShare>(`${this.resourceUrl}/${this.getPublicationShareIdentifier(publicationShare)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestPublicationShare>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPublicationShare[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestPublicationShare[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),

      catchError(() => scheduled([new HttpResponse<IPublicationShare[]>()], asapScheduler)),
    );
  }

  getPublicationShareIdentifier(publicationShare: Pick<IPublicationShare, 'id'>): number {
    return publicationShare.id;
  }

  comparePublicationShare(o1: Pick<IPublicationShare, 'id'> | null, o2: Pick<IPublicationShare, 'id'> | null): boolean {
    return o1 && o2 ? this.getPublicationShareIdentifier(o1) === this.getPublicationShareIdentifier(o2) : o1 === o2;
  }

  addPublicationShareToCollectionIfMissing<Type extends Pick<IPublicationShare, 'id'>>(
    publicationShareCollection: Type[],
    ...publicationSharesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const publicationShares: Type[] = publicationSharesToCheck.filter(isPresent);
    if (publicationShares.length > 0) {
      const publicationShareCollectionIdentifiers = publicationShareCollection.map(publicationShareItem =>
        this.getPublicationShareIdentifier(publicationShareItem),
      );
      const publicationSharesToAdd = publicationShares.filter(publicationShareItem => {
        const publicationShareIdentifier = this.getPublicationShareIdentifier(publicationShareItem);
        if (publicationShareCollectionIdentifiers.includes(publicationShareIdentifier)) {
          return false;
        }
        publicationShareCollectionIdentifiers.push(publicationShareIdentifier);
        return true;
      });
      return [...publicationSharesToAdd, ...publicationShareCollection];
    }
    return publicationShareCollection;
  }

  protected convertDateFromClient<T extends IPublicationShare | NewPublicationShare | PartialUpdatePublicationShare>(
    publicationShare: T,
  ): RestOf<T> {
    return {
      ...publicationShare,
      sharedAt: publicationShare.sharedAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restPublicationShare: RestPublicationShare): IPublicationShare {
    return {
      ...restPublicationShare,
      sharedAt: restPublicationShare.sharedAt ? dayjs(restPublicationShare.sharedAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestPublicationShare>): HttpResponse<IPublicationShare> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPublicationShare[]>): HttpResponse<IPublicationShare[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
