import { IUser } from 'app/entities/user/user.model';

export interface IConseiller {
  id: number;
  nom?: string | null;
  prenom?: string | null;
  specialite?: string | null;
  email?: string | null;
  description?: string | null;
  certifie?: boolean | null;
  password?: string | null;
  passwordHash?: string | null;
  keycloakId?: string | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewConseiller = Omit<IConseiller, 'id'> & { id: null };
