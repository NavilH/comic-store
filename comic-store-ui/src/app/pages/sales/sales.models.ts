export interface SaleItem {
  comicId: number;
  comic: string;
  quantity: number;
  unitPrice: number;
}

export interface Sale {
  id: number;
  date: Date;
  items: SaleItem[];
  total: number;
}

export const AVAILABLE_COMICS = [
  { id: 1, title: 'The Amazing Spider-Man', price: 4.99 },
  { id: 2, title: 'Batman',                 price: 3.99 },
  { id: 3, title: 'X-Men',                  price: 4.99 },
  { id: 4, title: 'Sandman',                price: 5.99 },
  { id: 5, title: 'Saga',                   price: 3.99 },
  { id: 6, title: 'Watchmen',               price: 5.99 },
];
