import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Task } from '../models/task.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class TaskService {
  private base = `${environment.apiBase}/api/tasks`;

  constructor(private http: HttpClient) {}

  list(): Observable<Task[]> {
    return this.http.get<Task[]>(this.base);
  }

  get(id: number): Observable<Task> {
    return this.http.get<Task>(`${this.base}/${id}`);
  }

  create(task: Task): Observable<Task> {
    return this.http.post<Task>(this.base, task);
  }

  update(id: number, task: Task): Observable<Task> {
    return this.http.put<Task>(`${this.base}/${id}`, task);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
