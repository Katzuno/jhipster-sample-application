import dayjs from 'dayjs';

export interface ICodes {
  id?: number;
  mode?: string | null;
  segment?: string | null;
  code?: string | null;
  dimension?: number | null;
  description?: string | null;
  minVal?: string | null;
  maxVal?: string | null;
  units?: string | null;
  enabled?: boolean | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export const defaultValue: Readonly<ICodes> = {
  enabled: false,
};
