import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Sale, SaleRequest } from '../models/sale.model';
import { PageResponse } from '../models/page-response.model';

@Injectable({ providedIn: 'root' })
export class SaleService {
  private http = inject(HttpClient);
  private readonly base = '/api/sales';

  getAll(page = 0, size = 20) {
    const params = new HttpParams().set('page', page).set('size', size).set('sort', 'saleDate,desc');
    return this.http.get<PageResponse<Sale>>(this.base, { params });
  }

  create(req: SaleRequest) { return this.http.post<Sale>(this.base, req); }
}
