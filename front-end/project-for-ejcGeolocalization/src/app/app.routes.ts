import { Routes } from '@angular/router';

export const routes: Routes = [
    {
    path: '',
    loadComponent: () =>
      import('./pages/inicio/inicio')
        .then(m => m.Inicio)
  },

  {
    path: 'registrar-localizacao',
    loadComponent: () =>
      import('./components/home/home')
        .then(m => m.Home)
  },

  {
    path: 'rota-visitas',
    loadComponent: () =>
      import('./pages/rota-visitas/rota-visitas')
        .then(m => m.RotaVisitas)
  }
];
