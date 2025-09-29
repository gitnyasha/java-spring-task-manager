import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, LoginResponse, RegisterRequest } from '../models/auth-requests.model';

const TOKEN_KEY = 'tm_token';
const USER_KEY = 'tm_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private authUrl = `${environment.apiBase}/auth`;
  private userSubject = new BehaviorSubject<LoginResponse | null>(this.loadFromStorage());

  user$ = this.userSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(payload: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.authUrl}/login`, payload)
      .pipe(tap((res) => this.saveToStorage(res)));
  }

  register(payload: RegisterRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.authUrl}/register`, payload)
      .pipe(tap((res) => this.saveToStorage(res)));
  }

  logout() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.userSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) return false;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const exp = payload.exp;
      if (exp && Date.now() >= exp * 1000) {
        this.logout();
        return false;
      }
    } catch {
      return false;
    }
    return true;
  }

  private saveToStorage(res: LoginResponse) {
    localStorage.setItem(TOKEN_KEY, res.token);
    localStorage.setItem(USER_KEY, JSON.stringify(res.user));
    this.userSubject.next(res);
  }

  private loadFromStorage(): LoginResponse | null {
    const token = localStorage.getItem(TOKEN_KEY);
    const user = localStorage.getItem(USER_KEY);
    return token && user ? { token, user: JSON.parse(user) } : null;
  }
}
