import dayjs from 'dayjs/esm';

import { IPublication, NewPublication } from './publication.model';

export const sampleWithRequiredData: IPublication = {
  id: 15498,
  content: '../fake-data/blob/hipster.txt',
  authorId: 19330,
  authorType: 'CONSEILLER',
  createdAt: dayjs('2026-01-20T00:10'),
};

export const sampleWithPartialData: IPublication = {
  id: 27061,
  content: '../fake-data/blob/hipster.txt',
  authorId: 15691,
  authorType: 'ETUDIANT',
  createdAt: dayjs('2026-01-20T02:32'),
};

export const sampleWithFullData: IPublication = {
  id: 6166,
  content: '../fake-data/blob/hipster.txt',
  authorId: 28675,
  authorType: 'CONSEILLER',
  createdAt: dayjs('2026-01-20T09:44'),
};

export const sampleWithNewData: NewPublication = {
  content: '../fake-data/blob/hipster.txt',
  authorId: 13763,
  authorType: 'ELEVE',
  createdAt: dayjs('2026-01-20T08:01'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
