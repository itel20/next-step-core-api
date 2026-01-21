import dayjs from 'dayjs/esm';
import { UserType } from 'app/entities/enumerations/user-type.model';

export interface IPublication {
  id: number;
  content?: string | null;
  authorId?: number | null;
  authorType?: keyof typeof UserType | null;
  createdAt?: dayjs.Dayjs | null;
}

export type NewPublication = Omit<IPublication, 'id'> & { id: null };
