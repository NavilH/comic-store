import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { Comic, ComicRequest } from '../../models/comic.model';
import { Publisher } from '../../models/publisher.model';
import { Author } from '../../models/author.model';
import { PublisherService } from '../../services/publisher.service';
import { AuthorService } from '../../services/author.service';

@Component({
  selector: 'app-comic-form-dialog',
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>{{ data?.id ? 'Edit Comic' : 'Add Comic' }}</h2>
    <mat-dialog-content>
      <form [formGroup]="form" class="form">
        <mat-form-field appearance="outline">
          <mat-label>Title</mat-label>
          <input matInput formControlName="title" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Issue Number</mat-label>
          <input matInput type="number" formControlName="issueNumber" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Publisher</mat-label>
          <mat-select formControlName="publisherId">
            @for (p of publishers; track p.id) {
              <mat-option [value]="p.id">{{ p.name }}</mat-option>
            }
          </mat-select>
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Authors</mat-label>
          <mat-select formControlName="authorIds" multiple>
            @for (a of authors; track a.id) {
              <mat-option [value]="a.id">{{ a.name }}</mat-option>
            }
          </mat-select>
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Genre</mat-label>
          <mat-select formControlName="genre">
            @for (g of genres; track g.value) {
              <mat-option [value]="g.value">{{ g.label }}</mat-option>
            }
          </mat-select>
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Price</mat-label>
          <span matTextPrefix>$&nbsp;</span>
          <input matInput type="number" step="0.01" formControlName="price" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Description</mat-label>
          <textarea matInput formControlName="description" rows="3"></textarea>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancel</button>
      <button mat-flat-button color="primary" [disabled]="form.invalid" (click)="save()">Save</button>
    </mat-dialog-actions>
  `,
  styles: [`
    .form { display: flex; flex-direction: column; gap: 4px; padding-top: 8px; min-width: 420px; }
  `],
})
export class ComicFormDialogComponent implements OnInit {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<ComicFormDialogComponent>);
  private publisherService = inject(PublisherService);
  private authorService = inject(AuthorService);
  data: Comic | null = inject(MAT_DIALOG_DATA);

  publishers: Publisher[] = [];
  authors: Author[] = [];

  genres = [
    { value: 'ACTION',    label: 'Action' },
    { value: 'ADVENTURE', label: 'Adventure' },
    { value: 'COMEDY',    label: 'Comedy' },
    { value: 'FANTASY',   label: 'Fantasy' },
    { value: 'HORROR',    label: 'Horror' },
    { value: 'ROMANCE',   label: 'Romance' },
    { value: 'SCI_FI',    label: 'Sci-Fi' },
    { value: 'SUPERHERO', label: 'Superhero' },
  ];

  form = this.fb.group({
    title:       [this.data?.title       ?? '', Validators.required],
    issueNumber: [this.data?.issueNumber ?? null as number | null, Validators.required],
    publisherId: [this.data?.publisher?.id ?? null as number | null, Validators.required],
    authorIds:   [this.data?.authors?.map(a => a.id) ?? [] as number[]],
    genre:       [this.data?.genre       ?? ''],
    price:       [this.data?.price       ?? null as number | null, [Validators.required, Validators.min(0.01)]],
    description: [this.data?.description ?? ''],
  });

  ngOnInit() {
    this.publisherService.getAll().subscribe(data => this.publishers = data);
    this.authorService.getAll().subscribe(data => this.authors = data);
  }

  save() {
    if (this.form.invalid) return;
    const v = this.form.value;
    const request: ComicRequest = {
      title:       v.title!,
      issueNumber: v.issueNumber!,
      publisherId: v.publisherId!,
      authorIds:   v.authorIds ?? [],
      genre:       v.genre ?? '',
      price:       v.price!,
      description: v.description ?? '',
    };
    this.dialogRef.close(request);
  }
}
