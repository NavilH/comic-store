import { Component, inject, OnInit } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatCardModule } from '@angular/material/card';
import { Author } from '../../models/author.model';
import { AuthorService } from '../../services/author.service';
import { AuthService } from '../../auth/auth.service';
import { AuthorFormDialogComponent } from './author-form-dialog.component';

@Component({
  selector: 'app-authors',
  imports: [MatTableModule, MatButtonModule, MatIconModule, MatTooltipModule, MatCardModule],
  templateUrl: './authors.component.html',
  styleUrl: './authors.component.scss',
})
export class AuthorsComponent implements OnInit {
  private dialog = inject(MatDialog);
  private authorService = inject(AuthorService);
  auth = inject(AuthService);

  displayedColumns = ['name', 'actions'];
  authors: Author[] = [];

  ngOnInit() {
    this.load();
  }

  load() {
    this.authorService.getAll().subscribe(data => this.authors = data);
  }

  openAdd() {
    const ref = this.dialog.open(AuthorFormDialogComponent, { data: null });
    ref.afterClosed().subscribe(req => {
      if (req) this.authorService.create(req).subscribe(() => this.load());
    });
  }

  openEdit(author: Author) {
    const ref = this.dialog.open(AuthorFormDialogComponent, { data: { ...author } });
    ref.afterClosed().subscribe(req => {
      if (req) this.authorService.update(author.id, req).subscribe(() => this.load());
    });
  }

  delete(id: number) {
    this.authorService.delete(id).subscribe(() => this.load());
  }
}
