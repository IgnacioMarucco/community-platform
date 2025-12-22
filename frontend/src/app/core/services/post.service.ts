import { inject, Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Page } from '../models/pagination.model';
import { Post, PostCreate, PostSummary, PostUpdate } from '../models/post.model';

@Injectable({ providedIn: 'root' })
export class PostService {
  private api = inject(ApiService);

  getFeed(page = 0, size = 20) {
    return this.api.get<Page<PostSummary>>('/v1/posts/feed', { page, size });
  }

  getGlobal(page = 0, size = 20) {
    return this.api.get<Page<PostSummary>>('/v1/posts', { page, size });
  }

  getTrending(page = 0, size = 20) {
    return this.api.get<Page<PostSummary>>('/v1/posts/trending', { page, size });
  }

  search(query: string, page = 0, size = 20) {
    return this.api.get<Page<PostSummary>>('/v1/posts/search', { q: query, page, size });
  }

  getById(postId: number) {
    return this.api.get<Post>(`/v1/posts/${postId}`);
  }

  getByUser(userId: number, page = 0, size = 20) {
    return this.api.get<Page<PostSummary>>(`/v1/posts/user/${userId}`, { page, size });
  }

  create(payload: PostCreate) {
    return this.api.post<Post>('/v1/posts', payload);
  }

  update(postId: number, payload: PostUpdate) {
    return this.api.put<Post>(`/v1/posts/${postId}`, payload);
  }

  delete(postId: number) {
    return this.api.delete<void>(`/v1/posts/${postId}`);
  }
}
