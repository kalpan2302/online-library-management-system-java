import { Component, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-header',
  standalone: true,
  templateUrl: './header.html',
})
export class Header {
  @Output() menuToggle = new EventEmitter<void>();
  toggleMenu() {
    this.menuToggle.emit();
  }
}