import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Secretaria } from '../models/secretaria';

export interface SecretariaRequest {
  nome: string;
  sigla: string;
}

@Injectable({
  providedIn: 'root'
})
export class SecretariaService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl = 'http://localhost:8080/secretarias';

  listar(): Observable<Secretaria[]> {
    return this.http.get<Secretaria[]>(this.apiUrl);
  }

  criar(request: SecretariaRequest): Observable<Secretaria> {
    return this.http.post<Secretaria>(this.apiUrl, request);
  }

  atualizar(
    id: number,
    request: SecretariaRequest
  ): Observable<Secretaria> {
    return this.http.put<Secretaria>(
      `${this.apiUrl}/${id}`,
      request
    );
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }
}
