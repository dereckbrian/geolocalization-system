import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Tio } from '../models/tio';
import { Jovem } from '../models/jovem';
import { RegistrarLocalizacaoRequest } from '../models/register-localizacao-request';
import { RotaVisita } from '../models/rota-visita';

@Injectable({
  providedIn: 'root',
})
export class DataService {

  private readonly apiUrl = '/api'

  constructor(private http: HttpClient){}

  buscarTios(): Observable<Tio[]> {
    return this.http.get<Tio[]>(
      `${this.apiUrl}/tio/buscar`
    );
  }

  buscarJovensPorTio(tioID: string): Observable<Jovem[]>{
    return this.http.get<Jovem[]>(
      `${this.apiUrl}/jovem/achar/${tioID}`
    );
  }

  registrarLocalizacao(dados: RegistrarLocalizacaoRequest): Observable<unknown>{
    return this.http.post(
      `${this.apiUrl}/visita/cadastrar`, 
      dados
    );
  }

  buscarRota(): Observable<RotaVisita[]> {
  return this.http.get<RotaVisita[]>('/api/visita/rota');
  }

  gerarRota(): Observable<RotaVisita[]>{
    return this.http.post<RotaVisita[]>(`api/visita/rota/gerar`, {});
  }

  registrarSucesso(visitaId: string){
    return this.http.patch(
      `api/visita/${visitaId}/sucesso`,
      {}
    );
  }

  registrarFalta(visitaId: string){
    return this.http.patch(
      `api/visita/${visitaId}/falta`,
      {}
    );
  }

}
