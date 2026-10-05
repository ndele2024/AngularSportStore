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
    return this.http.post<Order>(this.baseUrl + "orders", this.toOrderPayload(order), this.getOptions());
  }

  /**
   * Transforme la commande en objet plat avant l'envoi.
   *
   * Le modele de panier injecte porte, a l'execution, les services qu'Angular
   * lui a injectes : « private » n'existe qu'a la compilation, pas dans le
   * JavaScript produit. Le graphe obtenu est circulaire — panier vers service
   * d'authentification vers HttpClient vers injecteur — et JSON.stringify leve
   * une TypeError avant meme que la requete ne parte. Aucun appel n'atteignait
   * donc le serveur, et l'appelant ne voyait qu'une erreur generique.
   *
   * On n'envoie ici que les champs attendus par l'API.
   */
  private toOrderPayload(order: Order) {
    return {
      id: order.id,
      userId: order.userId,
      username: order.username,
      nom: order.nom,
      prenom: order.prenom,
      adresse: order.adresse,
      telephone: order.telephone,
      createdAt: order.createdAt,
      total: order.total,
      itemCount: order.itemCount,
      status: order.status,
      paymentStatus: order.paymentStatus,
      paymentMethod: order.paymentMethod,
      paymentReference: order.paymentReference,
      paymentLast4: order.paymentLast4,
      invoiceNumber: order.invoiceNumber,
      deliveredAt: order.deliveredAt,
      shipped: order.shipped,
      cart: {
        itemCount: order.cart?.itemCount,
        cartPrice: order.cart?.cartPrice,
        lines: (order.cart?.lines ?? []).map(ligne => ({
          product: {
            id: ligne.product?.id,
            name: ligne.product?.name,
            category: ligne.product?.category,
            description: ligne.product?.description,
            price: ligne.product?.price,
            imageUrl: ligne.product?.imageUrl
          },
          quantity: ligne.quantity
        }))
      }
    };
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
      this.toOrderPayload(order),
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
