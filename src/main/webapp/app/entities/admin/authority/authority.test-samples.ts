import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: '770bd8e2-1ed0-469e-95fc-776e1dbea613',
};

export const sampleWithPartialData: IAuthority = {
  name: '48e2eaae-f63f-4eda-8540-ef09fbb677fd',
};

export const sampleWithFullData: IAuthority = {
  name: '2c19545a-60d6-486e-b03e-ba44da278a94',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
