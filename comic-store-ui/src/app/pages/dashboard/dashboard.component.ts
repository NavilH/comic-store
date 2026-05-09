import { Component, inject, OnInit } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { forkJoin } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { Sale } from '../../models/sale.model';
import { ComicService } from '../../services/comic.service';
import { PublisherService } from '../../services/publisher.service';
import { InventoryService } from '../../services/inventory.service';
import { SaleService } from '../../services/sale.service';

const LOW_STOCK_THRESHOLD = 5;

@Component({
  selector: 'app-dashboard',
  imports: [CurrencyPipe, DatePipe, MatCardModule, MatTableModule, MatIconModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit {
  private comicService     = inject(ComicService);
  private publisherService = inject(PublisherService);
  private inventoryService = inject(InventoryService);
  private saleService      = inject(SaleService);

  stats = [
    { label: 'Total Comics',    value: '—', icon: 'menu_book',    color: '#3f51b5' },
    { label: 'Publishers',      value: '—', icon: 'business',     color: '#009688' },
    { label: 'Low Stock Items', value: '—', icon: 'warning',      color: '#f44336' },
    { label: 'Monthly Revenue', value: '—', icon: 'attach_money', color: '#4caf50' },
  ];

  recentSales: Sale[] = [];
  displayedColumns = ['date', 'items', 'total'];

  ngOnInit() {
    forkJoin({
      comics:     this.comicService.getAll(0, 1000),
      publishers: this.publisherService.getAll(),
      inventory:  this.inventoryService.getAll(),
      sales:      this.saleService.getAll(0, 1000),
    }).subscribe(({ comics, publishers, inventory, sales }) => {
      const now = new Date();
      const monthlyRevenue = sales.content
        .filter(s => {
          const d = new Date(s.saleDate);
          return d.getMonth() === now.getMonth() && d.getFullYear() === now.getFullYear();
        })
        .reduce((sum, s) => sum + Number(s.totalAmount), 0);

      this.stats[0].value = comics.totalElements.toString();
      this.stats[1].value = publishers.length.toString();
      this.stats[2].value = inventory.filter(i => i.quantity < LOW_STOCK_THRESHOLD).length.toString();
      this.stats[3].value = new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(monthlyRevenue);

      this.recentSales = [...sales.content]
        .sort((a, b) => new Date(b.saleDate).getTime() - new Date(a.saleDate).getTime())
        .slice(0, 5);
    });
  }
}
