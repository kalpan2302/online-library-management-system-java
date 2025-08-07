import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common'; // ✅ Import CommonModule

@Component({
  selector: 'app-sidemenu',
  standalone: true,
  imports: [CommonModule], // ✅ Add CommonModule here
  templateUrl: './sidemenu.html',
})
export class Sidemenu {
  @Input() isOpen: boolean = false;
  @Output() isOpenChange = new EventEmitter<boolean>();

  close() {
    this.isOpenChange.emit(false);
  }
}
