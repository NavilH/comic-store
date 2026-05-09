import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { InventoryItem } from '../models/inventory.model';

@Injectable({ providedIn: 'root' })
export class InventoryService {
  private http = inject(HttpClient);
  private readonly base = '/api/inventory';

  getAll()                          { return this.http.get<InventoryItem[]>(this.base); }
  restock(id: number, quantity: number) {
    return this.http.patch<InventoryItem>(`${this.base}/${id}/restock`, { quantity });
  }
}
