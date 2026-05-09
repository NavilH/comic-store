import { Comic } from './comic.model';

export interface SaleItem {
  id: number;
  comic: Comic;
  quantity: number;
  unitPrice: number;
}

export interface Sale {
  id: number;
  saleDate: string;
  totalAmount: number;
  items: SaleItem[];
}

export interface SaleRequest {
  items: { comicId: number; quantity: number }[];
}
