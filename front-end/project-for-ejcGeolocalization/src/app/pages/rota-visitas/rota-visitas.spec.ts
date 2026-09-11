import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RotaVisitas } from './rota-visitas';

describe('RotaVisitas', () => {
  let component: RotaVisitas;
  let fixture: ComponentFixture<RotaVisitas>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RotaVisitas],
    }).compileComponents();

    fixture = TestBed.createComponent(RotaVisitas);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
