import dayjs from 'dayjs/esm';

import { IEleve, NewEleve } from './eleve.model';

export const sampleWithRequiredData: IEleve = {
  id: 23076,
  nom: 'brave quoique hi',
  prenom: 'afin de juriste',
  email: 'Aphelie_Mercier24@hotmail.fr',
  serie: 'patientèle',
  niveauEtude: 'lorsque rapide',
  passwordHash: 'drôlement',
};

export const sampleWithPartialData: IEleve = {
  id: 10465,
  nom: 'sans que adepte à défaut de ',
  prenom: 'clientèle population du Québec rembourser',
  email: 'Alpinien.Dupuy@yahoo.fr',
  serie: 'pschitt sincère',
  niveauEtude: 'tellement sauter tailler',
  passwordHash: 'nonobstant ouf',
};

export const sampleWithFullData: IEleve = {
  id: 5895,
  nom: 'vu que vlan hypocrite',
  prenom: 'entre',
  dateNaissance: dayjs('2025-10-14'),
  telephone: '+33 567411772',
  adresse: 'dorénavant naître',
  email: 'Taurin_Schneider66@gmail.com',
  serie: 'toujours',
  niveauEtude: 'recommander ouille',
  lycee: 'ouille vorace',
  ville: 'clac',
  password: 'sympathique profiter',
  passwordHash: 'miaou devant',
  keycloakId: "d'entre après que",
};

export const sampleWithNewData: NewEleve = {
  nom: 'ah secours',
  prenom: 'corps enseignant du moment que',
  email: 'Arlette.Poirier29@yahoo.fr',
  serie: 'vanter',
  niveauEtude: 'passer',
  passwordHash: 'disposer police',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
