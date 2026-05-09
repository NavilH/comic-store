import { Component, inject, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatCardModule } from '@angular/material/card';
import { InventoryItem } from '../../models/inventory.model';
import { InventoryService } from '../../services/inventory.service';
import { AuthService } from '../../auth/auth.service';
import { RestockDialogComponent } from './restock-dialog.component';

const LOW_STOCK_THRESHOLD = 5;

@Component({
  selector: 'app-inventory',
  imports: [DatePipe, MatTableModule, MatButtonModule, MatIconModule, MatTooltipModule, MatCardModule],
  templateUrl: './inventory.component.html',
  styleUrl: './inventory.component.scss',
})
export class InventoryComponent implements OnInit {
  private dialog = inject(MatDialog);
  private inventoryService = inject(InventoryService);
  auth = inject(AuthService);

  displayedColumns = ['comic', 'publisher', 'quantity', 'status', 'lastRestocked', 'actions'];
  inventory: InventoryItem[] = [];

  ngOnInit() {
    this.load();
  }

  load() {
    this.inventoryService.getAll().subscribe(data => this.inventory = data);
  }

  isLowStock(item: InventoryItem): boolean {
    return item.quantity < LOW_STOCK_THRESHOLD;
  }

  openRestock(item: InventoryItem) {
    const ref = this.dialog.open(RestockDialogComponent);
    ref.afterClosed().subscribe((qty: number | undefined) => {
      if (qty) this.inventoryService.restock(item.id, qty).subscribe(() => this.load());
    });
  }
}
