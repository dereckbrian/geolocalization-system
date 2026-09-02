import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Tio } from '../models/tio';
import { Jovem } from '../models/jovem';
import { RegistrarLocalizacaoRequest } from '../models/register-localizacao-request';

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

  registrarLocalizacao(
    dados: RegistrarLocalizacaoRequest
  ): Observable<unknown>{

    return this.http.post(
      `${this.apiUrl}/visita/cadastrar`, 
      dados
    );
  }

}
