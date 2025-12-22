import { inject, Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { LikeResponse } from '../models/like.model';

@Injectable({ providedIn: 'root' })
export class LikeService {
  private api = inject(ApiService);

  likePost(postId: number) {
    return this.api.post<void>(`/v1/posts/${postId}/like`);
  }

  unlikePost(postId: number) {
    return this.api.delete<void>(`/v1/posts/${postId}/like`);
  }

  hasLikedPost(postId: number) {
    return this.api.get<boolean>(`/v1/posts/${postId}/like/check`);
  }

  getPostLikes(postId: number) {
    return this.api.get<LikeResponse[]>(`/v1/posts/${postId}/likes`);
  }

  likeComment(commentId: number) {
    return this.api.post<void>(`/v1/comments/${commentId}/like`);
  }

  unlikeComment(commentId: number) {
    return this.api.delete<void>(`/v1/comments/${commentId}/like`);
  }

  hasLikedComment(commentId: number) {
    return this.api.get<boolean>(`/v1/comments/${commentId}/like/check`);
  }

  getCommentLikes(commentId: number) {
    return this.api.get<LikeResponse[]>(`/v1/comments/${commentId}/likes`);
  }
}
