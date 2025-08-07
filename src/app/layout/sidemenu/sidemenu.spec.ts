import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CommonModule } from '@angular/common'; // ✅ Needed for ngClass
import { Sidemenu } from './sidemenu';

describe('Sidemenu', () => {
  let component: Sidemenu;
  let fixture: ComponentFixture<Sidemenu>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommonModule], // ✅ Correct: import CommonModule
      declarations: [Sidemenu], // ✅ Declare your component here
    }).compileComponents();

    fixture = TestBed.createComponent(Sidemenu);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
