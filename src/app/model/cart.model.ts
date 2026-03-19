import {Injectable} from '@angular/core';
import {Product} from './product.model';
import {AuthenticationService} from '../service/authentication.service';
import {RestDataSource} from './rest.datasource';

@Injectable()
export class CartModel {
  public lines: CartLine[] = [];
  public itemCount: number = 0;
  public cartPrice: number = 0;

  private guestStorageKey = "sport-store-guest-cart";
  private lastSessionUserId?: number;

  constructor(private auth: AuthenticationService,
              private dataSource: RestDataSource) {
    this.restoreCart(this.readGuestCart());
    this.auth.changes.subscribe(session => {
      const currentSessionUserId = session?.user?.id;
      if (currentSessionUserId === this.lastSessionUserId) {
        return;
      }

      this.lastSessionUserId = currentSessionUserId;
      if (session?.user) {
        const mergedCart = this.mergeCarts(session.user.cart, this.readGuestCart());
        this.restoreCart(mergedCart);
        this.clearGuestCart();
        this.persist();
      } else {
        this.restoreCart(this.readGuestCart());
      }
    });
  }

  addLine(product: Product, quantity: number = 1) {
    let line = this.lines.find(line => line.product.id == product.id);
    if (line != undefined) {
      line.quantity += quantity;
    } else {
      this.lines.push(new CartLine(product, quantity));
    }
    this.recalculate();
  }

  updateQuantity(product: Product, quantity: number) {
    let line = this.lines.find(line => line.product.id == product.id);
    if (line != undefined) {
      line.quantity = Number(quantity);
      if (line.quantity <= 0) {
        this.removeLine(product.id ?? 0);
        return;
      }
    }
    this.recalculate();
  }

  removeLine(id: number) {
    let index = this.lines.findIndex(line => line.product.id == id);
    if (index > -1) {
      this.lines.splice(index, 1);
      this.recalculate();
    }
  }
  clear() {
    this.lines = [];
    this.itemCount = 0;
    this.cartPrice = 0;
    this.persist();
  }

  private recalculate() {
    this.itemCount = 0;
    this.cartPrice = 0;
    this.lines.forEach(l => {
      this.itemCount += l.quantity;
      this.cartPrice += l.lineTotal;
    })
    this.persist();
  }

  private persist() {
    const cart = this.toStoredCart();
    const user = this.auth.currentUser;

    if (user?.id) {
      this.auth.patchCurrentUser({ cart });
      this.dataSource.updateUserCart(user.id, cart).subscribe({
        next: updatedUser => this.auth.patchCurrentUser(updatedUser),
        error: () => { }
      });
      return;
    }

    localStorage.setItem(this.guestStorageKey, JSON.stringify(cart));
  }

  private restoreCart(cart?: StoredCart) {
    this.lines = (cart?.lines ?? []).map(line => new CartLine(line.product, line.quantity));
    this.itemCount = 0;
    this.cartPrice = 0;
    this.lines.forEach(line => {
      this.itemCount += line.quantity;
      this.cartPrice += line.lineTotal;
    });
  }

  private toStoredCart(): StoredCart {
    return {
      lines: this.lines.map(line => ({
        product: line.product,
        quantity: line.quantity
      })),
      itemCount: this.itemCount,
      cartPrice: this.cartPrice
    };
  }

  private readGuestCart(): StoredCart | undefined {
    const rawCart = localStorage.getItem(this.guestStorageKey);
    return rawCart ? JSON.parse(rawCart) : undefined;
  }

  private clearGuestCart() {
    localStorage.removeItem(this.guestStorageKey);
  }

  private mergeCarts(userCart?: StoredCart, guestCart?: StoredCart): StoredCart | undefined {
    const sourceLines = [...(userCart?.lines ?? [])];

    (guestCart?.lines ?? []).forEach(guestLine => {
      const existingLine = sourceLines.find(line => line.product.id === guestLine.product.id);
      if (existingLine) {
        existingLine.quantity += guestLine.quantity;
      } else {
        sourceLines.push({ ...guestLine });
      }
    });

    const mergedCart: StoredCart = {
      lines: sourceLines,
      itemCount: 0,
      cartPrice: 0
    };

    mergedCart.lines.forEach(line => {
      mergedCart.itemCount += line.quantity;
      mergedCart.cartPrice += (line.product.price ?? 0) * line.quantity;
    });

    return mergedCart;
  }

}


export class CartLine {
  constructor(public product: Product,
              public quantity: number) {}
  get lineTotal() {
    return this.quantity * (this.product.price ?? 0);
  }
}

export interface StoredCartLine {
  product: Product;
  quantity: number;
}

export interface StoredCart {
  lines: StoredCartLine[];
  itemCount: number;
  cartPrice: number;
}
