import { format } from 'date-fns';

type DateInput = string | number | Date;

export const formatDateForInput = (date: DateInput) => format(new Date(date), 'yyyy-MM-dd');

export const formatDateForDisplay = (date: DateInput) => format(new Date(date), 'dd.MM.yyyy');

export const formatDateForDisplayWithTime = (date: DateInput) =>
  format(new Date(date), 'dd.MM.yyyy HH:mm');
