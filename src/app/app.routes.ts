import { Routes } from '@angular/router';
import {StoreComponent} from './store/store.component';
import {CartDetailComponent} from './cart-detail/cart-detail.component';
import {CheckoutComponent} from './checkout/checkout.component';
import {StoreFirstGuard} from './guard/store-firt.guard';
import {AdminComponent} from './admin/admin.component';
import {AuthenticationComponent} from './authentication/authentication.component';
import {AuthGuard} from './guard/auth.guard';
import {ProductEditorComponent} from './product-editor/product-editor.component';
import {ProductTableComponent} from './product-table/product-table.component';
import {OrderTableComponent} from './order-table/order-table.component';

export const routes: Routes = [
  { path: "", redirectTo: 'StoreComponent', pathMatch: "full" },
  { path: "store", component: StoreComponent, canActivate:[StoreFirstGuard]},
  { path: "cart", component: CartDetailComponent, canActivate:[StoreFirstGuard] },
  { path: "checkout", component: CheckoutComponent, canActivate:[StoreFirstGuard] },
  {path: "admin", component: AdminComponent, canActivate:[AuthGuard],
    children : [
      { path: "products/:mode/:id", component: ProductEditorComponent },
      { path: "products/:mode", component: ProductEditorComponent },
      { path: "products", component: ProductTableComponent },
      { path: "orders", component: OrderTableComponent },
      { path: "**", redirectTo: "products" }
    ]
  },

  {path: "auth", component: AuthenticationComponent, canActivate:[StoreFirstGuard]},
  { path: "**", redirectTo: "/store" }
];
