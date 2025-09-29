import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Task } from '../../core/models/task.model';
import { TaskService } from '../../core/services/task.service';
import { AuthService } from '../../core/services/auth.service';
import { TaskFormComponent } from './task-form.component';

@Component({
  selector: 'app-tasks',
  standalone: true,
  imports: [CommonModule, FormsModule, TaskFormComponent],
  templateUrl: './tasks.component.html',
})
export class TasksComponent implements OnInit {
  tasks: Task[] = [];
  loading = false;
  error: string | null = null;
  editing: Task | undefined;
  creating = false;

  constructor(private taskService: TaskService, public auth: AuthService) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.loading = true;
    this.taskService.list().subscribe({
      next: (data) => {
        console.log('Tasks data:', data);
        this.tasks = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message || 'Failed to load tasks';
        this.loading = false;
      },
    });
  }

  startCreate() {
    this.creating = true;
  }

  create(task: Task) {
    this.taskService.create(task).subscribe({
      next: (created) => {
        this.tasks.unshift(created);
        this.creating = false;
      },
      error: (err) => (this.error = err?.error?.message || 'Create failed'),
    });
  }

  startEdit(task: Task) {
    this.editing = { ...task };
  }

  saveEdit(updated: Task) {
    if (!this.editing?.id) return;
    const payload = { ...updated, id: this.editing.id };
    this.taskService.update(this.editing.id, payload).subscribe({
      next: (res) => {
        const idx = this.tasks.findIndex((t) => t.id === res.id);
        if (idx >= 0) this.tasks[idx] = res;
        this.editing = undefined;
      },
      error: (err) => (this.error = err?.error?.message || 'Update failed'),
    });
  }

  cancelEdit() {
    this.editing = undefined;
  }

  deleteTask(id?: number) {
    if (!id) return;
    this.taskService.delete(id).subscribe({
      next: () => {
        this.tasks = this.tasks.filter((t) => t.id !== id);
      },
      error: (err) => (this.error = err?.error?.message || 'Delete failed'),
    });
  }

  toggleStatus(task: Task) {
    const updated: Task = { ...task, status: task.status === 'PENDING' ? 'COMPLETED' : 'PENDING' };
    if (!updated.id) return;
    this.taskService.update(updated.id, updated).subscribe({
      next: (up) => {
        const idx = this.tasks.findIndex((t) => t.id === up.id);
        if (idx >= 0) this.tasks[idx] = up;
      },
      error: (err) => (this.error = err?.error?.message || 'Status change failed'),
    });
  }

  logout() {
    this.auth.logout();
  }
}
