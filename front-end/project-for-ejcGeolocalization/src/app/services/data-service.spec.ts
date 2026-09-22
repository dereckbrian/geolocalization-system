
import { TestBed } from '@angular/core/testing';

import { provideHttpClient } from '@angular/common/http';

import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';

import { HttpErrorResponse } from '@angular/common/http';

import { DataService } from './data-service';

import { Tio } from '../models/tio';
import { Jovem } from '../models/jovem';
import { RotaVisita } from '../models/rota-visita';
import { RegistrarLocalizacaoRequest } from '../models/register-localizacao-request';

describe('DataService', () => {

  let service: DataService;
  let httpMock: HttpTestingController;

  const tioExemplo = {
    id: 'tio-123',
    nomeTios: 'João e Maria'
  } as Tio;

  const jovemExemplo = {
    id: 'jovem-456',
    nome: 'Pedro'
  } as Jovem;

  const visitaExemplo = {
    visitaId: 'visita-789',
    jovem: 'Pedro',
    ordem: 1,
    latitude: -16.7,
    longitude: -49.25
  } as RotaVisita;

  beforeEach(() => {

    TestBed.configureTestingModule({
      providers: [
        DataService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(DataService);

    httpMock = TestBed.inject(HttpTestingController);

  });

  afterEach(() => {
    httpMock.verify();
  });

  // 1. CRIAÇÃO DO SERVIÇO

  it('deve criar o serviço', () => {
    expect(service).toBeTruthy();
  });

  // 2. BUSCAR TIOS

  it('deve buscar os tios usando GET', () => {

    const tiosEsperados = [tioExemplo];

    service.buscarTios().subscribe(tios => {

      expect(tios).toEqual(tiosEsperados);

    });

    const req = httpMock.expectOne('/api/tio/buscar');

    expect(req.request.method).toBe('GET');

    req.flush(tiosEsperados);

  });

  // 3. BUSCAR JOVENS POR TIO

  it('deve buscar os jovens pelo ID do tio', () => {

    const tioId = 'tio-123';

    const jovensEsperados = [jovemExemplo];

    service.buscarJovensPorTio(tioId).subscribe(jovens => {

      expect(jovens).toEqual(jovensEsperados);

    });

    const req = httpMock.expectOne(
      `/api/jovem/achar/${tioId}`
    );

    expect(req.request.method).toBe('GET');

    req.flush(jovensEsperados);

  });

  // 4. REGISTRAR LOCALIZAÇÃO

  it('deve enviar os dados da localização usando POST', () => {

    const dados = {
      tioID: 'tio-123',
      jovemID: 'jovem-456',
      latitude: -16.7,
      longitude: -49.25
    } as RegistrarLocalizacaoRequest;

    const respostaEsperada = {
      id: 'registro-123'
    };

    service.registrarLocalizacao(dados).subscribe(resposta => {

      expect(resposta).toEqual(respostaEsperada);

    });

    const req = httpMock.expectOne(
      '/api/visita/cadastrar'
    );

    expect(req.request.method).toBe('POST');

    expect(req.request.body).toEqual(dados);

    req.flush(respostaEsperada);

  });

  // 5. BUSCAR ROTA

  it('deve buscar a rota usando GET', () => {

    const rotaEsperada = [visitaExemplo];

    service.buscarRota().subscribe(rota => {

      expect(rota).toEqual(rotaEsperada);

    });

    const req = httpMock.expectOne(
      '/api/visita/rota'
    );

    expect(req.request.method).toBe('GET');

    req.flush(rotaEsperada);

  });

  it('deve retornar um array vazio quando não houver rota', () => {

    service.buscarRota().subscribe(rota => {

      expect(rota).toEqual([]);

    });

    const req = httpMock.expectOne(
      '/api/visita/rota'
    );

    req.flush([]);

  });

  // 6. GERAR NOVA ROTA

  it('deve gerar uma nova rota usando POST', () => {

    const rotaEsperada = [visitaExemplo];

    service.gerarRota().subscribe(rota => {

      expect(rota).toEqual(rotaEsperada);

    });

    const req = httpMock.expectOne(
      '/api/visita/rota/gerar'
    );

    expect(req.request.method).toBe('POST');

    expect(req.request.body).toEqual({});

    req.flush(rotaEsperada);

  });

  // 7. REGISTRAR VISITA REALIZADA

  it('deve registrar sucesso usando PATCH', () => {

    const visitaId = 'visita-789';

    const respostaEsperada = {
      mensagem: 'Visita registrada'
    };

    service.registrarSucesso(visitaId).subscribe(resposta => {

      expect(resposta).toEqual(respostaEsperada);

    });

    const req = httpMock.expectOne(
      `/api/visita/${visitaId}/sucesso`
    );

    expect(req.request.method).toBe('PATCH');

    expect(req.request.body).toEqual({});

    req.flush(respostaEsperada);

  });

  // 8. REGISTRAR AUSÊNCIA

  it('deve registrar ausência usando PATCH', () => {

    const visitaId = 'visita-789';

    const respostaEsperada = {
      mensagem: 'Ausência registrada'
    };

    service.registrarFalta(visitaId).subscribe(resposta => {

      expect(resposta).toEqual(respostaEsperada);

    });

    const req = httpMock.expectOne(
      `/api/visita/${visitaId}/falta`
    );

    expect(req.request.method).toBe('PATCH');

    expect(req.request.body).toEqual({});

    req.flush(respostaEsperada);

  });

  // 9. TESTES DE ERRO HTTP


  it('deve repassar o erro quando falhar a busca de tios', () => {

    let erroRecebido: HttpErrorResponse | undefined;
    let sucessoExecutado = false;

    service.buscarTios().subscribe({

      next: () => {
        sucessoExecutado = true;
      },

      error: (erro: HttpErrorResponse) => {
        erroRecebido = erro;
      }

    });

    const req = httpMock.expectOne(
      '/api/tio/buscar'
    );

    req.flush(
      { mensagem: 'Erro no servidor' },
      {
        status: 500,
        statusText: 'Internal Server Error'
      }
    );

    expect(sucessoExecutado).toBe(false);

    expect(erroRecebido?.status).toBe(500);

    expect(erroRecebido?.error).toEqual({
      mensagem: 'Erro no servidor'
    });

  });

  it('deve repassar o erro quando falhar o cadastro', () => {

    const dados = {
      tioID: 'tio-123',
      jovemID: 'jovem-456',
      latitude: -16.7,
      longitude: -49.25
    } as RegistrarLocalizacaoRequest;

    let erroRecebido: HttpErrorResponse | undefined;
    let sucessoExecutado = false;

    service.registrarLocalizacao(dados).subscribe({

      next: () => {
        sucessoExecutado = true;
      },

      error: (erro: HttpErrorResponse) => {
        erroRecebido = erro;
      }

    });

    const req = httpMock.expectOne(
      '/api/visita/cadastrar'
    );

    req.flush(
      {
        detail: 'Invalid request content.'
      },
      {
        status: 400,
        statusText: 'Bad Request'
      }
    );

    expect(sucessoExecutado).toBe(false);
    expect(erroRecebido?.status).toBe(400);

    expect(erroRecebido?.error).toEqual({
      detail: 'Invalid request content.'
    });

  });

  // 10. COMPORTAMENTO DOS OBSERVABLES

  it('não deve fazer a requisição antes do subscribe', () => {

    const resultado = service.buscarTios();

    httpMock.expectNone('/api/tio/buscar');

    resultado.subscribe();

    const req = httpMock.expectOne(
      '/api/tio/buscar'
    );

    expect(req.request.method).toBe('GET');

    req.flush([]);

  });

});