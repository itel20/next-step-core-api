import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface IEleve {
  id: number;
  nom?: string | null;
  prenom?: string | null;
  dateNaissance?: dayjs.Dayjs | null;
  telephone?: string | null;
  adresse?: string | null;
  email?: string | null;
  serie?: string | null;
  niveauEtude?: string | null;
  lycee?: string | null;
  ville?: string | null;
  password?: string | null;
  passwordHash?: string | null;
  keycloakId?: string | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewEleve = Omit<IEleve, 'id'> & { id: null };
