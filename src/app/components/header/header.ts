import { Component, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-header',
  standalone: true,
  templateUrl: './header.html',
  styleUrls: ['./header.css']
})
export class HeaderComponent {
  @Output() menuToggle = new EventEmitter<void>();
  toggleMenu() {
    this.menuToggle.emit();
  }
}
