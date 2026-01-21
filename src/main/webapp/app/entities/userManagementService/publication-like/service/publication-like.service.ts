import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, map, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IPublicationLike, NewPublicationLike } from '../publication-like.model';

export type PartialUpdatePublicationLike = Partial<IPublicationLike> & Pick<IPublicationLike, 'id'>;

type RestOf<T extends IPublicationLike | NewPublicationLike> = Omit<T, 'likedAt'> & {
  likedAt?: string | null;
};

export type RestPublicationLike = RestOf<IPublicationLike>;

export type NewRestPublicationLike = RestOf<NewPublicationLike>;

export type PartialUpdateRestPublicationLike = RestOf<PartialUpdatePublicationLike>;

export type EntityResponseType = HttpResponse<IPublicationLike>;
export type EntityArrayResponseType = HttpResponse<IPublicationLike[]>;

@Injectable({ providedIn: 'root' })
export class PublicationLikeService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/publication-likes', 'usermanagementservice');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/publication-likes/_search', 'usermanagementservice');

  create(publicationLike: NewPublicationLike): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(publicationLike);
    return this.http
      .post<RestPublicationLike>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(publicationLike: IPublicationLike): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(publicationLike);
    return this.http
      .put<RestPublicationLike>(`${this.resourceUrl}/${this.getPublicationLikeIdentifier(publicationLike)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(publicationLike: PartialUpdatePublicationLike): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(publicationLike);
    return this.http
      .patch<RestPublicationLike>(`${this.resourceUrl}/${this.getPublicationLikeIdentifier(publicationLike)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestPublicationLike>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPublicationLike[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestPublicationLike[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),

      catchError(() => scheduled([new HttpResponse<IPublicationLike[]>()], asapScheduler)),
    );
  }

  getPublicationLikeIdentifier(publicationLike: Pick<IPublicationLike, 'id'>): number {
    return publicationLike.id;
  }

  comparePublicationLike(o1: Pick<IPublicationLike, 'id'> | null, o2: Pick<IPublicationLike, 'id'> | null): boolean {
    return o1 && o2 ? this.getPublicationLikeIdentifier(o1) === this.getPublicationLikeIdentifier(o2) : o1 === o2;
  }

  addPublicationLikeToCollectionIfMissing<Type extends Pick<IPublicationLike, 'id'>>(
    publicationLikeCollection: Type[],
    ...publicationLikesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const publicationLikes: Type[] = publicationLikesToCheck.filter(isPresent);
    if (publicationLikes.length > 0) {
      const publicationLikeCollectionIdentifiers = publicationLikeCollection.map(publicationLikeItem =>
        this.getPublicationLikeIdentifier(publicationLikeItem),
      );
      const publicationLikesToAdd = publicationLikes.filter(publicationLikeItem => {
        const publicationLikeIdentifier = this.getPublicationLikeIdentifier(publicationLikeItem);
        if (publicationLikeCollectionIdentifiers.includes(publicationLikeIdentifier)) {
          return false;
        }
        publicationLikeCollectionIdentifiers.push(publicationLikeIdentifier);
        return true;
      });
      return [...publicationLikesToAdd, ...publicationLikeCollection];
    }
    return publicationLikeCollection;
  }

  protected convertDateFromClient<T extends IPublicationLike | NewPublicationLike | PartialUpdatePublicationLike>(
    publicationLike: T,
  ): RestOf<T> {
    return {
      ...publicationLike,
      likedAt: publicationLike.likedAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restPublicationLike: RestPublicationLike): IPublicationLike {
    return {
      ...restPublicationLike,
      likedAt: restPublicationLike.likedAt ? dayjs(restPublicationLike.likedAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestPublicationLike>): HttpResponse<IPublicationLike> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPublicationLike[]>): HttpResponse<IPublicationLike[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
