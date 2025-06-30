import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PortScanResultsComponent } from './port-scan-results.component';

describe('PortScanResultsComponent', () => {
  let component: PortScanResultsComponent;
  let fixture: ComponentFixture<PortScanResultsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PortScanResultsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PortScanResultsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
