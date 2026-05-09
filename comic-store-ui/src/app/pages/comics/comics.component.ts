import { Component, inject, OnInit } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatCardModule } from '@angular/material/card';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { Comic } from '../../models/comic.model';
import { ComicService } from '../../services/comic.service';
import { AuthService } from '../../auth/auth.service';
import { ComicFormDialogComponent } from './comic-form-dialog.component';

@Component({
  selector: 'app-comics',
  imports: [CurrencyPipe, MatTableModule, MatButtonModule, MatIconModule, MatTooltipModule, MatCardModule, MatPaginatorModule],
  templateUrl: './comics.component.html',
  styleUrl: './comics.component.scss',
})
export class ComicsComponent implements OnInit {
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);
  private comicService = inject(ComicService);
  auth = inject(AuthService);

  displayedColumns = ['title', 'issueNumber', 'publisher', 'genre', 'price', 'actions'];
  comics: Comic[] = [];
  page = 0;
  pageSize = 20;
  totalElements = 0;

  genreLabel: Record<string, string> = {
    ACTION: 'Action', ADVENTURE: 'Adventure', COMEDY: 'Comedy',
    FANTASY: 'Fantasy', HORROR: 'Horror', ROMANCE: 'Romance',
    SCI_FI: 'Sci-Fi', SUPERHERO: 'Superhero',
  };

  ngOnInit() {
    this.load();
  }

  load() {
    this.comicService.getAll(this.page, this.pageSize).subscribe(res => {
      this.comics = res.content;
      this.totalElements = res.totalElements;
    });
  }

  onPageChange(event: PageEvent) {
    this.page = event.pageIndex;
    this.pageSize = event.pageSize;
    this.load();
  }

  openAdd() {
    const ref = this.dialog.open(ComicFormDialogComponent, { data: null });
    ref.afterClosed().subscribe(req => {
      if (req) this.comicService.create(req).subscribe(() => this.load());
    });
  }

  openEdit(comic: Comic) {
    const ref = this.dialog.open(ComicFormDialogComponent, { data: comic });
    ref.afterClosed().subscribe(req => {
      if (req) this.comicService.update(comic.id, req).subscribe(() => this.load());
    });
  }

  delete(id: number) {
    this.comicService.delete(id).subscribe({
      next: () => this.load(),
      error: (err) => {
        console.error('Delete comic error:', err);
        const msg = typeof err.error === 'string' && err.error
          ? err.error
          : err.error?.message ?? err.message ?? 'Failed to delete comic';
        this.snackBar.open(msg, 'Dismiss', { duration: 4000 });
      }
    });
  }
}
