import dayjs from 'dayjs';
import { ICars } from 'app/shared/model/cars.model';

export interface IFleets {
  id?: number;
  name?: string | null;
  description?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
  cars?: ICars[] | null;
}

export const defaultValue: Readonly<IFleets> = {};
