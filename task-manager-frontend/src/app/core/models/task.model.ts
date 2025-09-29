export type TaskStatus = 'PENDING' | 'COMPLETED';

export interface Task {
  id?: number;
  userId?: number;
  title: string;
  description?: string;
  status: TaskStatus;
  createdAt?: string;
  updatedAt?: string;
}
