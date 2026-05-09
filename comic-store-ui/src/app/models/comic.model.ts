import { Author } from './author.model';
import { Publisher } from './publisher.model';

export interface Comic {
  id: number;
  title: string;
  issueNumber: number;
  publisher: Publisher;
  authors: Author[];
  genre: string;
  price: number;
  description?: string;
}

export interface ComicRequest {
  title: string;
  issueNumber: number;
  publisherId: number;
  authorIds: number[];
  genre: string;
  price: number;
  description?: string;
}
