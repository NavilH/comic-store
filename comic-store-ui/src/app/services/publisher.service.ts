import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Publisher, PublisherRequest } from '../models/publisher.model';

@Injectable({ providedIn: 'root' })
export class PublisherService {
  private http = inject(HttpClient);
  private readonly base = '/api/publishers';

  getAll()                                  { return this.http.get<Publisher[]>(this.base); }
  create(req: PublisherRequest)             { return this.http.post<Publisher>(this.base, req); }
  update(id: number, req: PublisherRequest) { return this.http.put<Publisher>(`${this.base}/${id}`, req); }
  delete(id: number)                        { return this.http.delete<void>(`${this.base}/${id}`); }
}
