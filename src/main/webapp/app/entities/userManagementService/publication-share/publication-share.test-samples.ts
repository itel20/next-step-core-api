import dayjs from 'dayjs/esm';

import { IPublicationShare, NewPublicationShare } from './publication-share.model';

export const sampleWithRequiredData: IPublicationShare = {
  id: 1073,
  userId: 235,
  userType: 'CONSEILLER',
  sharedAt: dayjs('2026-01-20T03:57'),
};

export const sampleWithPartialData: IPublicationShare = {
  id: 6242,
  userId: 21710,
  userType: 'CONSEILLER',
  sharedAt: dayjs('2026-01-20T08:56'),
};

export const sampleWithFullData: IPublicationShare = {
  id: 17970,
  userId: 30620,
  userType: 'ELEVE',
  sharedAt: dayjs('2026-01-20T00:18'),
};

export const sampleWithNewData: NewPublicationShare = {
  userId: 7825,
  userType: 'CONSEILLER',
  sharedAt: dayjs('2026-01-20T03:21'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
