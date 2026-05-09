import { Component, inject, OnInit } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatCardModule } from '@angular/material/card';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { Sale, SaleRequest } from '../../models/sale.model';
import { SaleService } from '../../services/sale.service';
import { AuthService } from '../../auth/auth.service';
import { SaleFormDialogComponent } from './sale-form-dialog.component';
import { SaleDetailDialogComponent } from './sale-detail-dialog.component';

@Component({
  selector: 'app-sales',
  imports: [CurrencyPipe, DatePipe, MatTableModule, MatButtonModule, MatIconModule, MatTooltipModule, MatCardModule, MatPaginatorModule],
  templateUrl: './sales.component.html',
  styleUrl: './sales.component.scss',
})
export class SalesComponent implements OnInit {
  private dialog = inject(MatDialog);
  private saleService = inject(SaleService);
  auth = inject(AuthService);

  displayedColumns = ['date', 'items', 'total', 'actions'];
  sales: Sale[] = [];
  page = 0;
  pageSize = 20;
  totalElements = 0;

  ngOnInit() {
    this.load();
  }

  load() {
    this.saleService.getAll(this.page, this.pageSize).subscribe(res => {
      this.sales = res.content;
      this.totalElements = res.totalElements;
    });
  }

  onPageChange(event: PageEvent) {
    this.page = event.pageIndex;
    this.pageSize = event.pageSize;
    this.load();
  }

  openNewSale() {
    const ref = this.dialog.open(SaleFormDialogComponent);
    ref.afterClosed().subscribe((request: SaleRequest | undefined) => {
      if (request) this.saleService.create(request).subscribe(() => this.load());
    });
  }

  viewDetails(sale: Sale) {
    this.dialog.open(SaleDetailDialogComponent, { data: sale });
  }
}
