import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, tap, throwError } from 'rxjs';
import { CustomResponse } from '../interface/custom-response';

@Injectable({ providedIn: 'root' })
export class ServerService {
  private readonly apiUrl = 'any';

  constructor(private http: HttpClient) { }

  // simple procedural request to backend
  getServers(): Observable<CustomResponse> {
    return this.http.get<CustomResponse>('http://localhost:8080/server/list')
  }

  //reactive approach of request to beckend
  servers$ = <Observable<CustomResponse>>
  this.http.get<CustomResponse>(`${this.apiUrl}/server/list`)
  .pipe(
    tap(console.log),
    catchError(this.handleError)
  );

  handleError(handleError: any): Observable<never> {
    return throwError (() => new Error('Method not implemented.'));
  }
}
