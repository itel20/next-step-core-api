import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'eleve',
    data: { pageTitle: 'userManagementServiceApp.userManagementServiceEleve.home.title' },
    loadChildren: () => import('./userManagementService/eleve/eleve.routes'),
  },
  {
    path: 'etudiant',
    data: { pageTitle: 'userManagementServiceApp.userManagementServiceEtudiant.home.title' },
    loadChildren: () => import('./userManagementService/etudiant/etudiant.routes'),
  },
  {
    path: 'conseiller',
    data: { pageTitle: 'userManagementServiceApp.userManagementServiceConseiller.home.title' },
    loadChildren: () => import('./userManagementService/conseiller/conseiller.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
