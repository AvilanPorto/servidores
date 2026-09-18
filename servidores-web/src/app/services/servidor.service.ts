import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Servidor } from '../models/servidor';
import { ServidorHistorico } from '../models/servidor-historico';

export interface ServidorRequest {
  nome: string;
  email: string;
  dataNascimento: string;
  secretariaId: number;
}

@Injectable({
  providedIn: 'root'
})
export class ServidorService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl = 'http://localhost:8080/servidores';

  listar(): Observable<Servidor[]> {
    return this.http.get<Servidor[]>(this.apiUrl);
  }

  criar(request: ServidorRequest): Observable<Servidor> {
    return this.http.post<Servidor>(this.apiUrl, request);
  }

  atualizar(id: number, request: ServidorRequest): Observable<Servidor> {
    return this.http.put<Servidor>(
      `${this.apiUrl}/${id}`,
      request
    );
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }

  historico(id: number): Observable<ServidorHistorico[]> {
    return this.http.get<ServidorHistorico[]>(
      `${this.apiUrl}/${id}/historico`
    );
  }
}
