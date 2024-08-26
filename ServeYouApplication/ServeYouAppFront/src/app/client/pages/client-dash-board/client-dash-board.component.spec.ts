import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientDashBoardComponent } from './client-dash-board.component';

describe('ClientDashBoardComponent', () => {
  let component: ClientDashBoardComponent;
  let fixture: ComponentFixture<ClientDashBoardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ClientDashBoardComponent]
    });
    fixture = TestBed.createComponent(ClientDashBoardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
