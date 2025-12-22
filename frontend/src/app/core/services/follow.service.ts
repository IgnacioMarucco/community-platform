import { inject, Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { FollowCount, FollowResponse } from '../models/follow.model';
import { UserSummary } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class FollowService {
  private api = inject(ApiService);

  followUser(userId: number) {
    return this.api.post<void>(`/v1/users/${userId}/follow`);
  }

  unfollowUser(userId: number) {
    return this.api.delete<void>(`/v1/users/${userId}/follow`);
  }

  isFollowing(userId: number) {
    return this.api.get<FollowResponse>(`/v1/users/${userId}/follow/check`);
  }

  getFollowers(userId: number) {
    return this.api.get<UserSummary[]>(`/v1/users/${userId}/followers`);
  }

  getFollowing(userId: number) {
    return this.api.get<UserSummary[]>(`/v1/users/${userId}/following`);
  }

  getFollowersCount(userId: number) {
    return this.api.get<FollowCount>(`/v1/users/${userId}/followers/count`);
  }

  getFollowingCount(userId: number) {
    return this.api.get<FollowCount>(`/v1/users/${userId}/following/count`);
  }
}
