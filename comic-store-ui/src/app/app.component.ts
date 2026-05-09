import { Component, inject } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from './auth/auth.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, MatSidenavModule, MatToolbarModule, MatListModule, MatIconModule, MatButtonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  auth = inject(AuthService);
  router = inject(Router);

  navItems = [
    { label: 'Dashboard',  icon: 'dashboard',     route: '/dashboard' },
    { label: 'Comics',     icon: 'menu_book',      route: '/comics' },
    { label: 'Inventory',  icon: 'inventory_2',    route: '/inventory' },
    { label: 'Sales',      icon: 'point_of_sale',  route: '/sales' },
    { label: 'Publishers', icon: 'business',       route: '/publishers' },
    { label: 'Authors',    icon: 'people',         route: '/authors' },
  ];

  get isLoginPage(): boolean {
    return this.router.url === '/login';
  }
}
