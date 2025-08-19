import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BooksManagement } from './books-management';

describe('BooksManagement', () => {
  let component: BooksManagement;
  let fixture: ComponentFixture<BooksManagement>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BooksManagement]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BooksManagement);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
