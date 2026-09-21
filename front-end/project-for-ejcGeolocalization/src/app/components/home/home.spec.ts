
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { Subject } from 'rxjs';

import { Home } from './home';
import { DataService } from '../../services/data-service';
import { Tio } from '../../models/tio';
import { Jovem } from '../../models/jovem';
import { RegistrarLocalizacaoRequest } from '../../models/register-localizacao-request';

class DataServiceFake {
  tios$ = new Subject<Tio[]>();
  jovens$ = new Subject<Jovem[]>();
  registro$ = new Subject<void>();

  consultasTios = 0;
  idsTiosConsultados: string[] = [];
  dadosEnviados: RegistrarLocalizacaoRequest[] = [];

  buscarTios() {
    this.consultasTios++;
    return this.tios$.asObservable();
  }

  buscarJovensPorTio(id: string) {
    this.idsTiosConsultados.push(id);
    return this.jovens$.asObservable();
  }

  registrarLocalizacao(dados: RegistrarLocalizacaoRequest) {
    this.dadosEnviados.push(dados);
    return this.registro$.asObservable();
  }
}

describe('Home', () => {
  let component: Home;
  let fixture: ComponentFixture<Home>;
  let dataService: DataServiceFake;

  let geolocalizacaoOriginal: PropertyDescriptor | undefined;

  const tioExemplo = {
    id: 'tio-123',
    nomeTios: 'João e Maria'
  } as Tio;

  const jovemExemplo = {
    id: 'jovem-456',
    nome: 'Pedro'
  } as Jovem;

  function preencherFormulario() {
    component.tioSelecionado = tioExemplo.id;
    component.jovemSelecionado = jovemExemplo.id;
    component.latitude = -16.7;
    component.longitude = -49.25;
  }

  function simularGPS(obterPosicao: Geolocation['getCurrentPosition']) {
    Object.defineProperty(navigator, 'geolocation', {
      configurable: true,
      value: {
        getCurrentPosition: obterPosicao
      }
    });
  }

  beforeEach(async () => {
    dataService = new DataServiceFake();

    geolocalizacaoOriginal = Object.getOwnPropertyDescriptor(
      navigator,
      'geolocation'
    );

    await TestBed.configureTestingModule({
      imports: [Home],
      providers: [
        provideRouter([]),
        {
          provide: DataService,
          useValue: dataService
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Home);
    component = fixture.componentInstance;

    fixture.detectChanges();
    await fixture.whenStable();
  });

  afterEach(() => {
    if (geolocalizacaoOriginal) {
      Object.defineProperty(
        navigator,
        'geolocation',
        geolocalizacaoOriginal
      );
    } else {
      Reflect.deleteProperty(navigator, 'geolocation');
    }
  });

  // 1. INICIALIZAÇÃO

  it('deve criar o componente', () => {
    expect(component).toBeTruthy();
  });

  it('deve buscar os tios ao iniciar', () => {
    expect(dataService.consultasTios).toBe(1);
    expect(component.carregandoTios).toBe(true);
  });

  // 2. CARREGAMENTO DOS TIOS

  it('deve carregar os tios com sucesso', () => {
    dataService.tios$.next([tioExemplo]);

    expect(component.tios).toEqual([tioExemplo]);
    expect(component.carregandoTios).toBe(false);
    expect(component.mensagemErro).toBe('');
  });

  it('deve exibir mensagem quando falhar ao carregar tios', () => {
    dataService.tios$.error(
      new Error('API indisponível')
    );

    expect(component.tios).toEqual([]);
    expect(component.carregandoTios).toBe(false);
    expect(component.mensagemErro).toBe(
      'Não foi possível carregar os tios.'
    );
  });

  // 3. CARREGAMENTO DOS JOVENS

  it('não deve buscar jovens sem um tio selecionado', () => {
    component.carregarJovens();

    expect(dataService.idsTiosConsultados.length).toBe(0);
    expect(component.jovens).toEqual([]);
  });

  it('deve carregar os jovens do tio selecionado', () => {
    component.tioSelecionado = tioExemplo.id;

    component.carregarJovens();

    expect(dataService.idsTiosConsultados).toEqual([
      tioExemplo.id
    ]);

    expect(component.carregandoJovens).toBe(true);

    dataService.jovens$.next([jovemExemplo]);

    expect(component.jovens).toEqual([jovemExemplo]);
    expect(component.carregandoJovens).toBe(false);
  });

  it('deve limpar o jovem anterior ao trocar de tio', () => {
    component.tioSelecionado = tioExemplo.id;
    component.jovemSelecionado = jovemExemplo.id;
    component.jovens = [jovemExemplo];

    component.carregarJovens();

    expect(component.jovemSelecionado).toBe('');
    expect(component.jovens).toEqual([]);
  });

  it('deve exibir mensagem quando falhar ao carregar jovens', () => {
    component.tioSelecionado = tioExemplo.id;

    component.carregarJovens();

    dataService.jovens$.error(
      new Error('Falha na consulta')
    );

    expect(component.carregandoJovens).toBe(false);
    expect(component.mensagemErro).toBe(
      'Não foi possível carregar os jovens.'
    );
  });

  // 4. GEOLOCALIZAÇÃO

  it('deve capturar latitude e longitude com sucesso', () => {
    simularGPS((sucesso) => {
      sucesso({
        coords: {
          latitude: -16.7,
          longitude: -49.25
        }
      } as GeolocationPosition);
    });

    component.capturarLocalizacao();

    expect(component.latitude).toBe(-16.7);
    expect(component.longitude).toBe(-49.25);
    expect(component.localizacaoCapturada).toBe(true);
    expect(component.capturandoLocalizacao).toBe(false);
  });

  it('deve tratar erro ao capturar a localização', () => {
    simularGPS((_sucesso, falha) => {
      falha?.({
        code: 1,
        message: 'Permissão negada'
      } as GeolocationPositionError);
    });

    component.capturarLocalizacao();

    expect(component.localizacaoCapturada).toBe(false);
    expect(component.capturandoLocalizacao).toBe(false);
    expect(component.mensagemErro).toContain(
      'Não foi possível obter sua localização'
    );
  });

  it('deve avisar quando o GPS não estiver disponível', () => {
    Object.defineProperty(navigator, 'geolocation', {
      configurable: true,
      value: undefined
    });

    component.capturarLocalizacao();

    expect(component.mensagemErro).toBe(
      'Seu dispositivo não oferece suporte à localização.'
    );

    expect(component.capturandoLocalizacao).toBe(false);
  });

  // 5. VALIDAÇÃO DO FORMULÁRIO

  it('não deve permitir envio com dados incompletos', () => {
    expect(component.podeEnviar).toBe(false);

    component.tioSelecionado = tioExemplo.id;
    component.jovemSelecionado = jovemExemplo.id;

    expect(component.podeEnviar).toBe(false);

    component.latitude = -16.7;
    component.longitude = -49.25;

    expect(component.podeEnviar).toBe(true);

    component.enviando = true;

    expect(component.podeEnviar).toBe(false);
  });

  it('deve abrir confirmação somente com formulário válido', () => {
    component.abrirConfirmacao();

    expect(component.mostrarConfirmacao).toBe(false);

    preencherFormulario();

    component.abrirConfirmacao();

    expect(component.mostrarConfirmacao).toBe(true);
  });

  it('deve cancelar o envio', () => {
    preencherFormulario();

    component.abrirConfirmacao();
    component.cancelarEnvio();

    expect(component.mostrarConfirmacao).toBe(false);
    expect(dataService.dadosEnviados.length).toBe(0);
  });

  // 6. ENVIO DA LOCALIZAÇÃO

  it('deve enviar os dados corretos ao backend', () => {
    preencherFormulario();

    component.abrirConfirmacao();
    component.confirmarEnvio();

    expect(dataService.dadosEnviados).toEqual([
      {
        tioID: tioExemplo.id,
        jovemID: jovemExemplo.id,
        latitude: -16.7,
        longitude: -49.25
      }
    ]);

    expect(component.enviando).toBe(true);
    expect(component.mostrarConfirmacao).toBe(false);
  });

  it('deve limpar o formulário após envio bem-sucedido', () => {
    preencherFormulario();

    component.confirmarEnvio();

    dataService.registro$.next();

    expect(component.mensagemSucesso).toBe(
      'Localização registrada com sucesso!'
    );

    expect(component.enviando).toBe(false);
    expect(component.tioSelecionado).toBe('');
    expect(component.jovemSelecionado).toBe('');
    expect(component.latitude).toBeUndefined();
    expect(component.longitude).toBeUndefined();
    expect(component.jovens).toEqual([]);
  });

  it('deve manter os dados quando o envio falhar', () => {
    preencherFormulario();

    component.confirmarEnvio();

    dataService.registro$.error({
      status: 400,
      error: {
        detail: 'Invalid request content.'
      }
    });

    expect(component.mensagemErro).toBe(
      'Erro ao registrar localização.'
    );

    expect(component.enviando).toBe(false);
    expect(component.tioSelecionado).toBe(tioExemplo.id);
    expect(component.jovemSelecionado).toBe(jovemExemplo.id);
    expect(component.localizacaoCapturada).toBe(true);
  });

  it('não deve enviar quando faltar a localização', () => {
    component.tioSelecionado = tioExemplo.id;
    component.jovemSelecionado = jovemExemplo.id;

    component.confirmarEnvio();

    expect(dataService.dadosEnviados.length).toBe(0);
  });

  // 7. TESTES DA INTERFACE HTML

  
  it('deve habilitar o select de jovens após selecionar um tio', async () => {

    dataService.tios$.next([tioExemplo]);

    fixture.detectChanges();
    await fixture.whenStable();

    const selectTio = fixture.nativeElement.querySelector(
      '#tio'
    ) as HTMLSelectElement;

    const selectJovem = fixture.nativeElement.querySelector(
      '#jovem'
    ) as HTMLSelectElement;

    expect(selectJovem.disabled).toBe(true);

    selectTio.value = tioExemplo.id;
    selectTio.dispatchEvent(
      new Event('change', { bubbles: true })
    );

    await fixture.whenStable();

    dataService.jovens$.next([jovemExemplo]);

    fixture.detectChanges();
    await fixture.whenStable();

    expect(component.tioSelecionado).toBe(tioExemplo.id);
    expect(component.carregandoJovens).toBe(false);
    expect(selectJovem.disabled).toBe(false);

  });

  it('deve habilitar o botão de envio somente com dados válidos', () => {
    const elemento = fixture.nativeElement as HTMLElement;

    const botao = Array.from(
      elemento.querySelectorAll('button')
    ).find((b) =>
      b.textContent?.includes('Enviar localização')
    );

    expect(botao).toBeDefined();
    expect(botao!.disabled).toBe(true);

    preencherFormulario();

    fixture.detectChanges();

    expect(botao!.disabled).toBe(false);
  });

  it('deve abrir a confirmação ao clicar no botão de envio', () => {
    preencherFormulario();

    fixture.detectChanges();

    const elemento = fixture.nativeElement as HTMLElement;

    const botao = Array.from(
      elemento.querySelectorAll('button')
    ).find((b) =>
      b.textContent?.includes('Enviar localização')
    );

    botao!.click();

    fixture.detectChanges();

    const dialogo = elemento.querySelector(
      '[role="dialog"]'
    );

    expect(dialogo).not.toBeNull();
    expect(dialogo?.textContent).toContain(
      'Confirmar localização?'
    );

    expect(dataService.dadosEnviados.length).toBe(0);
  });

  it('deve enviar somente depois do clique em Confirmar', () => {
    preencherFormulario();

    component.abrirConfirmacao();
    fixture.detectChanges();

    const elemento = fixture.nativeElement as HTMLElement;

    const dialogo = elemento.querySelector(
      '[role="dialog"]'
    );

    const botoes = dialogo!.querySelectorAll('button');

    botoes[1].click();

    expect(dataService.dadosEnviados.length).toBe(1);
    expect(component.mostrarConfirmacao).toBe(false);
  });
});