import {inject, Injectable} from '@angular/core';
import {RestDataSource} from '../model/rest.datasource';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private dataSource = inject(RestDataSource);
  constructor() { }

  authenticate(username: string, password: string): Observable<boolean> {
    return this.dataSource.authenticate(username, password);
  }
  get authenticated(): boolean {
    return this.dataSource.auth_token != null;
  }

  clear() {
    this.dataSource.auth_token = undefined;
  }

}
