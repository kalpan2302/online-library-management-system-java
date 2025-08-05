import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app';
import { HeaderComponent } from './app/components/header/header';
import { FooterComponent } from './app/components/footer/footer';
import { SidemenuComponent } from './app/components/sidemenu/sidemenu';

bootstrapApplication(AppComponent, {
  providers: [],
}).catch(err => console.error(err));
