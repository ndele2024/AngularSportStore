import { Routes } from '@angular/router';
import {StoreComponent} from './store/store.component';
import {CartDetailComponent} from './cart-detail/cart-detail.component';
import {CheckoutComponent} from './checkout/checkout.component';
import {StoreFirstGuard} from './store-firt.guard';
import {AdminComponent} from './admin/admin.component';
import {AuthenticationComponent} from './authentication/authentication.component';
import {AuthGuard} from './admin/auth.guard';

export const routes: Routes = [
  { path: "", redirectTo: 'StoreComponent', pathMatch: "full" },
  { path: "store", component: StoreComponent, canActivate:[StoreFirstGuard]},
  { path: "cart", component: CartDetailComponent, canActivate:[StoreFirstGuard] },
  { path: "checkout", component: CheckoutComponent, canActivate:[StoreFirstGuard] },
  {path: "admin", component: AdminComponent, canActivate:[AuthGuard]},
  {path: "auth", component: AuthenticationComponent, canActivate:[StoreFirstGuard]},
  { path: "**", redirectTo: "/store" }
];
