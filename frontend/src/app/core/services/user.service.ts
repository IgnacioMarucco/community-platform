import { inject, Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { ChangePasswordRequest, User, UserUpdate } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private api = inject(ApiService);

  getCurrentUser() {
    return this.api.get<User>('/v1/users/me');
  }

  updateCurrentUser(update: UserUpdate) {
    return this.api.put<User>('/v1/users/me', update);
  }

  getUserById(userId: number) {
    return this.api.get<User>(`/v1/users/${userId}`);
  }

  getUserByUsername(username: string) {
    return this.api.get<User>(`/v1/users/username/${username}`);
  }

  getAllUsers() {
    return this.api.get<User[]>('/v1/users');
  }

  changePassword(userId: number, payload: ChangePasswordRequest) {
    return this.api.post<void>(`/v1/users/${userId}/change-password`, payload);
  }
}
