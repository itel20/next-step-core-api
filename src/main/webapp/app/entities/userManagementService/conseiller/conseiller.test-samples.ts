import { IConseiller, NewConseiller } from './conseiller.model';

export const sampleWithRequiredData: IConseiller = {
  id: 27137,
  nom: 'afin que',
  prenom: 'blablabla assez',
  specialite: 'approcher',
  email: 'Paterne97@gmail.com',
  passwordHash: 'antagoniste alentour',
};

export const sampleWithPartialData: IConseiller = {
  id: 8835,
  nom: 'pendant que',
  prenom: 'du moment que quelque',
  specialite: 'insolite délégation',
  email: 'Penelope86@yahoo.fr',
  certifie: true,
  passwordHash: 'certainement',
};

export const sampleWithFullData: IConseiller = {
  id: 14909,
  nom: 'presser bè',
  prenom: 'pschitt',
  specialite: 'vouh',
  email: 'Lothaire72@gmail.com',
  description: 'au-dessous diplomate accepter',
  certifie: false,
  password: 'sans coac coac',
  passwordHash: 'chasser toutefois',
  keycloakId: 'rire diététiste',
};

export const sampleWithNewData: NewConseiller = {
  nom: 'premièrement',
  prenom: 'gratter',
  specialite: 'fidèle',
  email: 'Herbert25@yahoo.fr',
  passwordHash: 'turquoise',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
