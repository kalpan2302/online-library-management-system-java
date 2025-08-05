import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-sidemenu',
  templateUrl: './sidemenu.html',
  styleUrls: ['./sidemenu.css']
})
export class SidemenuComponent {
  @Input() isOpen: boolean = false;
}
