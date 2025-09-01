import { Injectable } from "@angular/core";
import {from, Observable} from "rxjs";
import { Order } from "./order.model";
import { StaticDataSource } from "./static.datasource";
@Injectable()
export class OrderRepository {
  private orders: Order[] = [];

  constructor(private order: Order, private datasource: StaticDataSource) {}

  getOrders(): Order[] {
    return this.orders;
  }

  saveOrder(order: Order): Observable<Order> {
    return this.datasource.saveOrder(order);
  }

}
