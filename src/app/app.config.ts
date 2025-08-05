import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';

import { HeaderComponent } from './components/header/header';
import { FooterComponent } from './components/footer/footer';
import { SidemenuComponent } from './components/sidemenu/sidemenu';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes)
  ],
};

export const appDeclarations = [
  HeaderComponent,
  FooterComponent,
  SidemenuComponent
];
