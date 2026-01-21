import dayjs from 'dayjs/esm';
import { IPublication } from 'app/entities/userManagementService/publication/publication.model';
import { UserType } from 'app/entities/enumerations/user-type.model';

export interface IPublicationLike {
  id: number;
  userId?: number | null;
  userType?: keyof typeof UserType | null;
  likedAt?: dayjs.Dayjs | null;
  publication?: Pick<IPublication, 'id'> | null;
}

export type NewPublicationLike = Omit<IPublicationLike, 'id'> & { id: null };
