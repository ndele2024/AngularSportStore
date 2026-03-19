import {inject, Injectable} from '@angular/core';
import {RestDataSource} from '../model/rest.datasource';
import {BehaviorSubject, Observable, tap} from 'rxjs';
import {AuthResponse, AuthSession, User} from '../model/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private dataSource = inject(RestDataSource);
  private storageKey = "sport-store-session";
  private sessionSubject = new BehaviorSubject<AuthSession | null>(this.restoreSession());
  constructor() { }

  authenticate(username: string, password: string): Observable<AuthResponse> {
    return this.dataSource.authenticate(username, password)
      .pipe(tap(response => this.handleResponse(response)));
  }

  register(user: User): Observable<AuthResponse> {
    return this.dataSource.register(user)
      .pipe(tap(response => this.handleResponse(response)));
  }

  updateProfile(user: Partial<User>): Observable<User> {
    const currentUserId = this.currentUser?.id;
    if (!currentUserId) {
      throw new Error("No authenticated user");
    }

    return this.dataSource.updateUserProfile(currentUserId, user)
      .pipe(tap(updatedUser => this.patchCurrentUser(updatedUser)));
  }

  get authenticated(): boolean {
    return this.sessionSubject.value != null;
  }

  get isAdmin(): boolean {
    return this.currentUser?.role === "admin";
  }

  get currentUser(): User | undefined {
    return this.sessionSubject.value?.user;
  }

  get changes(): Observable<AuthSession | null> {
    return this.sessionSubject.asObservable();
  }

  clear() {
    this.dataSource.auth_token = undefined;
    localStorage.removeItem(this.storageKey);
    this.sessionSubject.next(null);
  }

  patchCurrentUser(patch: Partial<User> | User) {
    const currentSession = this.sessionSubject.value;
    if (!currentSession) {
      return;
    }

    const updatedUser = {
      ...currentSession.user,
      ...patch
    };
    const updatedSession = { ...currentSession, user: updatedUser };
    localStorage.setItem(this.storageKey, JSON.stringify(updatedSession));
    this.sessionSubject.next(updatedSession);
  }

  private handleResponse(response: AuthResponse) {
    if (response.success && response.token && response.user) {
      this.dataSource.auth_token = response.token;
      const session = {
        token: response.token,
        user: response.user
      };
      localStorage.setItem(this.storageKey, JSON.stringify(session));
      this.sessionSubject.next(session);
      return;
    }

    this.dataSource.auth_token = undefined;
  }

  private restoreSession(): AuthSession | null {
    const rawSession = localStorage.getItem(this.storageKey);
    if (!rawSession) {
      return null;
    }

    const session = JSON.parse(rawSession) as AuthSession;
    this.dataSource.auth_token = session.token;
    return session;
  }

}
