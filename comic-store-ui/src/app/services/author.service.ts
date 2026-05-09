import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Author, AuthorRequest } from '../models/author.model';

@Injectable({ providedIn: 'root' })
export class AuthorService {
  private http = inject(HttpClient);
  private readonly base = '/api/authors';

  getAll()                              { return this.http.get<Author[]>(this.base); }
  create(req: AuthorRequest)            { return this.http.post<Author>(this.base, req); }
  update(id: number, req: AuthorRequest){ return this.http.put<Author>(`${this.base}/${id}`, req); }
  delete(id: number)                    { return this.http.delete<void>(`${this.base}/${id}`); }
}
