import { Secretaria } from './secretaria';

export interface ServidorHistorico {
  id: number;
  sequencial: number;
  tipoEvento: string;
  dataEvento: string;
  secretariaOrigem: Secretaria | null;
  secretariaDestino: Secretaria | null;
}
