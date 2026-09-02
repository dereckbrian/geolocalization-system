import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { DataService } from '../../services/data-service';

import { Tio } from '../../models/tio';
import { Jovem } from '../../models/jovem';
import { RegistrarLocalizacaoRequest } from '../../models/register-localizacao-request';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './home.html',
  styleUrl: './home.css',
})

export class Home implements OnInit {

  tios: Tio[] = [];
  jovens: Jovem[] = [];

  tioSelecionado = '';
  jovemSelecionado = '';

  latitude?: number;
  longitude?: number;

  carregandoTios = false;
  carregandoJovens = false;
  capturandoLocalizacao = false;
  enviando = false;

  mensagemErro = '';
  mensagemSucesso = '';

  mostrarConfirmacao = false;

  private readonly cdr = inject(ChangeDetectorRef);

  constructor(
    private dataService: DataService
  ) {}

  ngOnInit(): void {
    this.carregarTios();
  }

  carregarTios(): void {

    this.carregandoTios = true;

    this.dataService.buscarTios()
      .subscribe({

        next: (tios) => {
          this.tios = tios;
          this.carregandoTios = false;

          this.cdr.markForCheck();
        },

        error: (erro) => {
          console.error(erro);

          this.mensagemErro =
            'Não foi possível carregar os tios.';

          this.carregandoTios = false;

          this.cdr.markForCheck();
        }

      });
  }

  carregarJovens(): void {

  this.jovemSelecionado = '';
  this.jovens = [];

  if (!this.tioSelecionado) {
    return;
  }

  this.carregandoJovens = true;

  this.dataService
    .buscarJovensPorTio(this.tioSelecionado)
    .subscribe({

      next: (jovens) => {

        this.jovens = jovens;
        this.carregandoJovens = false;

        this.cdr.markForCheck();
      },

      error: (erro) => {

        console.error(erro);

        this.mensagemErro =
          'Não foi possível carregar os jovens.';

        this.carregandoJovens = false;

        this.cdr.markForCheck();
      }

    });
}

  capturarLocalizacao(): void {

  this.mensagemErro = '';

  if (!navigator.geolocation) {

    this.mensagemErro =
      'Seu dispositivo não oferece suporte à localização.';

    return;
  }

  this.capturandoLocalizacao = true;

  navigator.geolocation.getCurrentPosition(

    (position) => {

      this.latitude =
        position.coords.latitude;

      this.longitude =
        position.coords.longitude;

      this.capturandoLocalizacao = false;

      this.cdr.markForCheck();
    },

    (erro) => {

      console.error(erro);

      this.mensagemErro =
        'Não foi possível obter sua localização. Verifique a permissão do GPS.';

      this.capturandoLocalizacao = false;

      this.cdr.markForCheck();
    },

    {
      enableHighAccuracy: true,
      timeout: 15000,
      maximumAge: 0
    }

  );
}

  abrirConfirmacao(): void {

    if (!this.podeEnviar) {
      return;
    }

    this.mostrarConfirmacao = true;
  }

  cancelarEnvio(): void {
    this.mostrarConfirmacao = false;
  }

  confirmarEnvio(): void {

    if (
      this.latitude === undefined ||
      this.longitude === undefined
    ) {
      return;
    }

    const dados:  RegistrarLocalizacaoRequest= {

      tioID: this.tioSelecionado,

      jovemID: this.jovemSelecionado,

      latitude: this.latitude,

      longitude: this.longitude

    };

    this.enviando = true;
    this.mostrarConfirmacao = false;
    this.mensagemErro = '';
    this.mensagemSucesso = '';

    this.dataService
      .registrarLocalizacao(dados)
      .subscribe({

        next: () => {

          this.mensagemSucesso =
            'Localização registrada com sucesso!';

          this.enviando = false;

          this.limparFormulario();

          this.cdr.markForCheck();
        },

        error: (erro) => {

        console.error('STATUS:', erro.status);
        console.error('RESPOSTA DO BACK:', erro.error);
        console.error('DADOS ENVIADOS:', dados);

        this.mensagemErro =
          'Erro ao registrar localização.';

        this.enviando = false;

        this.cdr.markForCheck();
      }
      });
  }

  limparFormulario(): void {

    this.tioSelecionado = '';
    this.jovemSelecionado = '';

    this.jovens = [];

    this.latitude = undefined;
    this.longitude = undefined;
  }

  get localizacaoCapturada(): boolean {

    return (
      this.latitude !== undefined &&
      this.longitude !== undefined
    );
  }

  get podeEnviar(): boolean {

    return (
      !!this.tioSelecionado &&
      !!this.jovemSelecionado &&
      this.localizacaoCapturada &&
      !this.enviando
    );
  }
}
