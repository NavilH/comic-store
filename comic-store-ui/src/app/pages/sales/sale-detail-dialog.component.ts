import { Component, inject } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { Sale } from '../../models/sale.model';

@Component({
  selector: 'app-sale-detail-dialog',
  imports: [CurrencyPipe, DatePipe, MatDialogModule, MatTableModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>Sale Details — {{ sale.saleDate | date:'mediumDate' }}</h2>
    <mat-dialog-content>
      <table mat-table [dataSource]="sale.items" class="detail-table">

        <ng-container matColumnDef="comic">
          <th mat-header-cell *matHeaderCellDef>Comic</th>
          <td mat-cell *matCellDef="let item" class="comic-cell">{{ item.comic.title }}</td>
        </ng-container>

        <ng-container matColumnDef="quantity">
          <th mat-header-cell *matHeaderCellDef>Qty</th>
          <td mat-cell *matCellDef="let item">{{ item.quantity }}</td>
        </ng-container>

        <ng-container matColumnDef="unitPrice">
          <th mat-header-cell *matHeaderCellDef>Unit Price</th>
          <td mat-cell *matCellDef="let item">{{ item.unitPrice | currency }}</td>
        </ng-container>

        <ng-container matColumnDef="subtotal">
          <th mat-header-cell *matHeaderCellDef>Subtotal</th>
          <td mat-cell *matCellDef="let item">{{ item.unitPrice * item.quantity | currency }}</td>
        </ng-container>

        <tr mat-header-row *matHeaderRowDef="columns"></tr>
        <tr mat-row *matRowDef="let row; columns: columns;"></tr>

      </table>

      <div class="total-row">
        <span>Total</span>
        <strong>{{ sale.totalAmount | currency }}</strong>
      </div>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Close</button>
    </mat-dialog-actions>
  `,
  styles: [`
    .detail-table { width: 480px; }
    .comic-cell   { font-weight: 500; }
    .total-row {
      display: flex; justify-content: space-between; padding: 12px 0 0;
      margin-top: 8px; border-top: 1px solid #e0e0e0; font-size: 16px;
      strong { font-size: 20px; color: #3f51b5; }
    }
  `],
})
export class SaleDetailDialogComponent {
  sale: Sale = inject(MAT_DIALOG_DATA);
  columns = ['comic', 'quantity', 'unitPrice', 'subtotal'];
}
