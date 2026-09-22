import {Component, inject} from '@angular/core';
import {CurrencyPipe, DatePipe} from '@angular/common';
import {RouterLink} from '@angular/router';
import {AuthenticationService} from '../service/authentication.service';
import {OrderRepository} from '../service/order.repository';
import {Order} from '../model/order.model';
import {CartModel} from '../model/cart.model';

@Component({
  selector: 'app-dashboard',
  imports: [
    CurrencyPipe,
    DatePipe,
    RouterLink
  ],
  templateUrl: './dashboard.component.html',
  standalone: true,
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  private auth = inject(AuthenticationService);
  private orderRepo = inject(OrderRepository);
  cart = inject(CartModel);

  constructor() {
    const userId = this.auth.currentUser?.id;
    if (userId) {
      this.orderRepo.loadOrders(userId, true);
    }
  }

  get isAuthenticated(): boolean {
    return this.auth.authenticated;
  }

  get currentUser() {
    return this.auth.currentUser;
  }

  get recentPurchases(): Order[] {
    return this.userOrders
      .filter(order => order.status === "LIVRE" || order.shipped)
      .sort((left, right) => (right.createdAt ?? "").localeCompare(left.createdAt ?? ""))
      .slice(0, 5);
  }

  get activeOrders(): Order[] {
    return this.userOrders
      .filter(order => order.status !== "LIVRE" && !order.shipped)
      .sort((left, right) => (right.createdAt ?? "").localeCompare(left.createdAt ?? ""));
  }

  private get userOrders(): Order[] {
    const userId = this.auth.currentUser?.id;
    return userId ? this.orderRepo.getOrders(userId) : [];
  }
}
