import { Comic } from './comic.model';

export interface InventoryItem {
  id: number;
  comic: Comic;
  quantity: number;
  lastRestocked: string;
}
