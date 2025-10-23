import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface IEtudiant {
  id: number;
  nom?: string | null;
  prenom?: string | null;
  dateNaissance?: dayjs.Dayjs | null;
  telephone?: string | null;
  adresse?: string | null;
  email?: string | null;
  typeBac?: string | null;
  anneeBac?: number | null;
  niveauDetudes?: string | null;
  universiteSouhaitee?: string | null;
  specialiteSouhaitee?: string | null;
  password?: string | null;
  passwordHash?: string | null;
  keycloakId?: string | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewEtudiant = Omit<IEtudiant, 'id'> & { id: null };
