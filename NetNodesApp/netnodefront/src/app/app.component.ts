import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map, startWith } from 'rxjs/operators';
import { DataState } from './enum/data-state.enum';
import { Status } from './enum/status.enum';
import { AppState } from './interface/app-state';
import { CustomResponse } from './interface/custom-response';
import { Server } from './interface/server';
import { NotificationService } from './service/notification.service';
import { ServerService } from './service/server.service';
import { MatDialog } from '@angular/material/dialog';
import { PortScanResultsComponent} from './components/port-scan-results/port-scan-results.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppComponent implements OnInit {
  appState$: Observable<AppState<CustomResponse>>;
  readonly DataState = DataState;
  readonly Status = Status;
  private filterSubject = new BehaviorSubject<string>('');
  private dataSubject = new BehaviorSubject<CustomResponse>(null);
  filterStatus$ = this.filterSubject.asObservable();
  private isLoading = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoading.asObservable();
  scanResults: number[] = [];
  scanIpAddress = '';
  // Track scanning state per server IP
  private scanningServers = new Map<string, boolean>();
  private scanningSubject = new BehaviorSubject<Map<string, boolean>>(new Map());
  scanningState$ = this.scanningSubject.asObservable();

  constructor(private serverService: ServerService, private notifier: NotificationService, public dialog: MatDialog) { }

  ngOnInit(): void {
    this.appState$ = this.serverService.servers$
      .pipe(
        map(response => {
          this.notifier.onDefault(response.message);
          this.dataSubject.next(response);
          return { dataState: DataState.LOADED_STATE, appData: { ...response, data: { servers: response.data.servers.reverse() } } };
        }),
        startWith({ dataState: DataState.LOADING_STATE }),
        catchError((error: string) => {
          this.notifier.onError(error);
          return of({ dataState: DataState.ERROR_STATE, error });
        })
      );
  }

  // Helper method to check if a specific server is being scanned
  isServerScanning(ipAddress: string): boolean {
    return this.scanningServers.get(ipAddress) || false;
  }

  pingServer(ipAddress: string): void {
    this.filterSubject.next(ipAddress);
    this.appState$ = this.serverService.ping$(ipAddress)
      .pipe(
        map(response => {
          const index = this.dataSubject.value.data.servers.findIndex(server =>  server.id === response.data.server.id);
          this.dataSubject.value.data.servers[index] = response.data.server;
          this.notifier.onDefault(response.message);
          this.filterSubject.next('');
          return { dataState: DataState.LOADED_STATE, appData: this.dataSubject.value };
        }),
        startWith({ dataState: DataState.LOADED_STATE, appData: this.dataSubject.value }),
        catchError((error: string) => {
          this.filterSubject.next('');
          this.notifier.onError(error);
          return of({ dataState: DataState.ERROR_STATE, error });
        })
      );
  }

  scanServer(ipAddress: string): void {
    // Set scanning state for this specific server
    this.scanningServers.set(ipAddress, true);
    this.scanningSubject.next(new Map(this.scanningServers));
    
    this.serverService.scan$(ipAddress).subscribe({
      next: (response) => {
        this.dialog.open(PortScanResultsComponent, {
          data: {
            ipAddress,
            ports: response.data['Open ports'] || []
          }
        });
        // Clear scanning state for this specific server
        this.scanningServers.set(ipAddress, false);
        this.scanningSubject.next(new Map(this.scanningServers));
      },
      error: (error) => {
        // Clear scanning state for this specific server
        this.scanningServers.set(ipAddress, false);
        this.scanningSubject.next(new Map(this.scanningServers));
        this.notifier.onError(error);
      }
    });
  }

  saveServer(serverForm: NgForm): void {
    this.isLoading.next(true);
    this.appState$ = this.serverService.save$(serverForm.value as Server)
      .pipe(
        map(response => {
          this.dataSubject.next(
            {...response, data: { servers: [response.data.server, ...this.dataSubject.value.data.servers] } }
          );
          this.notifier.onDefault(response.message);
          document.getElementById('closeModal').click();
          this.isLoading.next(false);
          serverForm.resetForm({ status: this.Status.SERVER_DOWN });
          return { dataState: DataState.LOADED_STATE, appData: this.dataSubject.value };
        }),
        startWith({ dataState: DataState.LOADED_STATE, appData: this.dataSubject.value }),
        catchError((error: string) => {
          this.isLoading.next(false);
          this.notifier.onError(error);
          return of({ dataState: DataState.ERROR_STATE, error });
        })
      );
}

  filterServers(status: Status): void {
    this.appState$ = this.serverService.filter$(status, this.dataSubject.value)
      .pipe(
        map(response => {
          this.notifier.onDefault(response.message);
          return { dataState: DataState.LOADED_STATE, appData: response };
        }),
        startWith({ dataState: DataState.LOADED_STATE, appData: this.dataSubject.value }),
        catchError((error: string) => {
          this.notifier.onError(error);
          return of({ dataState: DataState.ERROR_STATE, error });
        })
      );
  }

  deleteServer(server: Server): void {
    this.appState$ = this.serverService.delete$(server.id)
      .pipe(
        map(response => {
          this.dataSubject.next(
            { ...response, data:
              { servers: this.dataSubject.value.data.servers.filter(s => s.id !== server.id)} }
          );
          this.notifier.onDefault(response.message);
          return { dataState: DataState.LOADED_STATE, appData: this.dataSubject.value };
        }),
        startWith({ dataState: DataState.LOADED_STATE, appData: this.dataSubject.value }),
        catchError((error: string) => {
          this.notifier.onError(error);
          return of({ dataState: DataState.ERROR_STATE, error });
        })
      );
  }

  printReport(): void {
    this.notifier.onDefault('Report downloaded');
    // window.print();
    const dataType = 'application/vnd.ms-excel.sheet.macroEnabled.12';
    const tableSelect = document.getElementById('servers');
    const tableHtml = tableSelect.outerHTML.replace(/ /g, '%20');
    const downloadLink = document.createElement('a');
    document.body.appendChild(downloadLink);
    downloadLink.href = 'data:' + dataType + ', ' + tableHtml;
    downloadLink.download = 'server-report.xls';
    downloadLink.click();
    document.body.removeChild(downloadLink);
  }
}
