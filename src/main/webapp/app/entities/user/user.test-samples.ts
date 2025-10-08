import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: '5e89c7c0-8b33-4612-bc0d-312c218a6081',
  login: 'CdP',
};

export const sampleWithPartialData: IUser = {
  id: '213e62ae-b257-4c2d-b264-e786845282f1',
  login: 'DE@XG\\CDz\\xl',
};

export const sampleWithFullData: IUser = {
  id: '82f948ac-5a9e-4ff2-ad1e-b36618147f7d',
  login: '.@PC53zN\\j39l\\YaECg\\YEhzgt\\-lU44\\s83',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
