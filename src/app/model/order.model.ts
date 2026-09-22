import { Injectable } from "@angular/core";
import { CartModel } from "./cart.model";

export type OrderStatus = "EN_TRAITEMENT" | "LIVRE";
export type PaymentStatus = "EN_ATTENTE" | "PAYE";

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
  public status: OrderStatus = "EN_TRAITEMENT";
  public paymentStatus: PaymentStatus = "EN_ATTENTE";
  public paymentMethod?: string;
  public paymentReference?: string;
  public paymentLast4?: string;
  public invoiceNumber?: string;
  public deliveredAt?: string;
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
    this.status = "EN_TRAITEMENT";
    this.paymentStatus = "EN_ATTENTE";
    this.paymentMethod = undefined;
    this.paymentReference = undefined;
    this.paymentLast4 = undefined;
    this.invoiceNumber = undefined;
    this.deliveredAt = undefined;
    this.shipped = false;
    this.cart.clear();
  }
}
