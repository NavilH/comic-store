import { Component, inject, OnInit } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatCardModule } from '@angular/material/card';
import { Publisher } from '../../models/publisher.model';
import { PublisherService } from '../../services/publisher.service';
import { AuthService } from '../../auth/auth.service';
import { PublisherFormDialogComponent } from './publisher-form-dialog.component';

@Component({
  selector: 'app-publishers',
  imports: [MatTableModule, MatButtonModule, MatIconModule, MatTooltipModule, MatCardModule],
  templateUrl: './publishers.component.html',
  styleUrl: './publishers.component.scss',
})
export class PublishersComponent implements OnInit {
  private dialog = inject(MatDialog);
  private publisherService = inject(PublisherService);
  auth = inject(AuthService);

  displayedColumns = ['name', 'country', 'actions'];
  publishers: Publisher[] = [];

  ngOnInit() {
    this.load();
  }

  load() {
    this.publisherService.getAll().subscribe(data => this.publishers = data);
  }

  openAdd() {
    const ref = this.dialog.open(PublisherFormDialogComponent, { data: null });
    ref.afterClosed().subscribe(req => {
      if (req) this.publisherService.create(req).subscribe(() => this.load());
    });
  }

  openEdit(publisher: Publisher) {
    const ref = this.dialog.open(PublisherFormDialogComponent, { data: { ...publisher } });
    ref.afterClosed().subscribe(req => {
      if (req) this.publisherService.update(publisher.id, req).subscribe(() => this.load());
    });
  }

  delete(id: number) {
    this.publisherService.delete(id).subscribe(() => this.load());
  }
}
