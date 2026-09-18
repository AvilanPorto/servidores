import { Routes } from '@angular/router';

import { ServidoresComponent } from './components/servidores/servidores.component';
import { SecretariasComponent } from './components/secretarias/secretarias.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'servidores',
    pathMatch: 'full'
  },
  {
    path: 'servidores',
    component: ServidoresComponent
  },
  {
    path: 'secretarias',
    component: SecretariasComponent
  }
];
