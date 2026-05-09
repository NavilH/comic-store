import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-restock-dialog',
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>Restock Item</h2>
    <mat-dialog-content>
      <form [formGroup]="form" class="form">
        <mat-form-field appearance="outline">
          <mat-label>Quantity to Add</mat-label>
          <input matInput type="number" formControlName="quantity" />
          <mat-hint>Enter the number of units to add to stock</mat-hint>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancel</button>
      <button mat-flat-button color="primary" [disabled]="form.invalid" (click)="save()">Restock</button>
    </mat-dialog-actions>
  `,
  styles: [`
    .form {
      padding-top: 8px;
      min-width: 320px;
    }
    mat-form-field {
      width: 100%;
    }
  `],
})
export class RestockDialogComponent {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<RestockDialogComponent>);

  form = this.fb.group({
    quantity: [null as number | null, [Validators.required, Validators.min(1)]],
  });

  save() {
    if (this.form.valid) {
      this.dialogRef.close(this.form.value.quantity);
    }
  }
}
