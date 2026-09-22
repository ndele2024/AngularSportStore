import {Component, inject, IterableDiffer, IterableDiffers} from '@angular/core';
import {MatCheckbox} from '@angular/material/checkbox';
import {FormsModule} from '@angular/forms';
import {
  MatCell, MatCellDef,
  MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow, MatHeaderRowDef, MatNoDataRow,
  MatRow, MatRowDef,
  MatTable, MatTableDataSource
} from '@angular/material/table';
import {MatButton} from '@angular/material/button';
import {Order} from '../model/order.model';
import {OrderRepository} from '../service/order.repository';
import {CurrencyPipe, DatePipe} from '@angular/common';

@Component({
  selector: 'app-order-table',
  imports: [
    MatCheckbox,
    FormsModule,
    MatTable,
    MatColumnDef,
    MatHeaderCell,
    MatCell,
    MatHeaderRow,
    MatRow,
    MatHeaderCellDef,
    MatCellDef,
    MatHeaderRowDef,
    MatRowDef,
    MatButton,
    MatNoDataRow,
    CurrencyPipe,
    DatePipe
  ],
  templateUrl: './order-table.component.html',
  standalone: true,
  styleUrl: './order-table.component.css'
})
export class OrderTableComponent {
  private repository = inject(OrderRepository);
  colsAndRows: string[] = ['customer', 'contact', 'createdAt', 'cart_q', 'payment', 'total', 'status', 'buttons'];
  dataSource = new MatTableDataSource<Order>(this.repository.getOrders());
  differ: IterableDiffer<Order>;

  constructor(differs: IterableDiffers) {
    this.differ = differs.find(this.repository.getOrders()).create();
    this.dataSource.filter = "true";
    this.dataSource.filterPredicate = (order, include) => {
      return order.status !== "LIVRE" || include.toString() == "true"
    };
  }

  get includeShipped(): boolean {
    return this.dataSource.filter == "true";
  }

  set includeShipped(include: boolean) {
    this.dataSource.filter = include.toString()
  }

  markAsDelivered(order: Order) {
    order.shipped = true;
    order.status = "LIVRE";
    this.repository.updateOrder(order);
  }

  delete(id: number) {
    this.repository.deleteOrder(id);
  }

  ngDoCheck() {
    let changes = this.differ?.diff(this.repository.getOrders());
    if (changes != null) {
      this.dataSource.data = this.repository.getOrders();
    }
  }

}
