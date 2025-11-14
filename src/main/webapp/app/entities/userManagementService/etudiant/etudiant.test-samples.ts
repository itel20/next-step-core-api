import dayjs from 'dayjs/esm';

import { IEtudiant, NewEtudiant } from './etudiant.model';

export const sampleWithRequiredData: IEtudiant = {
  id: 8752,
  nom: 'pas mal',
  prenom: 'pleurer',
  email: 'Germaine54@yahoo.fr',
  typeBac: 'fidèle avant de',
  passwordHash: 'chef',
};

export const sampleWithPartialData: IEtudiant = {
  id: 10255,
  nom: 'contre de crainte que',
  prenom: 'balancer',
  telephone: '0295316231',
  email: 'Julia2@gmail.com',
  typeBac: "police placide à l'entour de",
  niveauDetudes: 'sitôt que à cause de',
  universiteSouhaitee: 'quant à diététiste',
  password: 'étrangler',
  passwordHash: 'bientôt collègue',
};

export const sampleWithFullData: IEtudiant = {
  id: 6316,
  nom: 'derrière clientèle cocorico',
  prenom: 'présidence responsable pour que',
  dateNaissance: dayjs('2025-10-13'),
  telephone: '0270277069',
  adresse: 'en dehors de',
  email: 'Adonise.Joly@hotmail.fr',
  typeBac: 'rectorat',
  anneeBac: 11986,
  niveauDetudes: 'devant près de au dépens de',
  universiteSouhaitee: 'premièrement rose',
  specialiteSouhaitee: 'malade là',
  password: 'équipe de recherche',
  passwordHash: 'veiller',
  keycloakId: 'dans la mesure où subito multiplier',
};

export const sampleWithNewData: NewEtudiant = {
  nom: 'tant pleurer dehors',
  prenom: 'aimable souhaiter',
  email: 'Norbert53@yahoo.fr',
  typeBac: 'malade fonctionnaire',
  passwordHash: 'décourager à bas de',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
