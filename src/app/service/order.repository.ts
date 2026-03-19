import { Injectable } from "@angular/core";
import {Observable} from "rxjs";
import { Order } from "../model/order.model";
//import { StaticDataSource } from "./static.datasource";
import {RestDataSource} from '../model/rest.datasource';
@Injectable()
export class OrderRepository {
  private orders: Order[] = [];
  private loadedKey?: string;

  constructor(private dataSource: RestDataSource) {}

  loadOrders(userId?: number, forceReload: boolean = false) {
    const key = userId ? `user-${userId}` : "admin";
    if (!forceReload && this.loadedKey === key) {
      return;
    }

    this.loadedKey = key;
    const source = userId
      ? this.dataSource.getOrdersForUser(userId)
      : this.dataSource.getOrders();

    source.subscribe(orders => this.orders = orders);
  }

  getOrders(userId?: number): Order[] {
    const key = userId ? `user-${userId}` : "admin";
    if (this.loadedKey !== key) {
      this.loadOrders(userId);
    }
    return this.orders;
  }

  saveOrder(order: Order): Observable<Order> {
    this.loadedKey = undefined;
    return this.dataSource.saveOrder(order);
  }

  updateOrder(order: Order) {
    this.dataSource.updateOrder(order).subscribe(order => {
      this.orders.splice(this.orders.
      findIndex(o => o.id == order.id), 1, order);
    });
  }
  deleteOrder(id: number) {
    this.dataSource.deleteOrder(id).subscribe(order => {
      this.orders.splice(this.orders.findIndex(o => id == o.id), 1);
    });
  }

}
