import {
  ChangeDetectorRef,
  Component,
  OnInit,
  inject
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

import { DataService } from '../../services/data-service';
import { RotaVisita } from '../../models/rota-visita';

@Component({
  selector: 'app-rota-visitas',

  imports: [
    CommonModule,
    RouterLink
  ],

  templateUrl: './rota-visitas.html',
  styleUrl: './rota-visitas.css',
})
export class RotaVisitas implements OnInit {

  private dataService = inject(DataService);
  private cdr = inject(ChangeDetectorRef);

  rota: RotaVisita[] = [];

  carregando = false;
  erro = '';

  paginaAtual = 1;
  itensPorPagina = 10;

  visitaEmProcessamentoId: string | null = null;

  mostrarConfirmacao = false;
  acaoPendente: 'visitado' | 'ausente' | 'gerarRota' | null = null;
  visitaSelecionada: RotaVisita | null = null;

  get totalPaginas(): number {
    return Math.ceil(
      this.rota.length / this.itensPorPagina
    );
  }

  get visitasPaginadas(): RotaVisita[] {
    const inicio =
      (this.paginaAtual - 1) * this.itensPorPagina;

    const fim =
      inicio + this.itensPorPagina;

    return this.rota.slice(inicio, fim);
  }

  get mensagemConfirmacao(): string {

    if (this.acaoPendente === 'visitado') {
      return `Confirmar visita realizada para ${this.visitaSelecionada?.jovem}?`;
    }

    if (this.acaoPendente === 'ausente') {
      return `Confirmar que ${this.visitaSelecionada?.jovem} estava ausente?`;
    }

    if (this.acaoPendente === 'gerarRota') {
      return 'Gerar uma nova rota com todas as visitas pendentes?';
    }
    return '';
  }

  ngOnInit(): void {
    this.carregarRota();
  }

  abrirConfirmacao(acao: 'visitado' | 'ausente' | 'gerarRota',visita?: RotaVisita): void {

    this.acaoPendente = acao;
    this.visitaSelecionada = visita ?? null;
    this.mostrarConfirmacao = true;
  }

  cancelarConfirmacao(): void {

    this.mostrarConfirmacao = false;
    this.acaoPendente = null;
    this.visitaSelecionada = null;
  }

  confirmarAcao(): void {
    this.mostrarConfirmacao = false;

    if (this.acaoPendente === 'visitado' && this.visitaSelecionada) {
      this.registrarVisitado(
        this.visitaSelecionada
      );
    }

    if (this.acaoPendente === 'ausente' && this.visitaSelecionada) {
      this.registrarAusente(
        this.visitaSelecionada
      );
    }

    if (this.acaoPendente === 'gerarRota') {
      this.gerarNovaRota();
    }

    this.acaoPendente = null;
    this.visitaSelecionada = null;
  }

  carregarRota(): void {

    this.carregando = true;
    this.erro = '';

    this.dataService.buscarRota().subscribe({

      next: (rota) => {

        this.rota = rota;
        this.paginaAtual = 1;
        this.carregando = false;

        this.cdr.markForCheck();
      },

      error: (erro) => {

        console.error('Erro ao buscar rota:', erro);

        this.erro = 'Não foi possível carregar a rota.';
        this.carregando = false;

        this.cdr.markForCheck();
      }

    });
  }

  proximaPagina(): void {
    if (this.paginaAtual < this.totalPaginas) {
      this.paginaAtual++;
    }
  }

  paginaAnterior(): void {
    if (this.paginaAtual > 1) {
      this.paginaAtual--;
    }
  }

  gerarNovaRota(): void {

    if (this.carregando) {
      return;
    }

    this.carregando = true;
    this.erro = '';

    this.dataService.gerarRota().subscribe({

      next: (rota) => {
        this.rota = rota;
        this.paginaAtual = 1;
        this.carregando = false;

        this.cdr.markForCheck();
      },

      error: (erro) => {

        console.error('Erro ao gerar rota:', erro);

        this.erro = 'Não foi possível gerar a rota.';
        this.carregando = false;

        this.cdr.markForCheck();
      }

    });
  }

  registrarVisitado(visita: RotaVisita): void {

  if (this.visitaEmProcessamentoId !== null) {
    return;
  }

  this.visitaEmProcessamentoId = visita.visitaId;

  this.dataService
    .registrarSucesso(visita.visitaId)
    .subscribe({

      next: () => {

        this.removerVisitaDaTela(visita.visitaId);

        this.visitaEmProcessamentoId = null;

        this.cdr.markForCheck();
      },

      error: (erro) => {

        console.error(
          'Erro ao registrar visita:',
          erro
        );

        this.erro =
          'Não foi possível registrar a visita.';

        this.visitaEmProcessamentoId = null;

        this.cdr.markForCheck();
      }

    });
}

  registrarAusente(visita: RotaVisita): void {

  if (this.visitaEmProcessamentoId !== null) {
    return;
  }

  this.visitaEmProcessamentoId = visita.visitaId;

  this.dataService
    .registrarFalta(visita.visitaId)
    .subscribe({

      next: () => {

        this.removerVisitaDaTela(visita.visitaId);

        this.visitaEmProcessamentoId = null;

        this.cdr.markForCheck();
      },

      error: (erro) => {

        console.error(
          'Erro ao registrar ausência:',
          erro
        );

        this.erro =
          'Não foi possível registrar a ausência.';

        this.visitaEmProcessamentoId = null;

        this.cdr.markForCheck();
      }

    });
}

  private removerVisitaDaTela(visitaId: string): void {
    this.rota = this.rota.filter(
      visita => visita.visitaId !== visitaId
    );

    if (
      this.paginaAtual > this.totalPaginas &&
      this.paginaAtual > 1
    ) {
      this.paginaAtual--;
    }

    this.cdr.markForCheck();
  }

  abrirGoogleMaps(visita: RotaVisita): void {
    const url =
      `https://www.google.com/maps/dir/?api=1` +
      `&destination=${visita.latitude},${visita.longitude}` +
      `&travelmode=driving` +
      `&dir_action=navigate`;

    window.open(
      url,
      '_blank'
    );
  }

  abrirWaze(visita: RotaVisita): void {
    const url =
      `https://waze.com/ul?ll=${visita.latitude},${visita.longitude}` +
      `&navigate=yes`;

    window.open(
      url,
      '_blank'
    );
  }

}