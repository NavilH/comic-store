import { Component, inject, OnInit } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Comic } from '../../models/comic.model';
import { SaleRequest } from '../../models/sale.model';
import { ComicService } from '../../services/comic.service';

@Component({
  selector: 'app-sale-form-dialog',
  imports: [CurrencyPipe, ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, MatIconModule],
  template: `
    <h2 mat-dialog-title>New Sale</h2>
    <mat-dialog-content>
      <form [formGroup]="form">
        <div formArrayName="items">
          @for (item of items.controls; track $index; let i = $index) {
            <div [formGroupName]="i" class="item-row">
              <mat-form-field appearance="outline" class="comic-field">
                <mat-label>Comic</mat-label>
                <mat-select formControlName="comicId">
                  @for (comic of comics; track comic.id) {
                    <mat-option [value]="comic.id">{{ comic.title }}</mat-option>
                  }
                </mat-select>
              </mat-form-field>

              <mat-form-field appearance="outline" class="qty-field">
                <mat-label>Qty</mat-label>
                <input matInput type="number" formControlName="quantity" min="1" />
              </mat-form-field>

              <span class="item-subtotal">{{ getItemTotal(i) | currency }}</span>

              <button mat-icon-button color="warn" type="button"
                      [disabled]="items.length === 1" (click)="removeItem(i)">
                <mat-icon>remove_circle_outline</mat-icon>
              </button>
            </div>
          }
        </div>

        <button mat-button type="button" (click)="addItem()">
          <mat-icon>add</mat-icon> Add Item
        </button>

        <div class="total-row">
          <span>Total</span>
          <strong>{{ getTotal() | currency }}</strong>
        </div>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancel</button>
      <button mat-flat-button color="primary" [disabled]="form.invalid" (click)="save()">
        Complete Sale
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    form { padding-top: 8px; min-width: 520px; }
    .item-row { display: flex; align-items: center; gap: 12px; margin-bottom: 4px; }
    .comic-field { flex: 1; }
    .qty-field   { width: 80px; }
    .item-subtotal { width: 80px; text-align: right; font-weight: 500; white-space: nowrap; }
    .total-row {
      display: flex; justify-content: space-between; align-items: center;
      padding: 12px 0 0; margin-top: 8px; border-top: 1px solid #e0e0e0; font-size: 16px;
      strong { font-size: 20px; color: #3f51b5; }
    }
  `],
})
export class SaleFormDialogComponent implements OnInit {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<SaleFormDialogComponent>);
  private comicService = inject(ComicService);

  comics: Comic[] = [];

  form = this.fb.group({
    items: this.fb.array([this.createItem()]),
  });

  get items(): FormArray {
    return this.form.get('items') as FormArray;
  }

  ngOnInit() {
    this.comicService.getAll(0, 1000).subscribe(res => this.comics = res.content);
  }

  createItem() {
    return this.fb.group({
      comicId:  [null as number | null, Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]],
    });
  }

  addItem()              { this.items.push(this.createItem()); }
  removeItem(i: number)  { this.items.removeAt(i); }

  getComicPrice(comicId: number | null): number {
    return this.comics.find(c => c.id === comicId)?.price ?? 0;
  }

  getItemTotal(i: number): number {
    const item = this.items.at(i).value;
    return this.getComicPrice(item.comicId) * (item.quantity || 0);
  }

  getTotal(): number {
    return this.items.controls.reduce((sum, _, i) => sum + this.getItemTotal(i), 0);
  }

  save() {
    if (this.form.invalid) return;
    const request: SaleRequest = {
      items: this.items.value.map((item: { comicId: number; quantity: number }) => ({
        comicId: item.comicId,
        quantity: item.quantity,
      })),
    };
    this.dialogRef.close(request);
  }
}
