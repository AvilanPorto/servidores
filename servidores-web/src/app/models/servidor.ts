import { Secretaria } from './secretaria';

export interface Servidor {
  id: number;
  nome: string;
  email: string;
  dataNascimento: string;
  ativo: boolean;
  secretaria: Secretaria;
}
