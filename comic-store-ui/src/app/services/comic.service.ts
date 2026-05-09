import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Comic, ComicRequest } from '../models/comic.model';
import { PageResponse } from '../models/page-response.model';

@Injectable({ providedIn: 'root' })
export class ComicService {
  private http = inject(HttpClient);
  private readonly base = '/api/comics';

  getAll(page = 0, size = 20) {
    const params = new HttpParams().set('page', page).set('size', size).set('sort', 'title');
    return this.http.get<PageResponse<Comic>>(this.base, { params });
  }

  create(req: ComicRequest)              { return this.http.post<Comic>(this.base, req); }
  update(id: number, req: ComicRequest)  { return this.http.put<Comic>(`${this.base}/${id}`, req); }
  delete(id: number)                     { return this.http.delete<void>(`${this.base}/${id}`); }
}
