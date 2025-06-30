import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-port-scan-results',
  template: `
    <h2 mat-dialog-title>Open Ports for {{ data.ipAddress }}</h2>
    <mat-dialog-content>
      <mat-list>
        <mat-list-item *ngFor="let port of data.ports">
          Port {{ port }} is open
        </mat-list-item>
        <mat-list-item *ngIf="data.ports.length === 0">
          No open ports found.
        </mat-list-item>
      </mat-list>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Close</button>
    </mat-dialog-actions>
  `,
  styles: [`
    mat-list-item {
      height: 32px !important; /* Compact list */
    }
  `]
})
export class PortScanResultsComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: {
    ipAddress: string,
    ports: number[]
  }) {}
}
