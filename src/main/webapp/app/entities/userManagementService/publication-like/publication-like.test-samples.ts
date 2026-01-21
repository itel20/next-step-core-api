import dayjs from 'dayjs/esm';

import { IPublicationLike, NewPublicationLike } from './publication-like.model';

export const sampleWithRequiredData: IPublicationLike = {
  id: 17056,
  userId: 11485,
  userType: 'ELEVE',
  likedAt: dayjs('2026-01-20T12:14'),
};

export const sampleWithPartialData: IPublicationLike = {
  id: 29538,
  userId: 9870,
  userType: 'CONSEILLER',
  likedAt: dayjs('2026-01-20T06:46'),
};

export const sampleWithFullData: IPublicationLike = {
  id: 23368,
  userId: 10047,
  userType: 'CONSEILLER',
  likedAt: dayjs('2026-01-20T18:09'),
};

export const sampleWithNewData: NewPublicationLike = {
  userId: 27339,
  userType: 'ELEVE',
  likedAt: dayjs('2026-01-20T08:24'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
