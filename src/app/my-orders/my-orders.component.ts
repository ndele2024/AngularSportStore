import {Component, inject} from '@angular/core';
import {CurrencyPipe, DatePipe} from '@angular/common';
import {RouterLink} from '@angular/router';
import {AuthenticationService} from '../service/authentication.service';
import {OrderRepository} from '../service/order.repository';
import {Order} from '../model/order.model';

@Component({
  selector: 'app-my-orders',
  imports: [
    CurrencyPipe,
    DatePipe,
    RouterLink
  ],
  templateUrl: './my-orders.component.html',
  standalone: true,
  styleUrl: './my-orders.component.css'
})
export class MyOrdersComponent {
  private auth = inject(AuthenticationService);
  private orderRepo = inject(OrderRepository);

  isDownloading = false;

  constructor() {
    const userId = this.auth.currentUser?.id;
    if (userId) {
      this.orderRepo.loadOrders(userId, true);
    }
  }

  get orders(): Order[] {
    const userId = this.auth.currentUser?.id;
    return userId
      ? [...this.orderRepo.getOrders(userId)]
        .sort((left, right) => (right.createdAt ?? "").localeCompare(left.createdAt ?? ""))
      : [];
  }

  downloadInvoice(order: Order) {
    if (!order.id || this.isDownloading) {
      return;
    }

    this.isDownloading = true;
    this.orderRepo.downloadInvoice(order.id).subscribe({
      next: blob => {
        const url = window.URL.createObjectURL(blob);
        const anchor = document.createElement("a");
        anchor.href = url;
        anchor.download = `${order.invoiceNumber ?? `facture-${order.id}`}.pdf`;
        anchor.click();
        window.URL.revokeObjectURL(url);
        this.isDownloading = false;
      },
      error: () => {
        this.isDownloading = false;
      }
    });
  }
}
