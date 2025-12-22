import { inject, Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Comment, CommentCreate, CommentUpdate } from '../models/comment.model';
import { Page } from '../models/pagination.model';

@Injectable({ providedIn: 'root' })
export class CommentService {
  private api = inject(ApiService);

  create(postId: number, payload: CommentCreate) {
    return this.api.post<Comment>(`/v1/posts/${postId}/comments`, payload);
  }

  getById(commentId: number) {
    return this.api.get<Comment>(`/v1/comments/${commentId}`);
  }

  getByPost(postId: number, page = 0, size = 20) {
    return this.api.get<Page<Comment>>(`/v1/posts/${postId}/comments`, { page, size });
  }

  getTopLevel(postId: number, page = 0, size = 20) {
    return this.api.get<Page<Comment>>(`/v1/posts/${postId}/comments/top`, { page, size });
  }

  getReplies(commentId: number) {
    return this.api.get<Comment[]>(`/v1/comments/${commentId}/replies`);
  }

  update(commentId: number, payload: CommentUpdate) {
    return this.api.put<Comment>(`/v1/comments/${commentId}`, payload);
  }

  delete(commentId: number) {
    return this.api.delete<void>(`/v1/comments/${commentId}`);
  }
}
