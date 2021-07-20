import dayjs from 'dayjs';
import { IFleets } from 'app/shared/model/fleets.model';

export interface ICars {
  id?: number;
  name?: string | null;
  description?: string | null;
  carPlate?: string | null;
  vin?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
  fleetId?: IFleets | null;
}

export const defaultValue: Readonly<ICars> = {};
