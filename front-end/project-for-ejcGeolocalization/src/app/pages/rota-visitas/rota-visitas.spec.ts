
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { Subject } from 'rxjs';

import { RotaVisitas } from './rota-visitas';
import { DataService } from '../../services/data-service';
import { RotaVisita } from '../../models/rota-visita';

class DataServiceFake {
  rota$ = new Subject<RotaVisita[]>();
  novaRota$ = new Subject<RotaVisita[]>();
  sucesso$ = new Subject<void>();
  falta$ = new Subject<void>();

  consultasRota = 0;
  geracoesRota = 0;

  visitasRegistradas: string[] = [];
  ausenciasRegistradas: string[] = [];

  buscarRota() {
    this.consultasRota++;
    return this.rota$.asObservable();
  }

  gerarRota() {
    this.geracoesRota++;
    return this.novaRota$.asObservable();
  }

  registrarSucesso(visitaId: string) {
    this.visitasRegistradas.push(visitaId);
    return this.sucesso$.asObservable();
  }

  registrarFalta(visitaId: string) {
    this.ausenciasRegistradas.push(visitaId);
    return this.falta$.asObservable();
  }
}

describe('RotaVisitas', () => {
  let component: RotaVisitas;
  let fixture: ComponentFixture<RotaVisitas>;
  let dataService: DataServiceFake;

  function criarVisita(numero: number): RotaVisita {
    return {
      visitaId: `visita-${numero}`,
      jovem: `Jovem ${numero}`,
      ordem: numero,
      latitude: -16.7,
      longitude: -49.25
    } as RotaVisita;
  }

  const visita1 = criarVisita(1);
  const visita2 = criarVisita(2);

  beforeEach(async () => {
    dataService = new DataServiceFake();

    await TestBed.configureTestingModule({
      imports: [RotaVisitas],
      providers: [
        provideRouter([]),
        {
          provide: DataService,
          useValue: dataService
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RotaVisitas);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  // 1. INICIALIZAÇÃO

  it('deve criar o componente', () => {
    expect(component).toBeTruthy();
  });

  it('deve buscar a rota ao iniciar', () => {
    expect(dataService.consultasRota).toBe(1);
    expect(component.carregando).toBe(true);
  });

  // 2. CARREGAMENTO DA ROTA

  it('deve carregar a rota com sucesso', () => {
    dataService.rota$.next([visita1, visita2]);

    expect(component.rota).toEqual([
      visita1,
      visita2
    ]);

    expect(component.carregando).toBe(false);
    expect(component.erro).toBe('');
    expect(component.paginaAtual).toBe(1);
  });

  it('deve exibir erro quando falhar ao buscar a rota', () => {
    dataService.rota$.error(
      new Error('Falha no servidor')
    );

    expect(component.carregando).toBe(false);

    expect(component.erro).toBe(
      'Não foi possível carregar a rota.'
    );
  });

  it('deve limpar o erro ao tentar carregar novamente', () => {
    component.erro = 'Erro anterior';

    component.carregarRota();

    expect(component.erro).toBe('');
    expect(component.carregando).toBe(true);
    expect(dataService.consultasRota).toBe(2);
  });

  // 3. PAGINAÇÃO

  it('deve calcular corretamente o total de páginas', () => {
    component.rota = Array.from(
      { length: 21 },
      (_, i) => criarVisita(i + 1)
    );

    expect(component.totalPaginas).toBe(3);
  });

  it('deve retornar zero páginas quando a rota estiver vazia', () => {
    component.rota = [];

    expect(component.totalPaginas).toBe(0);
    expect(component.visitasPaginadas).toEqual([]);
  });

  it('deve mostrar somente os itens da página atual', () => {
    component.rota = Array.from(
      { length: 15 },
      (_, i) => criarVisita(i + 1)
    );

    component.paginaAtual = 1;

    expect(component.visitasPaginadas.length).toBe(10);

    expect(
      component.visitasPaginadas[0].visitaId
    ).toBe('visita-1');

    component.paginaAtual = 2;

    expect(component.visitasPaginadas.length).toBe(5);

    expect(
      component.visitasPaginadas[0].visitaId
    ).toBe('visita-11');
  });

  it('deve avançar para a próxima página', () => {
    component.rota = Array.from(
      { length: 15 },
      (_, i) => criarVisita(i + 1)
    );

    component.proximaPagina();

    expect(component.paginaAtual).toBe(2);
  });

  it('não deve avançar além da última página', () => {
    component.rota = Array.from(
      { length: 15 },
      (_, i) => criarVisita(i + 1)
    );

    component.paginaAtual = 2;

    component.proximaPagina();

    expect(component.paginaAtual).toBe(2);
  });

  it('deve voltar para a página anterior', () => {
    component.paginaAtual = 2;

    component.paginaAnterior();

    expect(component.paginaAtual).toBe(1);
  });

  it('não deve voltar antes da primeira página', () => {
    component.paginaAtual = 1;

    component.paginaAnterior();

    expect(component.paginaAtual).toBe(1);
  });

  // 4. CONFIRMAÇÃO DAS AÇÕES

  it('deve abrir confirmação para visita realizada', () => {
    component.abrirConfirmacao(
      'visitado',
      visita1
    );

    expect(component.mostrarConfirmacao).toBe(true);
    expect(component.acaoPendente).toBe('visitado');
    expect(component.visitaSelecionada).toEqual(visita1);

    expect(component.mensagemConfirmacao).toBe(
      'Confirmar visita realizada para Jovem 1?'
    );
  });

  it('deve abrir confirmação para ausência', () => {
    component.abrirConfirmacao(
      'ausente',
      visita1
    );

    expect(component.mensagemConfirmacao).toBe(
      'Confirmar que Jovem 1 estava ausente?'
    );
  });

  it('deve abrir confirmação para gerar uma nova rota', () => {
    component.abrirConfirmacao('gerarRota');

    expect(component.mostrarConfirmacao).toBe(true);
    expect(component.acaoPendente).toBe('gerarRota');
    expect(component.visitaSelecionada).toBeNull();

    expect(component.mensagemConfirmacao).toBe(
      'Gerar uma nova rota com todas as visitas pendentes?'
    );
  });

  it('deve cancelar uma ação sem chamar o backend', () => {
    component.abrirConfirmacao(
      'visitado',
      visita1
    );

    component.cancelarConfirmacao();

    expect(component.mostrarConfirmacao).toBe(false);
    expect(component.acaoPendente).toBeNull();
    expect(component.visitaSelecionada).toBeNull();

    expect(dataService.visitasRegistradas.length).toBe(0);
  });

  it('deve registrar visita somente após confirmação', () => {
    component.abrirConfirmacao(
      'visitado',
      visita1
    );

    expect(dataService.visitasRegistradas.length).toBe(0);

    component.confirmarAcao();

    expect(dataService.visitasRegistradas).toEqual([
      visita1.visitaId
    ]);

    expect(component.mostrarConfirmacao).toBe(false);
    expect(component.acaoPendente).toBeNull();
    expect(component.visitaSelecionada).toBeNull();
  });

  it('deve registrar ausência após confirmação', () => {
    component.abrirConfirmacao(
      'ausente',
      visita2
    );

    component.confirmarAcao();

    expect(dataService.ausenciasRegistradas).toEqual([
      visita2.visitaId
    ]);
  });

  
  it('deve cancelar a confirmação pelo botão do HTML', async () => {

    dataService.rota$.next([visita1]);

    fixture.detectChanges();
    await fixture.whenStable();

    const elemento = fixture.nativeElement as HTMLElement;

    const botaoVisitado = Array.from(
      elemento.querySelectorAll('button')
    ).find(
      b => b.textContent?.trim() === 'Visitado'
    );

    expect(botaoVisitado).toBeDefined();

    botaoVisitado!.click();

    await fixture.whenStable();
    fixture.detectChanges();

    expect(component.mostrarConfirmacao).toBe(true);

    const botaoCancelar = Array.from(
      elemento.querySelectorAll('button')
    ).find(
      b => b.textContent?.trim() === 'Cancelar'
    );

    expect(botaoCancelar).toBeDefined();

    botaoCancelar!.click();

    await fixture.whenStable();
    fixture.detectChanges();

    expect(component.mostrarConfirmacao).toBe(false);

    expect(component.acaoPendente).toBeNull();

    expect(component.visitaSelecionada).toBeNull();


    expect(dataService.visitasRegistradas.length).toBe(0);

  });

  // 5. GERAÇÃO DE UMA NOVA ROTA

  it('deve gerar uma nova rota com sucesso', () => {
    dataService.rota$.next([visita1]);

    component.paginaAtual = 2;

    component.gerarNovaRota();

    expect(component.carregando).toBe(true);
    expect(dataService.geracoesRota).toBe(1);

    // Simula a resposta do backend.
    dataService.novaRota$.next([
      visita1,
      visita2
    ]);

    expect(component.rota).toEqual([
      visita1,
      visita2
    ]);

    expect(component.paginaAtual).toBe(1);
    expect(component.carregando).toBe(false);
    expect(component.erro).toBe('');
  });

  it('não deve gerar outra rota enquanto estiver carregando', () => {
    component.gerarNovaRota();

    expect(dataService.geracoesRota).toBe(0);
    expect(component.carregando).toBe(true);
  });

  it('deve exibir erro quando falhar a geração da rota', () => {
    dataService.rota$.next([]);

    component.gerarNovaRota();

    dataService.novaRota$.error(
      new Error('Erro no OSRM')
    );

    expect(component.carregando).toBe(false);

    expect(component.erro).toBe(
      'Não foi possível gerar a rota.'
    );
  });

  // 6. REGISTRAR VISITA REALIZADA

  it('deve remover a visita da tela após registrar sucesso', () => {
    component.rota = [
      visita1,
      visita2
    ];

    component.registrarVisitado(visita1);

    expect(dataService.visitasRegistradas).toEqual([
      visita1.visitaId
    ]);

    expect(component.visitaEmProcessamentoId).toBe(
      visita1.visitaId
    );

    dataService.sucesso$.next();

    expect(component.rota).toEqual([visita2]);

    expect(component.visitaEmProcessamentoId).toBeNull();
  });

  it('deve manter a visita na tela se o registro falhar', () => {
    component.rota = [visita1];

    component.registrarVisitado(visita1);

    dataService.sucesso$.error(
      new Error('Erro ao atualizar visita')
    );

    expect(component.rota).toEqual([visita1]);

    expect(component.visitaEmProcessamentoId).toBeNull();

    expect(component.erro).toBe(
      'Não foi possível registrar a visita.'
    );
  });

  it('não deve processar duas visitas simultaneamente', () => {
    component.registrarVisitado(visita1);

    // Tenta registrar outra visita antes da resposta.
    component.registrarVisitado(visita2);

    expect(dataService.visitasRegistradas).toEqual([
      visita1.visitaId
    ]);

    expect(component.visitaEmProcessamentoId).toBe(
      visita1.visitaId
    );
  });

  // 7. REGISTRAR AUSÊNCIA

  it('deve remover a visita após registrar ausência', () => {
    component.rota = [
      visita1,
      visita2
    ];

    component.registrarAusente(visita1);

    expect(dataService.ausenciasRegistradas).toEqual([
      visita1.visitaId
    ]);

    dataService.falta$.next();

    expect(component.rota).toEqual([visita2]);

    expect(component.visitaEmProcessamentoId).toBeNull();
  });

  it('deve manter a visita se o registro de ausência falhar', () => {
    component.rota = [visita1];

    component.registrarAusente(visita1);

    dataService.falta$.error(
      new Error('Falha ao registrar ausência')
    );

    expect(component.rota).toEqual([visita1]);

    expect(component.visitaEmProcessamentoId).toBeNull();

    expect(component.erro).toBe(
      'Não foi possível registrar a ausência.'
    );
  });

  // 8. REMOÇÃO E PAGINAÇÃO

  it('deve voltar uma página ao remover o último item', () => {
    component.rota = Array.from(
      { length: 11 },
      (_, i) => criarVisita(i + 1)
    );

    component.paginaAtual = 2;

    const ultimaVisita = component.rota[10];

    component.registrarVisitado(ultimaVisita);

    dataService.sucesso$.next();

    expect(component.rota.length).toBe(10);
    expect(component.totalPaginas).toBe(1);
    expect(component.paginaAtual).toBe(1);
  });

  // 9. INTERFACE HTML

  it('deve mostrar mensagem quando não houver rota', () => {
    dataService.rota$.next([]);

    fixture.detectChanges();

    const elemento = fixture.nativeElement as HTMLElement;

    expect(elemento.textContent).toContain(
      'Nenhuma rota em andamento'
    );

    expect(elemento.textContent).toContain(
      'Gerar nova rota'
    );
  });

  it('deve mostrar as visitas retornadas pelo backend', () => {
    dataService.rota$.next([
      visita1,
      visita2
    ]);

    fixture.detectChanges();

    const elemento = fixture.nativeElement as HTMLElement;

    expect(elemento.textContent).toContain('Jovem 1');
    expect(elemento.textContent).toContain('Jovem 2');
  });

  it('deve abrir confirmação ao clicar em Visitado', () => {
    dataService.rota$.next([visita1]);

    fixture.detectChanges();

    const elemento = fixture.nativeElement as HTMLElement;

    const botao = Array.from(
      elemento.querySelectorAll('button')
    ).find(
      b => b.textContent?.trim() === 'Visitado'
    );

    expect(botao).toBeDefined();

    botao!.click();

    fixture.detectChanges();

    expect(component.mostrarConfirmacao).toBe(true);

    expect(elemento.textContent).toContain(
      'Confirmar visita realizada para Jovem 1?'
    );

    expect(dataService.visitasRegistradas.length).toBe(0);
  });

});