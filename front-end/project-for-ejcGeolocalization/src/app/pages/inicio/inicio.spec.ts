
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { Inicio } from './inicio';

describe('Inicio', () => {

  let component: Inicio;
  let fixture: ComponentFixture<Inicio>;

  beforeEach(async () => {

    await TestBed.configureTestingModule({
      imports: [Inicio],
      providers: [
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Inicio);
    component = fixture.componentInstance;

    fixture.detectChanges();

    await fixture.whenStable();

  });

  // 1. CRIAÇÃO DO COMPONENTE

  it('deve criar o componente', () => {

    expect(component).toBeTruthy();

  });

  // 2. TÍTULO DA PÁGINA

  it('deve exibir o título da aplicação', () => {

    const elemento = fixture.nativeElement as HTMLElement;

    const titulo = elemento.querySelector('h1');

    expect(titulo?.textContent?.trim()).toBe(
      'GeoField Tracker'
    );

  });

  // 3. LINK PARA REGISTRAR LOCALIZAÇÃO

  it('deve possuir um link para registrar localização', () => {

    const elemento = fixture.nativeElement as HTMLElement;

    const link = elemento.querySelector(
      'a[href="/registrar-localizacao"]'
    );

    expect(link).not.toBeNull();

    expect(link?.textContent).toContain(
      'Registrar localização'
    );

  });

  // 4. LINK PARA ROTA DE VISITAS

  it('deve possuir um link para a rota de visitas', () => {

    const elemento = fixture.nativeElement as HTMLElement;

    const link = elemento.querySelector(
      'a[href="/rota-visitas"]'
    );

    expect(link).not.toBeNull();

    expect(link?.textContent).toContain(
      'Rota de visitas'
    );

  });

});