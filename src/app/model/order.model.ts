import { Injectable } from "@angular/core";
import { CartModel } from "./cart.model";
@Injectable()
export class Order {
  public id?: number;
  public userId?: number;
  public username?: string;
  public nom?: string;
  public prenom?: string;
  public adresse?: string;
  public telephone?: string;
  public createdAt?: string;
  public total?: number;
  public itemCount?: number;
  public shipped: boolean = false;
  constructor(public cart: CartModel) { }
  clear() {
    this.id = undefined;
    this.userId = undefined;
    this.username = undefined;
    this.nom = this.prenom = undefined;
    this.adresse = this.telephone = undefined;
    this.createdAt = undefined;
    this.total = this.itemCount = undefined;
    this.shipped = false;
    this.cart.clear();
  }
}
