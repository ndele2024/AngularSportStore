import { Injectable } from "@angular/core";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import { Observable } from "rxjs";
import { Product } from "./product.model";
import { Order } from "./order.model";
import {AuthResponse, User} from "./user.model";
import {StoredCart} from "./cart.model";
import { environment } from "../../environments/environment";

@Injectable()
export class RestDataSource {
  baseUrl: string;
  auth_token? : string;

  constructor(private http: HttpClient) {
    // URL relative : le meme bundle fonctionne en local derriere le proxy de
    // « ng serve » et en production derriere nginx, sans recompilation.
    this.baseUrl = `${environment.apiUrl}/`;
  }

  getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.baseUrl + "products");
  }

  saveOrder(order: Order): Observable<Order> {
    return this.http.post<Order>(this.baseUrl + "orders", order, this.getOptions());
  }

  downloadInvoice(orderId: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}orders/${orderId}/invoice`, {
      ...this.getOptions(),
      responseType: "blob"
    });
  }

  authenticate(user: string, pass: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      this.baseUrl + "login",
      {
        username: user,
        password: pass
      });
  }

  register(user: User): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(this.baseUrl + "register", user);
  }

  private getOptions() {
    return {
      headers: new HttpHeaders({
        "Authorization": `Bearer<${this.auth_token}>`
      })
    }
  }

  saveProduct(product: Product): Observable<Product> {
    return  this.http.post<Product>(
      this.baseUrl + "products",
      product,
      this.getOptions()
    );
  }

  updateProduct(product: Product): Observable<Product> {
    return this.http.put<Product>(
      `${this.baseUrl}products/${product.id}`,
      product,
      this.getOptions()
    );
  }
  deleteProduct(id: number): Observable<Product> {
    return this.http.delete<Product>(
      `${this.baseUrl}products/${id}`,
      this.getOptions()
    );
  }

  getOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(
      this.baseUrl + "orders",
      this.getOptions()
    );
  }

  getOrdersForUser(userId: number): Observable<Order[]> {
    return this.http.get<Order[]>(
      `${this.baseUrl}orders`,
      {
        ...this.getOptions(),
        params: new HttpParams().set("userId", userId)
      }
    );
  }

  deleteOrder(id: number): Observable<Order> {
    return this.http.delete<Order>(
      `${this.baseUrl}orders/${id}`,
      this.getOptions()
    );
  }
  updateOrder(order: Order): Observable<Order> {
    return this.http.put<Order>(
      `${this.baseUrl}orders/${order.id}`,
      order,
      this.getOptions()
    );
  }

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(
      `${this.baseUrl}users`,
      this.getOptions()
    );
  }

  updateUserCart(userId: number, cart: StoredCart): Observable<User> {
    return this.http.patch<User>(
      `${this.baseUrl}users/${userId}`,
      { cart },
      this.getOptions()
    );
  }

  updateUserProfile(userId: number, user: Partial<User>): Observable<User> {
    return this.http.patch<User>(
      `${this.baseUrl}users/${userId}`,
      user,
      this.getOptions()
    );
  }


}
