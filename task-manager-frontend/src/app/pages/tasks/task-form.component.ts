import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Task } from '../../core/models/task.model';

@Component({
  selector: 'app-task-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task-form.component.html',
})
export class TaskFormComponent {
  @Input() initial?: Task;
  @Input() mode: 'create' | 'edit' = 'create';

  @Output() submit = new EventEmitter<Task>();
  @Output() cancel = new EventEmitter<void>();

  model: Task = { title: '', description: '', status: 'PENDING' };

  ngOnChanges() {
    if (this.initial) {
      this.model = { ...this.initial };
    } else {
      this.model = { title: '', description: '', status: 'PENDING' };
    }
  }

  onSubmit() {
    if (!this.model.title || this.model.title.trim().length === 0) {
      return;
    }

    this.submit.emit({ ...this.model });
  }

  onCancel() {
    this.cancel.emit();
  }
}
