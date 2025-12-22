import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ApiService } from './api.service';
import {
  MediaUploadResponse,
  PresignedConfirmRequest,
  PresignedDownloadResponse,
  PresignedUploadRequest,
  PresignedUploadResponse
} from '../models/media.model';

@Injectable({ providedIn: 'root' })
export class MediaService {
  private api = inject(ApiService);
  private http = inject(HttpClient);

  createPresignedUpload(payload: PresignedUploadRequest) {
    return this.api.post<PresignedUploadResponse>('/v1/media/presigned/upload', payload);
  }

  confirmPresignedUpload(payload: PresignedConfirmRequest) {
    return this.api.post<MediaUploadResponse>('/v1/media/presigned/confirm', payload);
  }

  createPresignedDownload(mediaId: number) {
    return this.api.get<PresignedDownloadResponse>(`/v1/media/${mediaId}/presigned-download`);
  }

  uploadToPresignedUrl(uploadUrl: string, file: File) {
    const headers = new HttpHeaders({
      'Content-Type': file.type || 'application/octet-stream'
    });
    return this.http.put(uploadUrl, file, { headers, responseType: 'text' });
  }
}
