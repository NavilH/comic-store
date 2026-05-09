export interface Publisher {
  id: number;
  name: string;
  country: string;
}

export interface PublisherRequest {
  name: string;
  country?: string;
}
